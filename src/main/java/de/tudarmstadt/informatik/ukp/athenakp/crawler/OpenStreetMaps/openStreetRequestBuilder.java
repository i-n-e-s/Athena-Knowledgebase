package de.tudarmstadt.informatik.ukp.athenakp.crawler.OpenStreetMaps;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.Location;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * is responsible for mapping our API calls to the Overpass Api
 * @author Julian
 */
public class openStreetRequestBuilder {


	private String amenity;
	private Double minLatitude;
	private Double minLongitude;
	private Double maxLatitude;
	private Double maxLongitude;
	private Integer radiusSizeOfInterest;
	Integer recentResponseCode;
	/**
	 * @param amenity the form of amenity we are searching from in form of a String. Example values include "tree",
	 *                  "restaurant" or "toilet
	 * The following parameters concern the concept of a bounding box. Because a GPS is never quite certain where it is
	 * exactly, usually a range of latitudes is returned emulating a "margin of error"
	 * @param maxLatitude the maximum Latitude of the bounding box, -90 to 90
	 * @param maxLongitude the maximum Longitude of the bounding box,  -90 to 90
	 * @param minLatitude the minimum Latitude of the bounding box ,  -90 to 90
	 * @param minLongitude the minimum Longitude of the bounding box,  -90 to 90
	 * @param radiusSizeOfInterest the radius in which the specified amenities are searched, specified in meters
 	*/
	public openStreetRequestBuilder(String amenity, Double minLatitude, Double minLongitude, Double maxLatitude,
									Double maxLongitude, Integer radiusSizeOfInterest) {
		this.amenity = amenity;
		this.minLatitude = minLatitude;
		this.minLongitude = minLongitude;
		this.maxLatitude = maxLatitude;
		this.maxLongitude = maxLongitude;
		this.radiusSizeOfInterest = radiusSizeOfInterest;
	}

	/**
	 * @return the request URL, not null
	 *
	 */
	private String buildRequestURL(){
		return "http://overpass-api.de/api/interpreter?data=[out:json];node(" +
				minLatitude.toString() +
				"," +
				minLongitude.toString() +
				"," +
				maxLatitude.toString() +
				"," +
				maxLongitude.toString() +
				");node(around:" +
				radiusSizeOfInterest.toString() +
				")[amenity=" +
				amenity +
				"];out;";
	}

	public List<Location> run() {
		// instead of wasting 3+ hours on implementing a dynamic conversion from xml to json,
		// the undocumented [out:json]; command works just as well.
		// I Have No Mouth But I Must Scream
		String searchRequestURL = buildRequestURL();

		// Create Connection and set basic parameters
		try {
			URL url = new URL(searchRequestURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setConnectTimeout(30 * 1000);        // 30s
			connection.setUseCaches(false);                 // Don't cache anything

			connection.setRequestMethod("GET");

			BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			int result2 = bis.read();
			// sourced from Philipp's work with the Semantic Scholar API
			while (result2 != -1) {
				buf.write((byte) result2);
				result2 = bis.read();
			}
			JSONObject jsonObject = new JSONObject(buf.toString());
			JSONArray locations = jsonObject.getJSONArray("elements");
			recentResponseCode = connection.getResponseCode();
			// return buf.toString();
			System.out.println(locations);
			return resolveJson(locations);
		}catch (IOException e){
			return null;
		}
	}
	private List<Location> resolveJson(JSONArray locations){
		int jsonLength = locations.length();
		List <Location> locationObjects = new ArrayList<>();
		for (int i = 0; i < jsonLength; i++) {
			JSONObject curObject = locations.getJSONObject(i);
			Location curLocation = new Location();
			curLocation.setId(curObject.getLong("id"));
			curLocation.setLon(curObject.getDouble("lon"));
			curLocation.setLat(curObject.getDouble("lat"));
			curLocation.setType(curObject.getString("type"));
			JSONObject tags = (JSONObject) curObject.get("tags");
			curLocation.setAmenity(tags.getString("amenity"));
			locationObjects.add(curLocation);
		}
		return locationObjects;
	}
}


