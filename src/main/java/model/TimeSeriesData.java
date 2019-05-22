/**
 * 
 */
package model;

import com.google.gson.annotations.SerializedName;

/**
 * @author george
 *
 */
public class TimeSeriesData 
implements Data {

	@SerializedName("1. open")
	private float open;
	
	@SerializedName("2. high")
	private float high;
	
	@SerializedName("3. low")
	private float low;
	
	@SerializedName("4. close")
	private float close;
	
	@SerializedName("5. volume")
	private float volume;
	/**
	 * 
	 */
	public TimeSeriesData() {}

	/**
	 * @return the open
	 */
	public float getOpen() {
		return open;
	}

	/**
	 * @return the high
	 */
	public float getHigh() {
		return high;
	}

	/**
	 * @return the low
	 */
	public float getLow() {
		return low;
	}

	/**
	 * @return the close
	 */
	public float getClose() {
		return close;
	}
	
	public float getVolume() {
		return volume;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TimeSeriesData [open=" + open + ", high=" 
				+ high + ", low=" + low + ", close=" + close + ", volume="
				+ volume + "]";
	}
}
