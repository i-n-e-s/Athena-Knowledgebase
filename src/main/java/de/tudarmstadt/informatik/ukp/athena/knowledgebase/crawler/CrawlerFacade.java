package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler;

import java.io.IOException;
import java.util.ArrayList;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Author;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
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
	 */
	public CrawlerFacade(SupportedConferences conference, String beginYear, String endYear){
		super();
		switch(conference) {
			case ACL:
				crawler = new ACL18WebParser(beginYear, endYear);
				break;
		}
	}

	@Override
	public ArrayList<Author> getAuthors() throws IOException {
		return crawler.getAuthors();
	}

	@Override
	public ArrayList<Paper> getPaperTitles() throws IOException {
		return crawler.getPaperTitles();
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
	public ArrayList<ScheduleEntry> getSchedule() throws IOException {
		return crawler.getSchedule();
	}
}