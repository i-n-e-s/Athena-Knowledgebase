package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler;

import java.io.IOException;
import java.util.ArrayList;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.ScheduleEntry;

/**
 *
 * This is the abstract base of the crawler
 *
 * @author Jonas Hake, Daniel Lehmann
 *
 */
abstract class AbstractCrawler {

	public abstract ArrayList<Conference> getPaperAuthorEvent() throws IOException;

	public abstract ArrayList<Conference> getConferenceACL2018() throws IOException;

	
	
	/**
	 * A method which returns a conference instance with its name, location, and start and end date set.
	 * Scrapes an about page for its information and employs string conversion found in CrawlerToolset
	 * @return A conference instance with its name, location, and start and end date set, null when data not available, an empty Conference instance if an IOException occurs
	 * @throws IOException if Jsoup.connect fails
	 * @author Julian Steitz
	 */
	public abstract Conference getConferenceInformation() throws IOException;

		
	
	/**
	 * Returns a list of schedule entries (can be events or workshops).
	 * @return A list of schedule entries, an empty list when data not available
	 * @throws IOException if Jsoup.connect fails
	 */
	public abstract ArrayList<ScheduleEntry> getSchedule() throws IOException;

	/**
	 * Can be used to remove any unneeded data after the crawler is done
	 */
	public abstract void close();
}
