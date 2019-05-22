package event;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEventConsumer
implements EventConsumer{

	private Queue<Event> eventQueue;
	private EventWorker worker;
		
	public AbstractEventConsumer() {
		eventQueue = new ConcurrentLinkedQueue<>();
		worker = new EventWorker();
		Thread workerThread = new Thread(worker);
		workerThread.setName(this.getClass().getCanonicalName());
		workerThread.start();
	}
	
	public void stopWorker() {
		worker.stop();
	}
	
	private class EventWorker
	implements Runnable{
		private Logger logger = LoggerFactory.getLogger(EventWorker.class);
		private volatile boolean isRunning = false;
		@Override
		public void run() {
			logger.info("Starting event worker for the event consumer");
			isRunning = true;
			while(isRunning) {
				if(!eventQueue.isEmpty())
					consume(eventQueue.poll());
				
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
					stop();
				}
			}
		}
		
		public void stop() {
			logger.info("Stopping the event worker");
			isRunning = false;
		}
	}
}
