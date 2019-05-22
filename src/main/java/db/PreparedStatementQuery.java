package db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatementQuery 
extends AbstractDBQuery{
	private PreparedStatement pstmt;

	@SuppressWarnings("unused")
	private PreparedStatementQuery() {}
	public PreparedStatementQuery(String pstmtString){
		super();
		conn = getConnection();
		try {
			this.pstmt = conn.prepareStatement(pstmtString);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}
	
	public PreparedStatement getStatement() {
		return pstmt;
	}
	@Override
	public void execute() {
		try {
			if(pstmt != null) {
				int result = pstmt.executeUpdate();
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
