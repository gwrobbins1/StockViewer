package model;

import com.google.gson.annotations.SerializedName;

public class SymbolData 
implements Data{

	@SerializedName("1. symbol")
	String symbol;
	
	@SerializedName("2. name")
	String name;
	
	@SerializedName("3. type")
	String type;
	
	@SerializedName("4. region")
	String region;
	
	@SerializedName("5. marketOpen")
	String marketOpen;
	
	@SerializedName("6. marketClose")
	String marketClose;
	
	@SerializedName("7. timezone")
	String timezone;
	
	@SerializedName("8. currency")
	String currency;
	
	@SerializedName("9. matchScore")
	String matchScore;
	
	public SymbolData() {}

	/**
	 * @return the marketClose
	 */
	public String getMarketClose() {
		return marketClose;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}
	
	/**
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @return the marketOpen
	 */
	public String getMarketOpen() {
		return marketOpen;
	}

	/**
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * @return the matchScore
	 */
	public String getMatchScore() {
		return matchScore;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {		
		return symbol + " "
		+ name + " " 
		+ type + " "
		+ region + " " 
		+ marketOpen + " "
		+ marketClose + " "
		+ timezone + " "
		+ currency;
	}
}
