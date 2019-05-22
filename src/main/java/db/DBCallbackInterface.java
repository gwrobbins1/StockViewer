package db;

import java.sql.ResultSet;

public interface DBCallbackInterface {
	void complete(int result);
	void complete(ResultSet result);
}
