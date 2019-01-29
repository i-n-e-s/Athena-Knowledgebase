package de.tudarmstadt.informatik.ukp.athenakp.api;


import de.tudarmstadt.informatik.ukp.athenakp.crawler.OpenStreetMaps.openStreetRequestBuilder;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Location;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/openStreetMaps")
public class StreetMapsController {

	@RequestMapping("/byBox/{amenity}/radiusInMeter/{radius}")
	public List<Location> returnAmenities(@PathVariable("amenity") String amenity, @PathVariable("radius") Integer radius) {
		openStreetRequestBuilder streetRequestBuilder = new openStreetRequestBuilder(amenity, 48.5657094,
				13.4490548, 48.5662416, 13.4501676, radius);
		return streetRequestBuilder.run();
	}
}
