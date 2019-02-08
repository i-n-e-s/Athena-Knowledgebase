package de.tudarmstadt.informatik.ukp.athenakp.crawler;

import java.io.IOException;
import java.util.ArrayList;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Person;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Session;

/**
 *
 * This is the abstract base of the crawler
 *
 * @author Jonas Hake
 *
 */
abstract class AbstractCrawler {
	/**
	 * Returns all authors who published in the year range given to the crawler facade
	 *
	 * @return A list of all authors, an empty list when data not available
	 * @throws IOException if Jsoup.connect fails
	 */
	public abstract ArrayList<Person> getAuthors() throws IOException;

	/**
	 * Returns all papers which were published n the year range given to the crawler facade
	 *
	 * @return A list of all papers, an empty list when data not available
	 * @throws IOException if Jsoup.connect fails
	 */
	public abstract ArrayList<Paper> getPapers() throws IOException;

	/**
	 * Returns a list of papers. Each object contains the paper's title and its authors
	 *
	 * @return A list of papers and their associated authors, an empty list when data not available
	 * @throws IOException if Jsoup.connect fails
	 */
	public abstract ArrayList<Paper> getPaperAuthor() throws IOException;

	/**
	 * A method which returns a conference instance with its name, location, and start and end date set.
	 * Scrapes an about page for its information and employs string conversion found in CrawlerToolset
	 * @return A conference instance with its name, location, and start and end date set, null when data not available, an empty Conference instance if an IOException occurs
	 * @throws IOException if Jsoup.connect fails
	 * @author Julian Steitz
	 */
	public abstract Conference getConferenceInformation() throws IOException;

	/**
	 * Returns a list of sessions.
	 * @return A list of sessions, an empty list when data not available
	 * @throws IOException if Jsoup.connect fails
	 */
	public abstract ArrayList<Session> getSchedule() throws IOException;
}
