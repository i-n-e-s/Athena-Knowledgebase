package de.tudarmstadt.informatik.ukp.athenakp.crawler.OpenStreetMaps;

import org.junit.Test;


import static org.junit.Assert.*;

public class openStreetRequestBuilderTest {
	@Test
	public void run() {
		openStreetRequestBuilder hark = new openStreetRequestBuilder("toilets", 48.5657094, 13.4490548, 48.5662416, 13.4501676, 150);
		int response;
			hark.run();
			response = hark.recentResponseCode;
		assertEquals(200, response);
		System.out.println();
	}
}