package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler;

import java.io.IOException;
import java.util.ArrayList;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.ScheduleEntry;

/**
 *
 * This is a facade class to hide the classes, which scrape the data from the specific websites
 *
 * @author Jonas Hake, Daniel Lehmann
 *
 */
public class CrawlerFacade extends AbstractCrawler{

	AbstractCrawler crawler;

	/**
	 * @param conference a supported Conference, which should be scraped
	 * @param beginYear The first year to get data from
	 * @param endYear The last year to get data from
	 * @param conferences The abbreviations (see {@link https://aclanthology.info/}) of the conferences to scrape papers/authors from. null to scrape all. Does not work when only scraping authors
	 */
	public CrawlerFacade(SupportedConferences conference, int beginYear, int endYear, String... conferences){
		super();
		switch(conference) {
			case ACL:
				crawler = new ACLWebCrawler(beginYear, endYear, conferences);
				break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Conference> getPaperAuthorEvent() throws IOException{
		return crawler.getPaperAuthorEvent();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Conference> getConferenceACL2018() throws IOException{
		return crawler.getConferenceACL2018();
	}

	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Conference getConferenceInformation() throws IOException {
		return crawler.getConferenceInformation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<ScheduleEntry> getSchedule() throws IOException {
		return crawler.getSchedule();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		crawler.close();
	}
}