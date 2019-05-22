package db;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBQueryFactory {
	private static Logger logger = LoggerFactory.getLogger(DBQueryFactory.class);
	
	public DBQueryFactory() {}
	
	public static PreparedStatementQueryBuilder getPreparedStatementBuilder(String query) {
		return new PreparedStatementQueryBuilder(query);
	}
	
	public static StatementQueryBuilder getStatementBuilder(String query) {
		return new StatementQueryBuilder(query);
	}

	static class PreparedStatementQueryBuilder{
		PreparedStatementQuery pstmt;
		
		@SuppressWarnings("unused")
		private PreparedStatementQueryBuilder() {}
		public PreparedStatementQueryBuilder(String query) {
			 pstmt = new PreparedStatementQuery(query);
		}
		
		public PreparedStatementQueryBuilder set(int index, Object parameter) {
			try {
				if(parameter instanceof Long)
					pstmt.getStatement().setLong(index, (Long)parameter);
			}catch(SQLException e) {
				logger.error(e.getMessage());
			}
			
			return this;
		}
		
		public PreparedStatementQueryBuilder set(DBCallbackInterface callback) {
			pstmt.registerCallback(callback);
			return this;
		}
		
		public PreparedStatementQuery build() {
			return pstmt;
		}
	}
	
	static class StatementQueryBuilder{
		StatementQuery stmt;
		
		@SuppressWarnings("unused")
		private StatementQueryBuilder() {}
		public StatementQueryBuilder(String query) {
			 stmt = new StatementQuery(query);
		}
		
		public StatementQueryBuilder set(DBCallbackInterface callback) {
			stmt.registerCallback(callback);
			return this;
		}
		
		public StatementQuery build() {
			return stmt;
		}
	}
}
