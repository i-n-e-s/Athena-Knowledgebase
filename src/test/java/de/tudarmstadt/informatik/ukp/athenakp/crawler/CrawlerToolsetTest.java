package de.tudarmstadt.informatik.ukp.athenakp.crawler;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class CrawlerToolsetTest {
	private CrawlerToolset crawlerToolset = new CrawlerToolset();
	private LocalTime correctTime = LocalTime.of(9,0);

	@Test
	public void testConvertStringToTime() {

		LocalTime crawlerTime = crawlerToolset.acl2018ConvertStringToTime("9:00");
		assertEquals(correctTime, crawlerTime);
	}
	@Test
	public void testZeroConvertStringToTime() {
		LocalTime crawlerTime = crawlerToolset.acl2018ConvertStringToTime("09:00");
		assertEquals(correctTime, crawlerTime);

	}
	@Test
	public void testSmallArray() {
		LocalTime crawlerTime = crawlerToolset.acl2018ConvertStringToTime("9");
		assertNull(crawlerTime);
	}
	@Test
	public void testStringToDate(){
		String dayDescription = "12";
		String yearDescription = "1990";
		String monthDescription = "July";
		LocalDate correctDate = LocalDate.of(1990, 7, 12);

		LocalDate convertedDate = crawlerToolset.stringToLocalDate(dayDescription, monthDescription, yearDescription);
		assertEquals(correctDate, convertedDate);
	}
	@Test
	public void testDateRange(){
		LocalDate[] correctDateRange = new LocalDate[2];
		correctDateRange[0] = LocalDate.of(2018,7,15);
		correctDateRange[1] = LocalDate.of(2018,7,20);

		String testString = "15-20 July 2018";
		LocalDate[] toolsetDateRange = crawlerToolset.acl2018ConvertStringToDateRange(testString);
		assertEquals(correctDateRange[0],toolsetDateRange[0]);
		assertEquals(correctDateRange[1], toolsetDateRange[1]);

	}
}