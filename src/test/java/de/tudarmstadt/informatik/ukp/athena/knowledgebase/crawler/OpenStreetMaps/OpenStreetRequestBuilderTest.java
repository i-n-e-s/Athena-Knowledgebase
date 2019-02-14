package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.OpenStreetMaps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Location;

public class OpenStreetRequestBuilderTest {
	private OpenStreetRequestBuilder openStreetRequestBuilder;
	@Test
	@Before
	public void setUp() {
		this.openStreetRequestBuilder = new OpenStreetRequestBuilder("toilets",
				48.5657094, 13.4490548, 48.5662416,
				13.4501676, 150);
	}
	// uncomment this only if you actually want to make an API call to Overpass - since we do not test APIs
	//	@Test
	//	public void run() {
	//		int response;
	//		openStreetRequestBuilder.run();
	//		response = openStreetRequestBuilder.getRecentResponseCode();
	//		assertEquals(200, response);
	//	}
	@Test
	// checks whether the function resolveJson actually does just that
	// invalid input is not tested, since we directly feed input from Overpass and should trust it?
	public void resolveJsonCorrectly() throws JSONException {
		JSONArray testArray = new JSONArray("[{\"lon\":13.4501029,\"id\":3708510719,\"type\":\"node\",\"lat\":48.5655149,\"tags\":{\"todo\":\"combine with proper indoor mapping\",\"level\":\"0\",\"amenity\":\"toilets\",\"diaper\":\"yes\"}},{\"lon\":13.4516014,\"id\":3708510720,\"type\":\"node\",\"lat\":48.5668519,\"tags\":{\"todo\":\"combine with proper indoor mapping\",\"level\":\"-1\",\"amenity\":\"toilets\",\"diaper\":\"yes\"}}]\n");
		Location testLocation = openStreetRequestBuilder.resolveJson(testArray).get(0);
		Location correctLocation = new Location();
		correctLocation.setType("node");
		correctLocation.setAmenity("toilets");
		correctLocation.setId(3708510719L);
		correctLocation.setLongitude(13.4501029);
		correctLocation.setLatitude(48.5655149);
		assertEquals(correctLocation.getType(), testLocation.getType());
		assertEquals(correctLocation.getAmenity(), testLocation.getAmenity());
		assertEquals(correctLocation.getId(), testLocation.getId());
		assertEquals(correctLocation.getLatitude(), testLocation.getLatitude(), 0.01);
		assertEquals(correctLocation.getLongitude(), testLocation.getLongitude(), 0.01);

	}
	// what happens when it is passed null (it should not be)
	@Test
	public void resolveJsonNull() {
		try {
			assertNull(openStreetRequestBuilder.resolveJson(null));
		}
		catch (JSONException e){
			System.out.println("JSON EXCEPTION where it does not belong");
			fail();
		}

	}
	@Test(expected = JSONException.class)
	// Here we are expecting a JSONException, since the mandatory "lon" value is missing
	// we do not want incomplete Location classes - they would hold no value
	public void resolveJsonWithMissing() throws JSONException{
		JSONArray testArray = new JSONArray("[{\"id\":3708510719,\"type\":\"node\",\"lat\":48.5655149,\"tags\":{\"todo\":\"combine with proper indoor mapping\",\"level\":\"0\",\"amenity\":\"toilets\",\"diaper\":\"yes\"}},{\"lon\":13.4516014,\"id\":3708510720,\"type\":\"node\",\"lat\":48.5668519,\"tags\":{\"todo\":\"combine with proper indoor mapping\",\"level\":\"-1\",\"amenity\":\"toilets\",\"diaper\":\"yes\"}}]\n");
		openStreetRequestBuilder.resolveJson(testArray);
	}

	@Test(expected = IllegalArgumentException.class)
	// we are testing a longitude that exceeds 180 - which would be invalid
	public void resolveJsonWithWrongLong() throws JSONException{
		JSONArray testArray = new JSONArray("[{\"lon\":187.4501029,\"id\":3708510719,\"type\":\"node\",\"lat\":48.5655149,\"tags\":{\"todo\":\"combine with proper indoor mapping\",\"level\":\"0\",\"amenity\":\"toilets\",\"diaper\":\"yes\"}},{\"lon\":13.4516014,\"id\":3708510720,\"type\":\"node\",\"lat\":48.5668519,\"tags\":{\"todo\":\"combine with proper indoor mapping\",\"level\":\"-1\",\"amenity\":\"toilets\",\"diaper\":\"yes\"}}]\n");
		openStreetRequestBuilder.resolveJson(testArray);
	}
	@Test(expected = IllegalArgumentException.class)
	// testing a latitude that is smaller than -90 - which would be invalid
	public void resolveJsonWithWrongLat() throws JSONException{
		JSONArray testArray = new JSONArray("[{\"lon\":13.4501029,\"id\":3708510719,\"type\":\"node\",\"lat\":-92.5655149,\"tags\":{\"todo\":\"combine with proper indoor mapping\",\"level\":\"0\",\"amenity\":\"toilets\",\"diaper\":\"yes\"}},{\"lon\":13.4516014,\"id\":3708510720,\"type\":\"node\",\"lat\":48.5668519,\"tags\":{\"todo\":\"combine with proper indoor mapping\",\"level\":\"-1\",\"amenity\":\"toilets\",\"diaper\":\"yes\"}}]\n");
		openStreetRequestBuilder.resolveJson(testArray);
	}

	@Test
	public void overpassString(){
		String correctString = "http://overpass-api.de/api/interpreter?data=[out:json];node(48.5657094,13.4490548,48.5662416,13.4501676);node(around:150)[amenity=toilets];out;";
		assertEquals(correctString, openStreetRequestBuilder.buildRequestURL());
	}
}