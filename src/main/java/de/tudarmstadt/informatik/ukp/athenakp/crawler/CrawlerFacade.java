package de.tudarmstadt.informatik.ukp.athenakp.crawler;

import java.io.IOException;
import java.util.ArrayList;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;

/**
 *
 * This is a facade class to hide the classes, which scrape the data from the specific websites
 *
 * @author Jonas Hake
 *
 */
public class CrawlerFacade extends AbstractCrawler{

	AbstractCrawler crawler;

	/**
	 * @param conference a supported Conference, which should be scraped
	 */
	public CrawlerFacade(SupportedConferences conference){
		super();
		switch(conference) {
			case ACL:
				crawler = new ACL18WebParser();
				break;
		}
	}

	@Override
	public ArrayList<String> getAuthors() throws IOException {
		return crawler.getAuthors();
	}

	@Override
	public ArrayList<String> getPaperTitles() throws IOException {
		return crawler.getPaperTitles();
	}

	@Override
	public ArrayList<ArrayList<String>> getPaperAuthor() throws IOException{
		return crawler.getPaperAuthor();
	}

	@Override
	public Conference getConferenceInformation() throws IOException {
		return crawler.getConferenceInformation();
	}

	@Override
	public ArrayList<ArrayList<String>> getTimetable() throws IOException {
		return crawler.getTimetable();
	}
}