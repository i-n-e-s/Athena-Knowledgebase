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

	
	
	/**
	 * A method which returns a list of conference instances with their name, location, and events set. 
	 * Those events are scraped according to https://aclweb.org/anthology/ and contain the respective papers.
	 * Those papers contain their authors. If the list of conferences is added to the database so are  
	 * events, papers and authors.
	 * Scrapes an about page for its information and employs string conversion found in CrawlerToolset
	 * @return A list of conferences with their name, location and events set, null when data not available, an empty Conference instance if an IOException occurs
	 * @throws IOException if Jsoup.connect fails
	 * @author Tim Schmidt
	 */
	public abstract ArrayList<Conference> getPaperAuthorEvent() throws IOException;

	/**
	 * A method which returns a list containing a single conference instance resembling acl2018 
	 * as presented on https://acl2018.org/.
	 * The conference contains its name, location, dates and description as well as its events.   
	 * The events contain varying informations as provided by the site.
	 * Mentioned papers and persons are added. Duplicates to getPaperAuthorEvent() are avoided.
	 * @return A list containing a conference instance of acl2018 with its information and events set.
	 * @throws IOException if Jsoup.connect fails
	 * @author Tim Schmidt
	 */
	public abstract ArrayList<Conference> getConferenceACL2018() throws IOException;

	/**
	 * Reads the json file named '' contained in '' retrieves the papers referenced by doc_key as paperID 
	 * creates tags and adds them to the respective paper.
	 * @return A list of papers with set tags.
	 * @throws IOException if Jsoup.connect fails
	 * @author Tim Schmidt
	 */
    public abstract ArrayList<Paper> getTags() throws IOException;
	
	
	/**
	 * Can be used to remove any unneeded data after the crawler is done
	 */
	public abstract void close();
}
