package de.tudarmstadt.informatik.ukp.athenakp.crawler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;

public class CrawlerToolbeltTest {
	private CrawlerToolbelt crawlerToolbelt = new CrawlerToolbelt();
	private LocalTime correctTime = LocalTime.of(9,0);

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConvertStringToTime() {

		LocalTime crawlerTime = crawlerToolbelt.acl2018ConvertStringToTime("9:00");
		assertEquals(correctTime, crawlerTime);
	}
	@Test
	public void testZeroConvertStringToTime() {
		LocalTime crawlerTime = crawlerToolbelt.acl2018ConvertStringToTime("09:00");
		assertEquals(correctTime, crawlerTime);

	}
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSmallArray() {
		crawlerToolbelt.acl2018ConvertStringToTime("9");
	}
	@Test
	public void testStringToDate(){
		String dayDescription = "12";
		String yearDescription = "1990";
		String monthDescription = "July";

		LocalDate correctDate = LocalDate.of(1990, 7, 12);
		LocalDate convertedDate = crawlerToolbelt.stringToLocalDate(dayDescription, monthDescription, yearDescription);
		assertEquals(correctDate, convertedDate);


	}
}