package de.tudarmstadt.informatik.ukp.athenakp.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.tudarmstadt.informatik.ukp.athenakp.JPASandBox;
import de.tudarmstadt.informatik.ukp.athenakp.crawler.CrawlerFacade;
import de.tudarmstadt.informatik.ukp.athenakp.crawler.SupportedConferences;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.ConferenceCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.EventCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.PaperCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.jpa.ConferenceJPAAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.jpa.EventJPAAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.jpa.PaperJPAAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Event;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;


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
		System.out.printf("Scraping years %s through %s", beginYear, endYear);

		try {
			parsedDataInserter.aclStorePapersAndAuthors();
		} catch (IOException e) {
			e.printStackTrace();
		}
		parsedDataInserter.acl2018StoreConferenceInformation();
		parsedDataInserter.acl2018StoreEventInformation();
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
		System.out.println(" - this can take a couple of minutes..");
		ArrayList<Paper> papers = acl18WebParser.getPaperAuthor();
		System.out.println("Done scraping! Inserting data into database...");
		PaperCommonAccess paperFiler = new PaperJPAAccess();
		// PersonCommonAccess personfiler = new PersonJPAAccess();

		for (Paper paper : papers) {
			paperFiler.add(paper);
		}
	}

	/**
	 * Stores the acl2018 conference into the database
	 */
	private void acl2018StoreConferenceInformation() {
		ConferenceCommonAccess conferenceCommonAccess = new ConferenceJPAAccess();
		try{
			Conference acl2018 = acl18WebParser.getConferenceInformation();
			conferenceCommonAccess.add(acl2018);
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Stores the acl2018 conference's timetable into the database
	 */
	private void acl2018StoreEventInformation() {
		EventCommonAccess eventCommonAccess = new EventJPAAccess();

		try {
			ArrayList<Event> events = acl18WebParser.getSchedule();

			for(Event event : events) {
				eventCommonAccess.add(event);
			}

		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}