package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.SemanticScholarAPI.S2APIFunctions;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.*;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.hibernate.PersonHibernateAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.*;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.JPASandBox;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.CrawlerFacade;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.SupportedConferences;


@SpringBootApplication
/*
	a class which is meant to be run only once, which is why it is separate from application. Starts Spring and adds
	data to an sql Database via hibernate
	contains methods which reformat ParserData into a hibernate digestible format
	@author Julian Steitz
 */
public class ParsedDataInserter {
	private CrawlerFacade acl18WebParser;

	public ParsedDataInserter(){}

	/**
	 * @param beginYear The first year to get data from
	 * @param endYear The last year to get data from
	 */
	public ParsedDataInserter(String beginYear, String endYear) {
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
		SpringApplication.run(JPASandBox.class, args);
		ParsedDataInserter parsedDataInserter;

		String beginYear = "2018", endYear = "2018";

		for(String arg : args) {
			if(arg.startsWith("-beginYear=")) {
				String year = arg.split("=")[1];

				Integer.parseInt(year); //parse to make sure that it's a number
				beginYear = year;
			}
			else if(arg.startsWith("-endYear=")) {
				String year = arg.split("=")[1];

				Integer.parseInt(year); //parse to make sure that it's a number
				endYear = year;
			}
		}

		parsedDataInserter = new ParsedDataInserter(beginYear, endYear);
		System.out.printf("Scraping years %s through %s - this can take a couple of minutes...\n", beginYear, endYear);

		try {
			parsedDataInserter.aclStorePapersAndAuthors();
		} catch (IOException e) {
			e.printStackTrace();
		}
		parsedDataInserter.acl2018StoreConferenceInformation(); //automatically saves the schedule as well
		System.out.println("Done!");
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
		System.out.println("Scraping papers and authors...");
		ArrayList<Paper> papers = acl18WebParser.getPaperAuthor();
		PaperCommonAccess paperFiler = new PaperJPAAccess();
		// PersonCommonAccess personfiler = new PersonJPAAccess();

		System.out.println("Inserting papers and authors into database...");

		for(Paper paper : papers) {
			paperFiler.add(paper);
		}

		System.out.println("Done inserting papers and authors!");
	}

	/**
	 * Stores the acl2018 conference including the schedule into the database
	 */
	private void acl2018StoreConferenceInformation() {
		ConferenceCommonAccess conferenceCommonAccess = new ConferenceJPAAccess();

		try {
			Conference acl2018 = acl18WebParser.getConferenceInformation();
			List<ScheduleEntry> entries = acl2018StoreSchedule();

			for(ScheduleEntry entry : entries) {
				if(entry instanceof Session)
					acl2018.addSession((Session)entry);
				else if(entry instanceof Workshop)
					acl2018.addWorkshop((Workshop)entry);
			}

			System.out.println("Inserting conference into database...");
			conferenceCommonAccess.add(acl2018);
			System.out.println("Done inserting!");
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
		SessionCommonAccess sessionCommonAccess = new SessionJPAAccess();
		WorkshopCommonAccess workshopCommonAccess = new WorkshopJPAAccess();
		List<ScheduleEntry> entries = new ArrayList<>(); //initialize in case anything fails

		try {
			entries = acl18WebParser.getSchedule();

			System.out.println("Inserting schedule into database...");
			//add to database
			for(ScheduleEntry entry : entries) {
				if(entry instanceof Session)
					sessionCommonAccess.add((Session)entry);
				else if(entry instanceof Workshop)
					workshopCommonAccess.add((Workshop)entry);
			}
			System.out.println("Done inserting!");
		}
		catch(IOException e){
			e.printStackTrace();
		}

		return entries;
	}

	/**
	 * This method runs through the DB and performs an authorSearch for every
	 * Person in the DB. Then extends every entry with the new data
	 *
	 * TODO write changes to DB
	 *
	 * @author Philipp Emmer
	 * @param n The first n authors will be added
	 */
	private void completeAuthorsByS2(int n) {
		PersonCommonAccess personfiler = new PersonJPAAccess();
		List<Person> authors = personfiler.get();

		//Go through every Author in the db
		long failedAuthors = 0;
		long totalAuthors = 0;
		long failedPapers = 0;
		long totalPapers = 0;
		for ( Person currPerson : authors ) {
			if( ++totalAuthors == n ) { break; }

			//TODO make sure we get author objects (with at least a name)
			//1. Make sure person is an Author and cast
			if ( failedAuthors == 20 ) {
				System.out.println("Too many fails, break now");
				break;
			}

			//2. Update information about Author
			boolean res = false;
			//TODO When setting coworkers etc: Use existing persons from db, try not to create new ones
			try { res = S2APIFunctions.completeAuthorInformationByAuthorSearch(currPerson, false); }
			catch (IOException e) {
				failedAuthors++;
				e.printStackTrace();
				System.err.println("curr");
			}

			//TODO 3. write changes to db
			if ( res ) {
				System.out.println("Trying to update: "+currPerson.toString());
				personfiler.update( currPerson );
			}
		}
		System.out.println("Failed: "+failedAuthors+"\nDone");

	}
}