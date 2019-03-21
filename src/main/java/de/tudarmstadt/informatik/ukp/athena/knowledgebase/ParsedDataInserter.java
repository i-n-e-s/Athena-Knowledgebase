package de.tudarmstadt.informatik.ukp.athena.knowledgebase;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.CrawlerFacade;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.SupportedConferences;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.ConferenceJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PaperJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.SessionJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.WorkshopJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.ScheduleEntry;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Session;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Workshop;


@SpringBootApplication
/*
	a class which is meant to be run only once, which is why it is separate from application. Starts Spring and adds
	data to an sql Database via hibernate
	contains methods which reformat ParserData into a hibernate digestible format
	@author Julian Steitz, Daniel Lehmann
 */
public class ParsedDataInserter {
	private CrawlerFacade acl18WebParser;
	private static Logger logger = LogManager.getLogger(ParsedDataInserter.class);

	public ParsedDataInserter(){}

	/**
	 * @param beginYear The first year to get data from
	 * @param endYear The last year to get data from
	 */
	public ParsedDataInserter(int beginYear, int endYear) {
		acl18WebParser = new CrawlerFacade(SupportedConferences.ACL, beginYear, endYear);
	}

	// This assures everything written into the database is in UTC.
	// from https://aboullaite.me/spring-boot-time-zone-configuration-using-hibernate/
	// took me far too long to find
	// TODO: look into application.yml ?
	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		long then = System.nanoTime();
		SpringApplication.run(ParsedDataInserter.class, args);
		ParsedDataInserter parsedDataInserter;
		List<String> argsList = Arrays.asList(args); //for .contains
		int beginYear = 2018, endYear = 2018;

		for(String arg : args) {
			if(arg.startsWith("-beginYear="))
				beginYear = Integer.parseInt(arg.split("=")[1]); //parse to make sure that it's a number
			else if(arg.startsWith("-endYear="))
				endYear = Integer.parseInt(arg.split("=")[1]); //parse to make sure that it's a number
		}

		if(beginYear > endYear) {
			int temp = beginYear;

			logger.info("Received arguments beginYear={}, endYear={}. endYear is bigger than beginYear, swapping them.", beginYear, endYear);
			beginYear = endYear;
			endYear = temp;
		}

		parsedDataInserter = new ParsedDataInserter(beginYear, endYear);
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

		if(argsList.contains("-scrape-acl18-info"))
			parsedDataInserter.acl2018StoreConferenceInformation(); //automatically saves the schedule as well
		else
			logger.info("\"-scrape-acl18-info\" argument was not found, skipping ACL 2018 scraping");

		logger.info("Done! (Took {})", LocalTime.ofNanoOfDay(System.nanoTime() - then));
		parsedDataInserter.acl18WebParser.close();
	}

	/**
	 * Constructs Person (Author) and Paper Objects from ACL18Webparser().getPaperAuthor() and adds them to the database
	 * see its documentation for its makeup
	 *
	 * @throws IOException if jsoup was interrupted in the scraping process (during getPaperAuthor())
	 * @author Julian Steitz, Daniel Lehmann
	 * TODO: implement saveandupdate in Common Access? Otherwise implement check if entry exist. Expensive?
	 */
	private void aclStorePapersAndAuthors() throws IOException {
		logger.info("Scraping papers and authors...");
		ArrayList<Paper> papers = acl18WebParser.getPaperAuthor();
		CommonAccess<Paper> paperFiler = new PaperJPAAccess();
		// PersonCommonAccess personfiler = new PersonJPAAccess();

		logger.info("Inserting papers and authors into database...");

		for(Paper paper : papers) {
			paperFiler.add(paper);
		}

		logger.info("Done inserting papers and authors!");
	}

	/**
	 * Stores the acl2018 conference including the schedule into the database
	 * Since Sessions contain Papers, this should be run after having executed aclStorePapersAndAuthors()
	 */
	private void acl2018StoreConferenceInformation() {
		CommonAccess<Conference> conferenceCommonAccess = new ConferenceJPAAccess();

		try {
			Conference acl2018 = acl18WebParser.getConferenceInformation();
			List<ScheduleEntry> entries = acl2018StoreSchedule();

			for(ScheduleEntry entry : entries) {
				if(entry instanceof Session)
					acl2018.addSession((Session)entry);
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
	 * @return The scraped and stored sessions
	 */
	private List<ScheduleEntry> acl2018StoreSchedule() {
		CommonAccess<Session> sessionCommonAccess = new SessionJPAAccess();
		CommonAccess<Workshop> workshopCommonAccess = new WorkshopJPAAccess();
		List<ScheduleEntry> entries = new ArrayList<>(); //initialize in case anything fails

		try {
			entries = acl18WebParser.getSchedule();

			logger.info("Inserting schedule into database...");
			//add to database
			for(ScheduleEntry entry : entries) {
				if(entry instanceof Session)
					sessionCommonAccess.add((Session)entry);
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
}