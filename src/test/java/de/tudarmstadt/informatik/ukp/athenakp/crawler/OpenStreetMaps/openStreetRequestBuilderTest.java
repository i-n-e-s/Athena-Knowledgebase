package de.tudarmstadt.informatik.ukp.athenakp.crawler.OpenStreetMaps;

import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

public class openStreetRequestBuilderTest {
	private openStreetRequestBuilder openStreetRequestBuilder;
	@Test
	@Before
	public void setUp() {
		this.openStreetRequestBuilder = new openStreetRequestBuilder("toilets",
				48.5657094, 13.4490548, 48.5662416,
				13.4501676, 150);
	}
	@Test
	public void run() {
		int response;
		openStreetRequestBuilder.run();
		response = openStreetRequestBuilder.recentResponseCode;
		assertEquals(200, response);
	}

	@Test
	public void run1() {
		openStreetRequestBuilder.run();
	}

	@Test
	public void resolveJsonNull() {
		assertNull(openStreetRequestBuilder.resolveJson(null));
	}
}