/**
 * 
 */
package event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author george
 *
 */
public class EventRouter {

	private Logger logger = LoggerFactory.getLogger(EventRouter.class);
	private Map<EventType,List<EventConsumer>> eventListeners;
	private Queue<Event> broadcastQueue;
	private BroadcastWorker broadcastWorker;
	/**
	 * 
	 */
	public EventRouter() {
		eventListeners = new ConcurrentHashMap<>();
		broadcastQueue = new ConcurrentLinkedQueue<>();
		broadcastWorker = new BroadcastWorker();
		Thread workerThread = new Thread(broadcastWorker);
		workerThread.setName("Event Router broad cast thread");
		workerThread.start();
	}
	
	public static EventRouter getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	private void broadcast(Event evt) {
		EventType type = evt.getType();
		if(!eventListeners.containsKey(type)) return;
		logger.info("broadcasting event "+ type);
		eventListeners.get(type)
					  .forEach(consumer -> {
						  consumer.consume(evt);
					  });
	}
	
	public void addBroadcast(Event evt) {
		broadcastQueue.add(evt);
	}
	
	public void register(EventType type, EventConsumer consumer) {
		logger.info("Registering consumer:"+consumer.toString() + " for events of type:"+type);
		if(eventListeners.containsKey(type)) {
			eventListeners.get(type)
						  .add(consumer);
			return;
		}
		
		CopyOnWriteArrayList<EventConsumer> lst = new CopyOnWriteArrayList<>();
		lst.add(consumer);
		eventListeners.put(type, lst);
	}
	
	public void unregister(EventType type, EventConsumer consumer) {
		logger.info("Unregistering consumer:"+consumer.toString() + " for events of type:"+type);
		if(!eventListeners.containsKey(type)) return;
		
		List<EventConsumer> removeLst = new ArrayList<>();
		for(EventConsumer c : eventListeners.get(type)) {
			if(c.equals(consumer))
				removeLst.add(c);
		}
		
		List<EventConsumer> consumers = eventListeners.get(type);
		consumers.removeAll(removeLst);
	}
	
	public void stopBroadcasting() {
		broadcastWorker.stop();
	}
	
	private static class LazyHolder{
		public static final EventRouter INSTANCE = new EventRouter();
	}
	
	private class BroadcastWorker
	implements Runnable{
		private Logger logger = LoggerFactory.getLogger(BroadcastWorker.class);
		private volatile boolean isRunning = false;
		@Override
		public void run() {
			logger.info("Starting broadcast worker");
			isRunning = true;
			while(isRunning) {
				if(!broadcastQueue.isEmpty())
					broadcast(broadcastQueue.poll());
				
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
					stop();
				}//sleep for 1 second
			}
		}
		
		public void stop() {
			logger.info("Stopping broadcast worker");
			isRunning = false;
		}
	}
}
