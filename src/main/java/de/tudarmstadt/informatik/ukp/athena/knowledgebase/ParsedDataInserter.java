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
	 * Needed so spring works avoidance
	 */
	public ParsedDataInserter(){}

	/**
	 * @param beginYear The first year to get data from
	 * @param endYear The last year to get data from
	 * @param conferences The abbreviations (see {@link https://aclanthology.info/}) of the conferences to scrape papers/authors from. null to scrape all. Does not work when only scraping authors
	 */
	public ParsedDataInserter(int beginYear, int endYear, String... conferences) {
		acl18WebParser = new CrawlerFacade(SupportedConferences.ACL, beginYear, endYear, conferences);
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

		for(String arg : args) {
			if(arg.startsWith("-beginYear="))
				beginYear = Integer.parseInt(arg.split("=")[1]); //parse to make sure that it's a number
			else if(arg.startsWith("-endYear="))
				endYear = Integer.parseInt(arg.split("=")[1]); //parse to make sure that it's a number
			else if(arg.startsWith("-conferences="))
				conferences = arg.replace("-conferences=", "").split(",");

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

		parsedDataInserter = new ParsedDataInserter(beginYear, endYear, conferences);

		if(argsList.contains("-scrape-paper-author-event")) {
			try {
				logger.info("Scraping years {} through {} - this can take a couple of minutes...", beginYear, endYear);
				parsedDataInserter.aclStorePapersAndAuthorsAndEvents();
			} catch (IOException e) {
				e.printStackTrace();}
			
			
		}else {
		logger.info("\"-scrape-paper-author-event\" argument was not found, skipping event scraping");

		//only scrape if respective argument was found
		if(argsList.contains("-scrape-paper-author")) {
			try {
				logger.info("Scraping years {} through {} - this can take a couple of minutes...", beginYear, endYear);
				parsedDataInserter.aclStorePapersAndAuthors();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
			logger.info("\"-scrape-paper-author\" argument was not found, skipping paper author scraping");

		}
		
		if(argsList.contains("-scrape-acl18-info"))
			parsedDataInserter.acl2018StoreConferenceInformation(); //automatically saves the schedule as well
		else
			logger.info("\"-scrape-acl18-info\" argument was not found, skipping ACL 2018 scraping");

		logger.info("Done! (Took {})", LocalTime.ofNanoOfDay(System.nanoTime() - then));
		
		parsedDataInserter.acl18WebParser.close();
	
	}

	private void aclStorePapersAndAuthorsAndEvents() throws IOException {
		logger.info("Scraping papers and authors...");
		ArrayList<Conference> conferences = acl18WebParser.getPaperAuthorEvent();
		CommonAccess<Conference> conferenceFiler = new ConferenceJPAAccess();

		logger.info("Inserting papers and authors into database...");

		for(Conference conference : conferences) {
            System.out.println("Paper added: " + conference.getName());
			conferenceFiler.add(conference);
		}

		logger.info("Done inserting papers and authors!");		
	}

	/**
	 * Constructs person (author) and paper objects from {@link de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.ACLWebCrawler#getPaperAuthor()}
	 * and adds them to the database. See its documentation for its makeup
	 *
	 * @throws IOException if jsoup was interrupted in the scraping process (during getPaperAuthor())
	 * @author Julian Steitz, Daniel Lehmann
	 */
	private void aclStorePapersAndAuthors() throws IOException {
		logger.info("Scraping papers and authors...");
		ArrayList<Paper> papers = acl18WebParser.getPaperAuthor();
		CommonAccess<Paper> paperFiler = new PaperJPAAccess();

		logger.info("Inserting papers and authors into database...");

		for(Paper paper : papers) {
            System.out.println("Paper added: " + paper.getTitle());
			paperFiler.add(paper);
		}

		logger.info("Done inserting papers and authors!");
		
	}

	/**
	 * Stores the ACL 2018 conference including the schedule into the database
	 * Since events contain papers, this should be run after having executed {@link ParsedDataInserter#aclStorePapersAndAuthors()}
	 */
	private void acl2018StoreConferenceInformation() {
		CommonAccess<Conference> conferenceCommonAccess = new ConferenceJPAAccess();

		try {
			Conference acl2018 = acl18WebParser.getConferenceInformation();
			List<ScheduleEntry> entries = acl2018StoreSchedule();

			for(ScheduleEntry entry : entries) {
				if(entry instanceof Event)
					acl2018.addEvent((Event)entry);
				else if(entry instanceof Workshop)
					acl2018.addWorkshop((Workshop)entry);
			}

			logger.info("Inserting conference into database...");
			conferenceCommonAccess.add(acl2018);
			logger.info("Done inserting!");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stores the acl2018 conference's schedule into the database
	 * @return The scraped and stored events and workshops
	 */
	private List<ScheduleEntry> acl2018StoreSchedule() {
		CommonAccess<Event> eventCommonAccess = new EventJPAAccess();
		CommonAccess<Workshop> workshopCommonAccess = new WorkshopJPAAccess();
		List<ScheduleEntry> entries = new ArrayList<>(); //initialize in case anything fails

		try {
			entries = acl18WebParser.getSchedule();

			logger.info("Inserting schedule into database...");
			//add to database
			for(ScheduleEntry entry : entries) {
				if(entry instanceof Event)
					eventCommonAccess.add((Event)entry);
				else if(entry instanceof Workshop)
					workshopCommonAccess.add((Workshop)entry);
			}
			logger.info("Done inserting!");
		}
		catch(IOException e){
			e.printStackTrace();
		}

		return entries;
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