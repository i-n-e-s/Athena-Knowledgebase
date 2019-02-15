package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.Test;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.CrawlerToolset;

public class CrawlerToolsetTest {
	private LocalTime correctTime = LocalTime.of(9,0);

	@Test
	public void testConvertStringToTime() {

		LocalTime crawlerTime = CrawlerToolset.acl2018ConvertStringToTime("9:00");
		assertEquals(correctTime, crawlerTime);
	}
	@Test
	public void testZeroConvertStringToTime() {
		LocalTime crawlerTime = CrawlerToolset.acl2018ConvertStringToTime("09:00");
		assertEquals(correctTime, crawlerTime);

	}
	@Test
	public void testSmallArray() {
		LocalTime crawlerTime = CrawlerToolset.acl2018ConvertStringToTime("9");
		assertNull(crawlerTime);
	}
	@Test
	public void testStringToDate(){
		String dayDescription = "12";
		String yearDescription = "1990";
		String monthDescription = "July";
		LocalDate correctDate = LocalDate.of(1990, 7, 12);

		LocalDate convertedDate = CrawlerToolset.stringToLocalDate(dayDescription, monthDescription, yearDescription);
		assertEquals(correctDate, convertedDate);
	}
	@Test
	public void testDateRange(){
		LocalDate[] correctDateRange = new LocalDate[2];
		correctDateRange[0] = LocalDate.of(2018,7,15);
		correctDateRange[1] = LocalDate.of(2018,7,20);

		String testString = "15-20 July 2018";
		LocalDate[] toolsetDateRange = CrawlerToolset.acl2018ConvertStringToDateRange(testString);
		assertEquals(correctDateRange[0],toolsetDateRange[0]);
		assertEquals(correctDateRange[1], toolsetDateRange[1]);

	}
	@Test
	public void testInvalidDateRange(){
		String testString = "garbladsa";
		LocalDate[] toolsetDateRange = CrawlerToolset.acl2018ConvertStringToDateRange(testString);
		assertNull(toolsetDateRange[0]);
		assertNull(toolsetDateRange[1]);

	}
	@Test
	public void testMonthIndex() {
		String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

		for(int i = 0; i < months.length; i++) {
			assertEquals(i + 1, CrawlerToolset.getMonthIndex(months[i]));
		}

		//invalid input
		assertEquals(-1, CrawlerToolset.getMonthIndex("fdsdfsfdsdfsdfsdfsfds"));
	}
}