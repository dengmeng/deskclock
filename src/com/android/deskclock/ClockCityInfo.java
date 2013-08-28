package com.android.deskclock;

public class ClockCityInfo {

	// the follow constants reference toStringArray()
	public static final int INDEX = 0;

	public static final int WEATHER_ID = 1;

	public static final int TIME_ZONE = 2;

	public static final int CITY_NAME = 3;

	// initial index value not exist in HashMap(timezones.xml)
	private String index = "-1";

	private String weatherID = "";

	private String timeZone;

	private String cityName;

	/**
	 * an empty construct fun
	 */
	public ClockCityInfo() {
	}

	/**
	 * @param index
	 * @param weatherID
	 * @param timeZone
	 * @param cityName
	 */
	public ClockCityInfo(String index, String weatherID, String timeZone,
			String cityName) {
		super();
		this.index = index;
		this.weatherID = weatherID;
		this.timeZone = timeZone;
		this.cityName = cityName;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getWeatherID() {
		return weatherID;
	}

	public void setWeatherID(String weatherID) {
		this.weatherID = weatherID;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	/**
	 * convert ClockCityInfor object to a string array
	 * 
	 * @return String Array
	 */
	public String[] toStringArray() {
		String[] cityArray = new String[4];
		cityArray[INDEX] = this.index;
		cityArray[WEATHER_ID] = this.weatherID;
		cityArray[TIME_ZONE] = this.timeZone;
		cityArray[CITY_NAME] = this.cityName;
		return cityArray;
	}

	@Override
	public boolean equals(Object o) {
		boolean ret = false;
		if (o instanceof ClockCityInfo) {
			String cityName = ((ClockCityInfo) o).getCityName();
			if (null != this.cityName && null != cityName) {
				ret = this.cityName.equals(cityName);
			}
		}
		return ret;
	}

	@Override
	public String toString() {
		return "index:" + index + ",weatherID:" + weatherID + ",timeZone:"
				+ timeZone + ",cityName:" + cityName;
	}

}
