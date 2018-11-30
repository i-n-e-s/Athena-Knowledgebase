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
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSmallArray() {
		crawlerToolset.acl2018ConvertStringToTime("9");
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
}