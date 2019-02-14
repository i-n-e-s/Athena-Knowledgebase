package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.openstreetmaps.OpenStreetRequestBuilder;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Location;

/**
 * The REST API controller for OpenStreetMap data
 * Currently, /openStreetMaps is not mapped, but might be extended in the future
 */
@RestController
@RequestMapping("/openStreetMaps")
public class StreetMapsController {
	/**
	 * Since location data is almost never exact, overpass expects a bounding box (long-/latitude parameters) which symbolises one rough location.
	 * If you have exact data (you shouldn't), simply add or subtract a small value from your longitude and latitude to
	 * create a range.
	 *
	 * @param amenity The type of amenity (e.g. restaurant or even tree)
	 * @param radius The search radius in meters
	 * @param minLatitude The minimum latitude for the bounding box
	 * @param minLongitude The minimum longitude for the bounding box
	 * @param maxLatitude	The maximum latitude for the bounding box
	 * @param maxLongitude	The maximum longitude for the bounding box
	 * @throws ResponseStatusException signals to the user that the API is either unreachable or the request was bad
	 * @return A list of location objects, can be null, though this is unlikely
	 */
	@RequestMapping("/location/minLatitude/{minLatitude}/minLongitude/{minLongitude}/maxLatitude/{maxLatitude}/maxLongitude/{maxLongitude}/amenity/{amenity}/radiusInMeter/{radius}")
	public List<Location> returnAmenities(@PathVariable("amenity") String amenity,
			@PathVariable("radius") Integer radius,
			@PathVariable("minLatitude") Double minLatitude,
			@PathVariable("minLongitude") Double minLongitude,
			@PathVariable("maxLatitude") Double maxLatitude,
			@PathVariable("maxLongitude") Double maxLongitude) {
		OpenStreetRequestBuilder streetRequestBuilder = new OpenStreetRequestBuilder(amenity, minLatitude,
				minLongitude, maxLatitude, maxLongitude, radius);

		//		openStreetRequestBuilder streetRequestBuilder = new openStreetRequestBuilder(amenity, 48.5657094,
		//				13.4490548, 48.5662416, 13.4501676, radius);
		// working example:
		// http://localhost:8080/openStreetMaps/location/minLatitude/48.5657094/minLongitude/13.4490548/maxLatitude/48.5662416/maxLongitude/13.4501676/amenity/restaurant/radiusInMeter/3000
		try{
			return streetRequestBuilder.run();
		} catch (IOException e){
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "IO Exception, this might be due to the Overpass API not being available " +
							"or an incorrect String: \n Are your longitudes and latitudes correct?"
					);
		}
	}
}
