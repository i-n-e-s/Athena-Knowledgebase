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

	public String getAmenity() {
		return amenity;
	}

	public void setAmenity(String amenity) {
		this.amenity = amenity;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		if (longitude > 180 || longitude < -180){
			throw new IllegalArgumentException("value is not a valid longitude");
		}
		this.longitude = longitude;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		if (latitude >90 || latitude < -90){
			throw new IllegalArgumentException("value is not a valid latitude");
		}
		this.latitude = latitude;
	}
}
