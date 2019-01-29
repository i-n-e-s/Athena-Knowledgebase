package de.tudarmstadt.informatik.ukp.athenakp.api;


import de.tudarmstadt.informatik.ukp.athenakp.crawler.OpenStreetMaps.openStreetRequestBuilder;
import org.json.JSONArray;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/openStreetMaps")
public class StreetMapsController {

	@RequestMapping("/byBox/{amenity}")
	public JSONArray returnAmenities(@PathVariable("amenity") String amenity) {
		openStreetRequestBuilder streetRequestBuilder = new openStreetRequestBuilder(amenity, 48.5657094,
				13.4490548, 48.5662416, 13.4501676, 150);
		return streetRequestBuilder.run();
	}
}
