package db;

public interface DBQuery {
	void execute();
	void registerCallback(DBCallbackInterface callback);
}
