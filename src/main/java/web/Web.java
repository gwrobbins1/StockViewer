/**
 * 
 */
package web;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DBCallbackInterface;
import db.DBManager;

import event.CallbackInterface;
import event.Event;
import event.EventEmitter;
import event.EventSource;
import event.EventType;

/**
 * @author george
 *
 */
public class Web
extends EventEmitter {
	
	private Logger logger = LoggerFactory.getLogger(Web.class);
	
	private Queue<APIRequest> requestQueue = new ConcurrentLinkedQueue<>();
	private Queue<Pair<APIRequest,Thread>> waitingQueue = new ConcurrentLinkedQueue<>();
	
	private RequestWorker worker = new RequestWorker();
	
	private static DBManager db;
	
	/** Max time to wait for request to complete. */
	private final static long MAX_WAIT = 60000l;
	private final static int DAILY_RATE_LIMIT = 500;
	private final static int MINUTE_RATE_LIMIT = 5;
	private final static long MINUTE_MILLISECONDS = 60000l;
	private final static long DAILY_MILLISECONDS = 86400000l;
	
	private static List<Long> minuteRequestTimes;
	private static List<Long> dailyRequestTimes;
	
	private static Map<APIRequest, Timer> requestToTimer;
	
	static {
		requestToTimer = new ConcurrentHashMap<>();
		minuteRequestTimes = Collections.synchronizedList(new LinkedList<>());
		dailyRequestTimes = Collections.synchronizedList(new LinkedList<>());
		db = DBManager.getInstance();
	}

	private Web() {
		new Thread(worker).start();
	}
	
	public void testDefaultRequest() {
		requestQueue.add(new APIRequest(FunctionType.TIME_SERIES_INTRADAY,
										"MSFT",
										IntervalType.FIVE_MIN));
	}
	
	public void loadDailyCounter() {
		db.getDailyCounter(new DBCallbackInterface() {
			@Override
			public void complete(int result) {}

			@Override
			public void complete(ResultSet result) {
				try {
					while(result.next()) {
						dailyRequestTimes.add(result.getLong("TIME_MS"));
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				
				logger.info("loaded daily counter times. There are "+dailyRequestTimes.size()+" times.");
			}
		});
	}
	
	public void addNewRequest(APIRequest request) {
		requestQueue.add(request);
	}
	
	private void watch(APIRequest request, Thread thread) {		
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				request.cancel();
				thread.interrupt();
				logger.error("Times up for request "+request);
				
				emit(new Event() {
					@Override
					public EventType getType() {
						return EventType.REQUEST_TIMEOUT;
					}

					@Override
					public EventSource getSource() {
						return new EventSource() {};
					}
					
				});
			}
		}, MAX_WAIT);
		
		requestToTimer.put(request, timer);
		logger.info("Watching request: "+request.toString() +"\nWill cancel request in "+MAX_WAIT);
	}
	
	public void sendWhenReady(APIRequest request) {
		Thread reqThread = new Thread(request);
		
		if(!isLimitsViolated()) {
			reqThread.start();
			watch(request,reqThread);
			trackRequestTime(System.currentTimeMillis());
		}else {
			waitingQueue.add(new Pair<>(request,reqThread));
		}
	}
	
	public void stopWatching(APIRequest request) {
		if(requestToTimer.containsKey(request)) {
			Timer timer = requestToTimer.remove(request);
			timer.cancel();
			logger.info("Canceled watchdog for request "+ request);
		}else
			logger.error("Could not find watchdog for request "+ request);
	}
	
	private void trackRequestTime(long time) {
		minuteRequestTimes.add(time);
		dailyRequestTimes.add(time);
		db.addDailyCounter(time,new DBCallbackInterface () {

			@Override
			public void complete(int result) {
				logger.info("Added daily request time to database. Result:"+result);
			}

			@Override
			public void complete(ResultSet result) {}
		});
	}

	private boolean isLimitsViolated() {
		return isMinuteRateViolated() || isDailyRateViolated();
	}

	private boolean isMinuteRateViolated() {
		boolean violated = minuteRequestTimes.size() >= MINUTE_RATE_LIMIT;
		if(violated)
			logger.debug("Minute rate has been violated");
		return violated;
	}
	
	private boolean isDailyRateViolated() {
		boolean violated = dailyRequestTimes.size() >= DAILY_RATE_LIMIT;
		if(violated)
			logger.info("Daily rate has been violated");
		return violated;
	}
	
	public void stopWorker() {
		logger.info("Stopping web request worker");
		worker.stop();
	}
	
	public static Web getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	private static class LazyHolder{
		public static final Web INSTANCE = new Web();
	}
	
	private class RequestWorker
	implements Runnable{
		private volatile boolean isRunning = false;
		private static final long REQUEST_TIME_PURGE_INTERVAL = 1000l;// 1 second
		private volatile long lastPurgeTime = System.currentTimeMillis();

		@Override
		public void run() {
			isRunning = true;
			while(isRunning) {
				if(!requestQueue.isEmpty()) {
					APIRequest request = requestQueue.poll();
					request.registerCallback(new CallbackInterface() {
						@Override
						public void complete() {
							logger.info("Request complete "+request.toString());
							stopWatching(request);
						}

						@Override
						public void failed() {
							logger.error("Request Failed "+request.toString());
							stopWatching(request);
						}
					});
					
					sendWhenReady(request);
				}
				
				if(!waitingQueue.isEmpty() && !isLimitsViolated()) {
					Pair<APIRequest,Thread> pair = waitingQueue.poll();
					pair.getValue().start();
					watch(pair.getKey(),pair.getValue());
				}
				
				if((System.currentTimeMillis() - lastPurgeTime) > REQUEST_TIME_PURGE_INTERVAL) {
					logger.debug("Purging request times");
					lastPurgeTime = System.currentTimeMillis();
					for(ListIterator<Long> it = (ListIterator<Long>) minuteRequestTimes.iterator(); 
							it.hasNext();) {
						
						long timeOfRequest = it.next();
						if((System.currentTimeMillis() - timeOfRequest) > MINUTE_MILLISECONDS) {
							logger.info("Purging time:"+timeOfRequest+" from minuteRequestTimes");
							it.remove();
						}
					}
					
					for(ListIterator<Long> it = (ListIterator<Long>) dailyRequestTimes.iterator(); 
							it.hasNext();) {
						
						long timeOfRequest = it.next();
						if((System.currentTimeMillis() - timeOfRequest) > DAILY_MILLISECONDS) {
							logger.info("Purging time:"+timeOfRequest+" from dailyRequestTimes");
							db.removeDailyCounter(timeOfRequest,new DBCallbackInterface() {

								@Override
								public void complete(int result) {
									logger.info("Removed time from database. Result:"+result);
								}

								@Override
								public void complete(ResultSet result) {}
							});
							it.remove();
						}
					}
				}
				
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			}
		}
		
		public void stop() {
			isRunning = false;
		}
	}
	
	public final class Pair<K,V>{
		private K k;
		private V v;
		
		public Pair(K k, V v) {
			this.k = k;
			this.v = v;
		}
		
		public K getKey() {
			return k;
		}
		
		public V getValue() {
			return v;
		}
	}
}
