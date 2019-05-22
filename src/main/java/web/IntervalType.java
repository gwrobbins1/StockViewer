package web;

public enum IntervalType {
	ONE_MIN("1min"),
	FIVE_MIN("5min"),
	FIFTEEN_MIN("15min"),
	THIRTY_MIN("30min"),
	SIXTY_MIN("60min");
	
	private final String interval;
	
	private IntervalType(String intervalStr) {
		interval = intervalStr;
	}
	
	public String getInterval() {
		return interval;
	}
}
