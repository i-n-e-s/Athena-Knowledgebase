package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

public class Location {
	// the id of the location
	private long id;
	// the longitude of the location
	private double longitude;
	// the latitude of the location
	private double latitude;
	// the type of the location
	private String type;
	// the type of amenity the location is (e.g. tree or restaurant)
	private String amenity;

	/**
	 * Gets the location's amenity type
	 * @return the location's amenity type
	 */
	public String getAmenity() {
		return amenity;
	}
	/**
	 * Sets the location's amenity type
	 * @param amenity the location's amenity type
	 */
	public void setAmenity(String amenity) {
		this.amenity = amenity;
	}

	/**
	 * Gets the location's longitude
	 * @return the location's longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * sets the location's longitude
	 * @param longitude the location's longitude (in a range of -180 to 180)
	 */
	public void setLongitude(double longitude) {
		if (longitude > 180 || longitude < -180){
			throw new IllegalArgumentException("value is not a valid longitude");
		}
		this.longitude = longitude;
	}

	/**
	 * gets the location's id
	 * @return the location's id
	 */
	public long getId() {
		return id;
	}

	/**
	 * sets the location's id
	 * @param id the location's id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * gets the location's type
	 * @return the location's type
	 */
	public String getType() {
		return type;
	}

	/**
	 * sets the location's type
	 * @param type the location's type, which is a remnant of the Overpass API - mostly "node"
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * gets the location's latitude
	 * @return the location's latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * sets the location's latitude
	 * @param latitude the location's latitude
	 */
	public void setLatitude(double latitude) {
		if (latitude >90 || latitude < -90){
			throw new IllegalArgumentException("value is not a valid latitude");
		}
		this.latitude = latitude;
	}
}
