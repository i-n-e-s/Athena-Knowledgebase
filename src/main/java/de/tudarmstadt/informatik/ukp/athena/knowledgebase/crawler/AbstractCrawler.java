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
	/**
	 * Can be used to remove any unneeded data after the crawler is done
	 */
	public abstract void close();
}
