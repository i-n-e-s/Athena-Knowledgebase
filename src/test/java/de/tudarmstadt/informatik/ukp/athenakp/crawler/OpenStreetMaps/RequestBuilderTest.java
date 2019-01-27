package de.tudarmstadt.informatik.ukp.athenakp.crawler.OpenStreetMaps;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class RequestBuilderTest {
	@Test
	public void run() {
		RequestBuilder hark = new RequestBuilder("toilets", 48.5657094, 13.4490548, 48.5662416, 13.4501676, 150);
		int response;
		try {
			response = hark.run();
		}catch (IOException e){
			fail();
			response = 0;
		}
		assertEquals(200, response);
		System.out.println();
	}
}