package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import configuration.ConfigurationUtils;

public class AbstractDBQuery 
implements DBQuery{
	protected static Logger logger = LoggerFactory.getLogger(AbstractDBQuery.class);
	
	private static ConfigurationUtils config;
	private static String dbHost;
	private static String dbName;
	private static String uri;
	
	protected Connection conn;
	
	protected DBCallbackInterface callback;
	
	static {
		config = ConfigurationUtils.getInstance();
		dbHost = config.get("dbHost");
		dbName = config.get("dbName");
		uri = "jdbc:sqlite:"+dbHost+dbName;
	}
	
	public AbstractDBQuery() {}
	
	protected Connection getConnection() {		
		try {
			conn = DriverManager.getConnection(uri);
			logger.info("Connection established with database:"+uri);
		}catch(SQLException e) {
			logger.error("Error occured when trying to establish connection");
		}
		
		return conn;
	}
	
	protected static void close(Connection conn) {
		try {
			conn.close();
			logger.info("Connection closed");
		}catch(SQLException e) {
			logger.error("Error occured when trying to close connection");
		}
	}
	
	@Override
	public void execute() {}

	@Override
	public void registerCallback(DBCallbackInterface callback) {
		this.callback = callback; 
	}
}
