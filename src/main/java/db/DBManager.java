package db;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DBManager {
	private static Logger logger = LoggerFactory.getLogger(DBManager.class);

	private static Queue<AbstractDBQuery> queryQueue;
	private static Worker worker;
	static {
		queryQueue = new ConcurrentLinkedQueue<>();
		worker = new Worker();
	}
	
	private DBManager() {
		new Thread(worker).start();
	}
	
	private static class LazyHolder{
		public static final DBManager INSTANCE = new DBManager();
	}
	
	public static DBManager getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	public void getDailyCounter(DBCallbackInterface callback){
		String query = "SELECT TIME_MS FROM daily_counter;";
		
		StatementQuery pstmt = DBQueryFactory.getStatementBuilder(query)
									    	 .set(callback)
									    	 .build();
			
		queryQueue.add(pstmt);
	}
	
	public void addDailyCounter(long timeMs,DBCallbackInterface callback) {		
		String query = "INSERT INTO daily_counter(TIME_MS) VALUES(?);";
		
		PreparedStatementQuery pstmt = DBQueryFactory.getPreparedStatementBuilder(query)
													 .set(1, (Long)timeMs)
													 .set(callback)
													 .build();
		queryQueue.add(pstmt);
	}
	
	public void removeDailyCounter(long timeMs,DBCallbackInterface callback) {
		String query = "DELETE FROM daily_counter WHERE TIME_MS=?;";
		PreparedStatementQuery pstmt = DBQueryFactory.getPreparedStatementBuilder(query)
				 									 .set(1, (Long)timeMs)
				 									 .set(callback)
				 									 .build();
		queryQueue.add(pstmt);
	}
	
	public void stopWorker() {
		worker.stop();
	}
	
	private static class Worker
	implements Runnable{
		private volatile boolean isRunning = false;

		@Override
		public void run() {
			isRunning = true;
			
			while(isRunning) {
				try {
					Thread.sleep(1);
				}catch(InterruptedException e) {
					logger.info(e.getMessage());
				}
				
				if(!queryQueue.isEmpty()) {
					queryQueue.poll().execute();
				}
			}
		}
		
		public void stop() {
			isRunning = false;
			logger.info("Stopping database worker");
		}
	}
}
