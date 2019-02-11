package de.tudarmstadt.informatik.ukp.athenakp.crawler.OpenStreetMaps;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.Location;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
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
	private Integer recentResponseCode;
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
	 * this method builds a request URL which is used to query the overpass API
	 * the order of minLat, minLong, maxLat, maxLong can be different for other geo-APIs (like Nominatim) but is the
	 * standard for this API
	 * @return the request URL, not null
	 *
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
	 * This method uses the generated Request URL to obtain an input Stream, which it reads, translates it
	 * into a JSON Array of Data from the Overpass API and finally resolves this data to a usable List of Locations for
	 * the StreetMapsController
	 * @return a list of Locations
	 */
	public List<Location> run() throws IOException{
		// instead of wasting 3+ hours on implementing a dynamic conversion from xml to json,
		// the undocumented [out:json]; command works just as well.
		// I Have No Mouth But I Must Scream
		String searchRequestURL = buildRequestURL();

		// Create Connection and set basic parameters
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
	 * Builds a list of Locations from the JSON data
	 * @param locations a JSONArray of nodes in the openStreetMap sense, not null
	 * @return a list of Locations which are then collected from the API
	 */
	 List<Location> resolveJson(JSONArray locations) throws JSONException {
	 	// this should never happen
	 	if (locations == null){
	 		System.out.println("JSONArray of locations was null");
	 		return null;
		}
		// instantiates a list of Locations that will be passed along by the controller method
		List <Location> locationObjects = new ArrayList<>();
		for (int i = 0; i < locations.length(); i++) {
			JSONObject curObject = locations.getJSONObject(i);
			// creates a new Location and sets its attributes
			Location curLocation = new Location();
			curLocation.setId(curObject.getLong("id"));
			curLocation.setLongitude(curObject.getDouble("lon"));
			curLocation.setLatitude(curObject.getDouble("lat"));
			curLocation.setType(curObject.getString("type"));
			JSONObject tags = (JSONObject) curObject.get("tags");
			curLocation.setAmenity(tags.getString("amenity"));
			// add it to our list of Locations
			locationObjects.add(curLocation);
		}
		return locationObjects;
	}

	// this could sometimes be of interest - e.g. the commented test in openStreetRequestBuilderTest with a real API
	// 	call
	public Integer getRecentResponseCode() {
		return recentResponseCode;
	}
}


