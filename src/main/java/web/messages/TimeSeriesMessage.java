package web.messages;

import java.util.Date;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import event.EventSource;
import model.MetaData;
import model.TimeSeriesData;

public class TimeSeriesMessage
implements EventSource{
	
	@SerializedName("Meta Data")
	MetaData metadata;
	
	Map<Date, TimeSeriesData> timeseriesData;
	
	public TimeSeriesMessage() {}
	
	public TimeSeriesMessage(MetaData metadata, Map<Date, TimeSeriesData> timeseriesData) {
		this.metadata = metadata;
		this.timeseriesData = timeseriesData;
	}


	/**
	 * @return the metadata
	 */
	public MetaData getMetadata() {
		return metadata;
	}


	/**
	 * @return the timeseriesData
	 */
	public Map<Date, TimeSeriesData> getTimeseriesData() {
		return timeseriesData;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TimeSeriesMessage [metadata=" + metadata 
				+ ", timeseriesData=" + timeseriesData + "]";
	}
}
