package de.tudarmstadt.informatik.ukp.athena.knowledgebase;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.PDFParser.Parser;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.APIController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.APIController;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.NeedlemanWunsch;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.CrawlerFacade;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.SupportedConferences;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.semanticscholarapi.S2APIFunctions;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.ConferenceJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.EventJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PaperJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PersistenceManager;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PersonJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.WorkshopJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Event;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.ScheduleEntry;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Workshop;

@SpringBootApplication
/**
 *	A class which is meant to be run only once, which is why it is separate from application. Starts Spring and adds
 *	data to an sql Database via hibernate
 *	contains methods which reformat ParserData into a hibernate digestible format
 *	@author Julian Steitz, Daniel Lehmann, Philipp Emmer
 */
public class ParsedDataInserter {
	private CrawlerFacade acl18WebParser;
	private static Logger logger = LogManager.getLogger(ParsedDataInserter.class);

	/**
	 * Needed so spring works
	 */
	public ParsedDataInserter(){}

	/**
	 * @param beginYear The first year to get data from
	 * @param endYear The last year to get data from
	 * @param conferences The abbreviations (see {@link https://aclanthology.info/}) of the conferences to scrape papers/authors from. null to scrape all.
	 */
	public ParsedDataInserter(int beginYear, int endYear,boolean parsePdf, String... conferences) {
		acl18WebParser = new CrawlerFacade(SupportedConferences.ACL, beginYear, endYear,parsePdf, conferences);
	}


	/**
	 * This assures that everything written into the database is in UTC.
	 * From https://aboullaite.me/spring-boot-time-zone-configuration-using-hibernate/
	 */
	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC")); // took me far too long to find
	}

	//length of 40 lines exceeded because this is all one startup sequence which manages everything
	public static void main(String[] args) {
		long then = System.nanoTime();
		SpringApplication.run(ParsedDataInserter.class, args);
		ParsedDataInserter parsedDataInserter;
		List<String> argsList = Arrays.asList(args); //for .contains
		int beginYear = 2018, endYear = 2018;
		String[] conferences = null;
		boolean parsePdf=false;

		for(String arg : args) {
			if(arg.contains("-beginYear=")) {
				beginYear = Integer.parseInt(arg.split("=")[1]); //parse to make sure that it's a number
			}
			if(arg.contains("-endYear=")) {
				endYear = Integer.parseInt(arg.split("=")[1]); //parse to make sure that it's a number
			}
			if(arg.contains("-conferences=")) {
				conferences = arg.replace("-conferences=", "").split(",");
			}
			if(arg.contains("-parsePdf")) {
				parsePdf=true;
			}
			
		}

		if(beginYear > endYear) {
			int temp = beginYear;

			logger.info("Received arguments beginYear={}, endYear={}. endYear is bigger than beginYear, swapping them.", beginYear, endYear);
			beginYear = endYear;
			endYear = temp;
		}

		if(conferences == null)
			logger.info("No specific conferences given, will scrape papers and authors from all available conferences");
		else
			logger.info("Specific conferences given, will scrape papers and authors from the following: {}", Arrays.toString(conferences));

		parsedDataInserter = new ParsedDataInserter(beginYear, endYear,parsePdf, conferences);
		
		//only scrape if respective argument was found
		if(argsList.contains("-scrape-paper-author-event")) {
			try {
				logger.info("Scraping years {} through {} - this can take a couple of minutes...", beginYear, endYear);
				parsedDataInserter.aclStorePapersAndAuthorsAndEvents();
			} catch (IOException e) {
				e.printStackTrace();}
			
			
		}else {
		logger.info("\"-scrape-paper-author-event\" argument was not found, skipping event scraping");
		
		if(argsList.contains("-scrape-acl18-info"))
			try {
			logger.info("Scraping ACL2018", beginYear, endYear);
			parsedDataInserter.aclStoreConferenceACL2018(); //automatically saves the schedule as well
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			else
			logger.info("\"-scrape-acl18-info\" argument was not found, skipping ACL 2018 scraping");
		
		}
		Parser parse = new Parser();
		
		if(argsList.contains("-insert-tags")) {
			
			try {
				logger.info("Inserting Tags", beginYear, endYear);
				parsedDataInserter.aclStoreTags(); //automatically saves the schedule as well
				} catch (IOException e) {
					e.printStackTrace();
				}
			
			
			
		}else {
			logger.info("\"-insert-tags\" argument was not found, skipping tag generation");

			
		}
		
		
		//retrieves the institution the papers are published from by their plain text and writes it to the institution field of the authors
		if(argsList.contains("-parse-institutions"))
			parse.parseInstitution();
		logger.info("Done! (Took {})", LocalTime.ofNanoOfDay(System.nanoTime() - then));

		parsedDataInserter.acl18WebParser.close();
	}

	private void aclStorePapersAndAuthorsAndEvents() throws IOException {
		logger.info("Scraping papers and authors...");
		ArrayList<Conference> conferences = acl18WebParser.getPaperAuthorEvent();
		CommonAccess<Conference> conferenceFiler = new ConferenceJPAAccess();

		logger.info("Inserting papers, authors and events into database...");

		for(Conference conference : conferences) {
            System.out.println("Conference added: " + conference.getName());
			conferenceFiler.commitChanges(conference);
		}
		logger.info("Done inserting papers, authors and events!");
	}
	
	
	
	private void aclStoreConferenceACL2018() throws IOException {
		logger.info("Scraping papers and authors...");
		ArrayList<Conference> conferences = acl18WebParser.getConferenceACL2018();
		CommonAccess<Conference> conferenceFiler = new ConferenceJPAAccess();

		logger.info("Inserting papers, authors and events into database...");

		for(Conference conference : conferences) {
            System.out.println("Conference added: " + conference.getName());
			conferenceFiler.commitChanges(conference);
		}
		logger.info("Done inserting papers, authors and events!");
	}
	
	private void aclStoreTags() throws IOException {
		logger.info("Storing tags...");
		ArrayList<Paper> papers = acl18WebParser.getTags();
		CommonAccess<Paper> paperFiler = new PaperJPAAccess();

		logger.info("Tags...");

		for(Paper paper : papers) {
			paperFiler.commitChanges(paper);
		}

		logger.info("Done inserting Tags!");
		
	}
	


	/**
	 * This method runs through the DB and performs an author search for every
	 * person in the DB. It then extends every entry with the new data
	 *
	 * @author Philipp Emmer
	 * @param n The first n authors will be enhanced with Semantic Scholar data
	 */
	private void completeAuthorsByS2(int n) {
		PersonJPAAccess personfiler = new PersonJPAAccess();
		List<Person> authors = personfiler.get();
		EntityManager entityManager = PersistenceManager.getEntityManager();
		//Go through every author in the db
		long failedAuthors = 0;
		long totalAuthors = 0;
		for ( Person currPerson : authors ) {
			if( totalAuthors++ == n ) { break; }
			//1. Update information about the author
			entityManager.getTransaction().begin();
			try { S2APIFunctions.completeAuthorInformationByAuthorSearch(currPerson, false); }
			catch (IOException | JSONException e) {
				failedAuthors++;
				e.printStackTrace();
			}
			entityManager.getTransaction().commit();
		}
		logger.info("Failed: {}\nDone",failedAuthors);

	}

}