package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.openstreetmaps;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.StreetMapsController;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Location;

/**
 * Is responsible for mapping our API calls to the Overpass Api
 * @author Julian
 */
public class OpenStreetRequestBuilder {
	private static Logger logger = LogManager.getLogger(OpenStreetRequestBuilder.class);
	private String amenity;
	private Double minLatitude;
	private Double minLongitude;
	private Double maxLatitude;
	private Double maxLongitude;
	private Integer radiusSizeOfInterest;
	private Integer recentResponseCode;
	/**
	 * The long-/latitude parameters concern the concept of a bounding box. Because a GPS is never quite certain where it is
	 * exactly, usually a range of latitudes is returned, emulating a "margin of error".
	 *
	 * @param amenity The form of amenity we are searching in form of a string. Example values include "tree",
	 *                  "restaurant" or "toilet".
	 * @param minLatitude The minimum latitude of the bounding box, -90 to 90
	 * @param minLongitude The minimum longitude of the bounding box, -180 to 180
	 * @param maxLatitude The maximum latitude of the bounding box, -90 to 90
	 * @param maxLongitude The maximum longitude of the bounding box, -180 to 180
	 * @param radiusSizeOfInterest The radius in which the specified amenities are searched, specified in meters
	 */
	public OpenStreetRequestBuilder(String amenity, Double minLatitude, Double minLongitude, Double maxLatitude,
			Double maxLongitude, Integer radiusSizeOfInterest) {
		this.amenity = amenity;
		this.minLatitude = minLatitude;
		this.minLongitude = minLongitude;
		this.maxLatitude = maxLatitude;
		this.maxLongitude = maxLongitude;
		this.radiusSizeOfInterest = radiusSizeOfInterest;
	}

	/**
	 * This method builds a request URL which is used to query the overpass API.
	 * The order of minLat, minLong, maxLat, maxLong can be different for other geo-APIs (like Nominatim) but is the
	 * standard for this API.
	 *
	 * @return The request URL, not null
	 */
	@NotNull
	String buildRequestURL(){
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

	/**
	 * This method uses the generated request URL to obtain an input stream, which it reads, translates
	 * into a JSON Array of data from the Overpass API, and finally resolves this data to a usable
	 * {@link java.util.List list} of {@link Location locations} for
	 * the {@link StreetMapsController}.
	 * We assume the API works and do not test it - what would be the point? Faulty data would be spotted
	 * in other (tested methods)
	 * @throws  IOException in case of connection problems
	 * @return A list of locations, can be null
	 */
	public List<Location> run() throws IOException{
		// instead of wasting 3+ hours on implementing a dynamic conversion from xml to json,
		// the undocumented [out:json]; command works just as well.
		// I Have No Mouth But I Must Scream
		String searchRequestURL = buildRequestURL();

		// Create connection and set basic parameters
		// if this fails, we return null and cry
		URL url = new URL(searchRequestURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		// sourced from Philipp's work with the Semantic Scholar API
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setConnectTimeout(30 * 1000);        // 30s
		connection.setUseCaches(false);                 // Don't cache anything

		connection.setRequestMethod("GET");
		BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
		// starting here we read and resolve the InputStream
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result2 = bis.read();

		while (result2 != -1) {
			buf.write((byte) result2);
			result2 = bis.read();
		}
		// we create a JSON Object out of the buffer
		JSONObject jsonObject = new JSONObject(buf.toString());
		// because of how the Overpass Data is structured, we only care about this part
		JSONArray locations = jsonObject.getJSONArray("elements");
		// after having run the method, we can now call the recentResponseCode to troubleshoot if necessary
		recentResponseCode = connection.getResponseCode();
		// finally, we call resolveJson our JSONArray of locations and return the result
		return resolveJson(locations);
	}

	/**
	 * Builds a list of {@link Location locations} from the JSON data
	 *
	 * @param locations A {@link JSONArray} of nodes in the openStreetMap sense, not null
	 * @throws JSONException in case the JSON was badly formatted or missing key values, though this should not happen
	 * @return A list of locations which are then collected from the API, or null if it was passed null
	 */
	List<Location> resolveJson(JSONArray locations) throws JSONException {
		// this should never happen
		if (locations == null){
			logger.warn("JSONArray of locations was null");
			return null;
		}
		// instantiates a list of locations that will be passed along by the controller method
		List <Location> locationObjects = new ArrayList<>();
		for (int i = 0; i < locations.length(); i++) {
			JSONObject curObject = locations.getJSONObject(i);
			// creates a new location and sets its attributes
			Location curLocation = new Location();
			curLocation.setId(curObject.getLong("id"));
			curLocation.setLongitude(curObject.getDouble("lon"));
			curLocation.setLatitude(curObject.getDouble("lat"));
			curLocation.setType(curObject.getString("type"));
			JSONObject tags = (JSONObject) curObject.get("tags");
			curLocation.setAmenity(tags.getString("amenity"));
			// add it to our list of locations
			locationObjects.add(curLocation);
		}
		return locationObjects;
	}

	// this could sometimes be of interest - e.g. the commented test in openStreetRequestBuilderTest with a real API
	// 	call

	/**
	 * Gets the most recent responseCode, which can be used to check whether a problem occurred API side or sever side
	 * @return The Overpass API's most recent response code
	 */
	public Integer getRecentResponseCode() {
		return recentResponseCode;
	}
}
