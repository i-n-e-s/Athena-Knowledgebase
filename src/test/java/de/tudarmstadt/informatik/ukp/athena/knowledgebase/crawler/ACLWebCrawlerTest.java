package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;


public class ACLWebCrawlerTest {
	private ACLWebCrawler acl18WebParser = new ACLWebCrawler(2018, 2018);
	private Conference crawledConference = acl18WebParser.getConferenceInformation();

	public ACLWebCrawlerTest() throws IOException {
	}


	@Test
	public void getConferenceName() throws IOException {
		Assert.assertEquals("ACL 2018",crawledConference.getName());
	}
	@Test
	public void testConferenceDates(){
		Assert.assertEquals(LocalDate.of(2018,7,15), crawledConference.getBegin());
		Assert.assertEquals(LocalDate.of(2018,7,20), crawledConference.getEnd());
	}
	@Test
	public void testConferenceAddress(){
		Assert.assertEquals("Melbourne Convention and Exhibition Centre", crawledConference.getAddress());
	}
	@Test
	public void testConferenceCountry(){
		Assert.assertEquals("Australia", crawledConference.getCountry());
	}
	@Test
	public void testConferenceCity(){
		Assert.assertEquals("Melbourne", crawledConference.getCity());
	}
}