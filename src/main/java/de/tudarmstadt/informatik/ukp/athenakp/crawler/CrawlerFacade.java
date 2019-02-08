package de.tudarmstadt.informatik.ukp.athenakp.crawler;

import java.io.IOException;
import java.util.ArrayList;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.*;

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
	 */
	public CrawlerFacade(SupportedConferences conference, String beginYear, String endYear){
		super();
		switch(conference) {
			case ACL:
				crawler = new ACLWebCrawler(beginYear, endYear);
				break;
		}
	}

	@Override
	public ArrayList<Person> getAuthors() throws IOException {
		return crawler.getAuthors();
	}

	@Override
	public ArrayList<Paper> getPapers() throws IOException {
		return crawler.getPapers();
	}

	@Override
	public ArrayList<Paper> getPaperAuthor() throws IOException{
		return crawler.getPaperAuthor();
	}

	@Override
	public Conference getConferenceInformation() throws IOException {
		return crawler.getConferenceInformation();
	}

	@Override
	public ArrayList<Session> getSchedule() throws IOException {
		return crawler.getSchedule();
	}
}