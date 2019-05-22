/**
 * 
 */
package model;

import com.google.gson.annotations.SerializedName;

/**
 * @author george
 *
 */
public class MetaData 
implements Data {
	
	@SerializedName("1. Information")
	private String information;
	
	@SerializedName("2. Symbol")
	private String symbol;
	
	@SerializedName("3. Last Refreshed")
	private String lastRefreshed;
	
	@SerializedName("4. Interval")
	private String interval;
	
	@SerializedName("5. Output Size")
	private String outputSize;
	
	@SerializedName("6. Time Zone")
	private String timeZone;

	/**
	 * 
	 */
	public MetaData() {}

	/**
	 * @return the information
	 */
	public String getInformation() {
		return information;
	}

	/**
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * @return the lastRefreshed
	 */
	public String getLastRefreshed() {
		return lastRefreshed;
	}

	/**
	 * @return the interval
	 */
	public String getInterval() {
		return interval;
	}

	/**
	 * @return the outputSize
	 */
	public String getOutputSize() {
		return outputSize;
	}

	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MetaData [information=" + information + ", symbol=" 
				+ symbol + ", lastRefreshed=" + lastRefreshed
				+ ", interval=" + interval + ", outputSize=" 
				+ outputSize + ", timeZone=" + timeZone + "]";
	}
}
