package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api;


import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.OpenStreetMaps.openStreetRequestBuilder;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Location;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

/**
 * The REST API controller for StreetMap data
 * currently /openStreetMaps is not mapped but might be extended in the future
 */
@RestController
@RequestMapping("/openStreetMaps")
public class StreetMapsController {
	/**
	 *
	 * @param amenity the type of amentiy (e.g. restaurant or even tree)
	 * @param radius the searchradius in meters
	 * Since location data is almost never exact, overpass expects a bounding box which symbolises once rough location
	 * if you have exact data (you shouldn't) simply add or subtract a small value from your Longitude and Latitude to
	 * create a range
	 * @param minLatitude the minimum Latitude for the bounding box
	 * @param minLongitude the minimum Longitude for the bounding box
	 * @param maxLatitude	the maximum Latitude for the bounding box
	 * @param maxLongitude	the maximum Longitude for the bounding box
	 * @return A list of Location Objects
	 */
	@RequestMapping("/location/minLatitude/{minLatitude}/minLongitude/{minLongitude}/maxLatitude/{maxLatitude}/maxLongitude/{maxLongitude}/amenity/{amenity}/radiusInMeter/{radius}")
	public List<Location> returnAmenities(@PathVariable("amenity") String amenity,
										  @PathVariable("radius") Integer radius,
										  @PathVariable("minLatitude") Double minLatitude,
										  @PathVariable("minLongitude") Double minLongitude,
										  @PathVariable("maxLatitude") Double maxLatitude,
										  @PathVariable("maxLongitude") Double maxLongitude) {
		openStreetRequestBuilder streetRequestBuilder = new openStreetRequestBuilder(amenity, minLatitude,
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
