package de.tudarmstadt.informatik.ukp.athenakp.crawler;

import java.io.IOException;
import java.util.ArrayList;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;

/**
 *
 * This is the abstract base of the crawler
 *
 * @author Jonas Hake
 *
 */
abstract class AbstractCrawler {
	/**
	 * Returns all authors who published in the year 2018
	 *
	 * @return A list of all authors, null when data not available
	 * @throws IOException if Jsoup.connect fails
	 */
	public abstract ArrayList<String> getAuthors() throws IOException;

	/**
	 * Returns all papers which were published in the year 2018
	 *
	 * @return A list of all paper titles, null when data not available
	 * @throws IOException if Jsoup.connect fails
	 */
	public abstract ArrayList<String> getPaperTitles() throws IOException;

	/**
	 * Returns a list of lists. Each sublist represents a published paper.
	 * The sub lists are in the form: Title, Author1, Author2, ...
	 *
	 * @return A list of lists of paper's titles and their associated author, null when data not available
	 * @throws IOException if Jsoup.connect fails
	 */
	public abstract ArrayList<ArrayList<String>> getPaperAuthor() throws IOException;

	/**
	 * A method which returns a conference instance with its name, location, and start and end date set.
	 * Scrapes an about page for its information and employs string conversion found in CrawlerToolset
	 * @return A conference instance with its name, location, and start and end date set, null when data not available, an empty Conference instance if an IOException occurs
	 * @throws IOException if Jsoup.connect fails
	 * @author Julian Steitz
	 */
	public abstract Conference getConferenceInformation() throws IOException;

	/**
	 * Returns a list of lists. Each sublist represents an event.
	 * The sub lists are in the form: conference, date, begin time, end time, title, (host,) place, description, category, list of sessions
	 * @return A list of lists of events and their metadata, null when data not available
	 * @throws IOException if Jsoup.connect fails
	 */
	public abstract ArrayList<ArrayList<Object>> getTimetable() throws IOException;
}
