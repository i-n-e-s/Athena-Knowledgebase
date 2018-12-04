package de.tudarmstadt.informatik.ukp.athenakp.database;

import java.io.IOException;
import java.time.LocalDate;
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
import de.tudarmstadt.informatik.ukp.athenakp.database.access.PaperCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.ConferenceHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.PaperHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Author;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;


@SpringBootApplication
/*
	a class which is meant to be run only once, which is why it is separate from application. Starts Spring and adds
	data to an sql Database via hibernate
	contains methods which reformat ParserData into a hibernate digestible format
	@author Julian Steitz
 */
public class ParsedDataInserter {
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
		ParsedDataInserter parsedDataInserter = new ParsedDataInserter();

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

		System.out.printf("Scraping years %s through %s", beginYear, endYear);

		try {
			parsedDataInserter.aclStorePapersAndAuthors(beginYear, endYear);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//		parsedDataInserter.acl2018StoreConferenceInformation();
	}

	/**
	 * Constructs Author and Paper Objects from ACL18Webparser().getPaperAuthor() and adds them to the database
	 * see its documentation for its makeup
	 *
	 * @param beginYear The first year to get data from
	 * @param endYear The last year to get data from
	 * @throws IOException if jsoup was interrupted in the scraping process (during getPaperAuthor())
	 * @author Julian Steitz, Daniel Lehmann
	 * TODO: implement saveandupdate in Common Access? Otherwise implement check if entry exist. Expensive?
	 */
	private void aclStorePapersAndAuthors(String beginYear, String endYear) throws IOException {
		CrawlerFacade acl18WebParser = new CrawlerFacade(SupportedConferences.ACL, beginYear, endYear);
		System.out.println(" - this can take a couple of minutes..");
		ArrayList<ArrayList<String>> listOfPaperAuthor = acl18WebParser.getPaperAuthor();
		System.out.println("Done scraping! Inserting data into database...");
		PaperCommonAccess paperFiler = new PaperHibernateAccess();
		// PersonCommonAccess personfiler = new PersonHibernateAccess();

		for (ArrayList<String> paperAndAuthors : listOfPaperAuthor) {
			// only one Paper per paperandauthors
			Paper paper = new Paper();
			// clean up the titles in the form of [C18-1017] Simple Neologism Based Domain Independe...
			// C18-1017 would be the anthology - we remove [] because the rest API dislikes the characters and they
			// convey no meaning
			String rawStore = paperAndAuthors.get(0);
			String[] storeSplit = rawStore.split(";;");
			String rawTitle = storeSplit[0];
			String[] splitRawTitle = rawTitle.split(" ", 2);
			String paperTitle = splitRawTitle[1];
			String anthology = splitRawTitle[0].replace("[", "").replace("]", "");
			paper.setTitle(paperTitle);
			paper.setAnthology(anthology);
			paper.setReleaseDate(LocalDate.of(Integer.parseInt(storeSplit[1]), Integer.parseInt(storeSplit[2]), 1));
			paper.setHref("http://aclweb.org/anthology/" + anthology); //wow that was easy
			// we ignore the first entry, since it is a Paper's title
			for (int i = 1; i < paperAndAuthors.size(); i++) {
				String authorName = paperAndAuthors.get(i);
				Author author = new Author();
				// because acl2018 seems to not employ prefixes (e.g. Prof. Dr.), we do not need to scan them
				// scanning them might make for a good user story
				author.setFullName(authorName);
				// Both following statements seem necessary for the author_paper table but lead to Hibernate
				// access returning an object (paper) as often as a relation in author_paper exists
				// looking into the tables themselves, duplicate papers (even with the same PaperID) do not exist
				// TODO: fix whatever causes the multiple Hibernate Accesses (returning the same object)
				// TODO: when calling the API (my guess: paper_author relation)
				// set paper - author relation
				paper.addAuthor(author);
				// set author - paper relation
				author.addPaper(paper);
				// add author to database + paper included
				// personfiler.add(author);
			}
			// adding the paper automatically adds the corresponding authors - realisation that took hours
			paperFiler.add(paper);
		}
	}

	/**
	 * Stores the acl2018 conference into the database
	 * @param beginYear The first year to get data from
	 * @param endYear The last year to get data from
	 */
	private void acl2018StoreConferenceInformation(String beginYear, String endYear) {
		CrawlerFacade acl18WebParser = new CrawlerFacade(SupportedConferences.ACL, beginYear, endYear);
		ConferenceCommonAccess conferenceCommonAccess = new ConferenceHibernateAccess();
		try{
			Conference acl2018 = acl18WebParser.getConferenceInformation();
			conferenceCommonAccess.add(acl2018);
		}
		catch (IOException e){
			e.printStackTrace();
		}

	}
}