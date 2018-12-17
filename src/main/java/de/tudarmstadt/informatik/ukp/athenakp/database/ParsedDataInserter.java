package de.tudarmstadt.informatik.ukp.athenakp.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.tudarmstadt.informatik.ukp.athenakp.Application;
import de.tudarmstadt.informatik.ukp.athenakp.crawler.CrawlerFacade;
import de.tudarmstadt.informatik.ukp.athenakp.crawler.SupportedConferences;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.ConferenceCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.EventCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.PaperCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.WorkshopCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.ConferenceHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.EventHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.PaperHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.WorkshopHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Event;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.ScheduleEntry;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Workshop;


@SpringBootApplication
/*
	a class which is meant to be run only once, which is why it is separate from application. Starts Spring and adds
	data to an sql Database via hibernate
	contains methods which reformat ParserData into a hibernate digestible format
	@author Julian Steitz
 */
public class ParsedDataInserter {
	private CrawlerFacade acl18WebParser;

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
		SpringApplication.run(Application.class, args);
		ParsedDataInserter parsedDataInserter;

		List<String> argList = Arrays.asList(args);
		String beginYear = "2018", endYear = "2018";

		for(String arg : argList) {
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
	 * Constructs Author and Paper Objects from ACL18Webparser().getPaperAuthor() and adds them to the database
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
		PaperCommonAccess paperFiler = new PaperHibernateAccess();
		// PersonCommonAccess personfiler = new PersonHibernateAccess();

		for (Paper paper : papers) {
			paperFiler.add(paper);
		}
	}

	/**
	 * Stores the acl2018 conference into the database
	 */
	private void acl2018StoreConferenceInformation() {
		ConferenceCommonAccess conferenceCommonAccess = new ConferenceHibernateAccess();
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
		EventCommonAccess eventCommonAccess = new EventHibernateAccess();
		WorkshopCommonAccess workshopCommonAccess = new WorkshopHibernateAccess();

		try {
			ArrayList<ScheduleEntry> entries = acl18WebParser.getSchedule();

			for(ScheduleEntry entry : entries) {
				if(entry instanceof Event)
					eventCommonAccess.add((Event)entry);
				else if(entry instanceof Workshop)
					workshopCommonAccess.add((Workshop)entry);
			}

		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}