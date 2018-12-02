package de.tudarmstadt.informatik.ukp.athenakp.crawler;

/**
 * 
 * This is a facade class to hide the classes, which scrape the data from the specific websites 
 * 
 * @author Jonas Hake
 * 
 */
public class CrawlerFacade extends AbstractCrawler{

	AbstractCrawler crawler;

	public CrawlerFacade(SupportedConferences conference){
		switch(conference) {
		case ACL:
			crawler = new ACL18WebParser();
			break;
		}
	}
}