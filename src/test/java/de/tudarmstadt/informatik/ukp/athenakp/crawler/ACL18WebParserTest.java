package de.tudarmstadt.informatik.ukp.athenakp.crawler;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.ACL18WebParser;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;


public class ACL18WebParserTest {
	private ACL18WebParser acl18WebParser = new ACL18WebParser("2018", "2018");
	private Conference crawledConference = acl18WebParser.getConferenceInformation();

	public ACL18WebParserTest() throws IOException {
	}


	@Test
	public void getConferenceName() throws IOException {
		Assert.assertEquals("ACL 2018",crawledConference.getName());
	}
	@Test
	public void testConferenceDates(){
		Assert.assertEquals(LocalDate.of(2018,7,15), crawledConference.getStartDate());
		Assert.assertEquals(LocalDate.of(2018,7,20), crawledConference.getEndDate());
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