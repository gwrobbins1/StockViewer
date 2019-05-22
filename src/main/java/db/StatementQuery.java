package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementQuery 
extends AbstractDBQuery{

	private Statement stmt;
	private String query;

	@SuppressWarnings("unused")
	private StatementQuery() {}
	public StatementQuery(String stmtString){
		super();
		conn = getConnection();
		try {
			this.stmt = conn.createStatement();
			this.query = stmtString;
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}
	
	public Statement getStatement() {
		return stmt;
	}
	@Override
	public void execute() {		
		try {
			if(stmt != null) {
				ResultSet result = stmt.executeQuery(query);
				if(callback != null)
					callback.complete(result);
			}
		}catch(SQLException e) {
			logger.error("Error deleting daily counter table");
		}finally {
			close(conn);
		}
	}
}
