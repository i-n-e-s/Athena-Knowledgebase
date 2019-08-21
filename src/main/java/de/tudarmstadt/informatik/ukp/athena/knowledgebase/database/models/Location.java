package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

public class Location {
	// the id of the location
	private Long id;
	// the Longitude of the location
	private double Longitude;
	// the latitude of the location
	private double latitude;
	// the type of the location
	private String type;
	// the type of amenity the location is (e.g. tree or restaurant)
	private String amenity;

	/**
	 * Gets the location's amenity type
	 * @return The location's amenity type
	 */
	public String getAmenity() {
		return amenity;
	}

	/**
	 * Sets the location's amenity type
	 * @param amenity The location's amenity type
	 */
	public void setAmenity(String amenity) {
		this.amenity = amenity;
	}

	/**
	 * Gets the location's Longitude
	 * @return The location's Longitude
	 */
	public double getLongitude() {
		return Longitude;
	}

	/**
	 * Sets the location's Longitude
	 * @param Longitude The location's Longitude restricted to -180 to 180
	 * @throws IllegalArgumentException if the Longitude was not in range
	 */
	public void setLongitude(double Longitude) {
		if (Longitude > 180 || Longitude < -180){
			throw new IllegalArgumentException("value is not a valid Longitude");
		}
		this.Longitude = Longitude;
	}

	/**
	 * Gets the location's id
	 * @return The location's id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the location's id
	 * @param id The location's id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the location's type
	 * @return The location's type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the location's type
	 * @param type The location's type, which is a remnant of the Overpass API - mostly "node"
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the location's latitude
	 * @return The location's latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Sets the location's latitude
	 * @param latitude The location's latitude, restricted to -90 to 90
	 * @throws IllegalArgumentException if the specified latitude was not in range
	 */
	public void setLatitude(double latitude) {
		if (latitude > 90 || latitude < -90){
			throw new IllegalArgumentException("value is not a valid latitude");
		}
		this.latitude = latitude;
	}
}
