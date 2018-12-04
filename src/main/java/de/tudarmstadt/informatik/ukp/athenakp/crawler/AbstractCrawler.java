package de.tudarmstadt.informatik.ukp.athenakp.crawler;

import java.io.IOException;
import java.util.ArrayList;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;

/**
 *
 * This is the abstract base of the Crawler
 *
 * @author Jonas Hake
 *
 */
abstract class AbstractCrawler {
	/**
	 * Returns all Authors, which published in the year 2018
	 *
	 * @return a list of all authors, null when data not available
	 * @throws IOException if Jsoup.connect fails
	 */
	public abstract ArrayList<String> getAuthors() throws IOException;

	/**
	 * Returns all Papers, which were published in the year 2018
	 *
	 * @return a list of all paper titles, null when data not available
	 * @throws IOException if Jsoup.connect fails
	 */
	public abstract ArrayList<String> getPaperTitles() throws IOException;

	/**
	 *
	 * Returns a List of List. Each Sublist represent a published Paper from ACL'18.
	 * The Sublists are in the Form: Title, Author1, Author2, ...
	 *
	 * @return A List of Lists of Papertitle and associated Author, null when data not available
	 * @throws IOException if Jsoup.connect fails
	 */
	public abstract ArrayList<ArrayList<String>> getPaperAuthor() throws IOException;
	/**
	 * A method which returns a Conference instance with its name, location and start and end date set
	 * scrapes the aboutPage of ACL2018 for its information and employs String conversion found in CrawlerToolset
	 * if an IO Exception occurs, it returns an empty Conference instance
	 * @return a Conference instance with its name, location and start and end date set, null when data not available
	 * @throws IOException if Jsoup.connect fails
	 * @author Julian Steitz
	 */
	public abstract Conference getConferenceInformation() throws IOException;
}
