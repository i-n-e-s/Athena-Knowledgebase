package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.JPASandBox;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.CrawlerFacade;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.SupportedConferences;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.ConferenceCommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.PaperCommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.SessionCommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.ConferenceJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PaperJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.SessionJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Session;


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

	// this makes it so everything written into the database is in UTC.
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

			acl2018.setSessions(new HashSet<Session>(acl2018StoreSchedule())); //acl2018StoreSchedule returns a list, passing that to the hashset initializes the set with the list elements
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
	private List<Session> acl2018StoreSchedule() {
		SessionCommonAccess sessionCommonAccess = new SessionJPAAccess();
		WorkshopCommonAccess workshopCommonAccess = new WorkshopJPAAccess();
		List<ScheduleEntry> sessions = new ArrayList<>(); //initialize in case anything fails

		try {
			sessions = acl18WebParser.getSchedule();

			System.out.println("Inserting schedule into database...");
			//add to database
			for(ScheduleEntry entry : entries) {
				if(entry instanceof Event)
					eventCommonAccess.add((Event)entry);
				else if(entry instanceof Workshop)
					workshopCommonAccess.add((Workshop)entry);
			}
			System.out.println("Done inserting!");
		}
		catch(IOException e){
			e.printStackTrace();
		}

		return sessions;
	}
}