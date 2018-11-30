package de.tudarmstadt.informatik.ukp.athenakp.database;

import de.tudarmstadt.informatik.ukp.athenakp.Application;
import de.tudarmstadt.informatik.ukp.athenakp.crawler.ACL18WebParser;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.ConferenceCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.PaperCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.ConferenceHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.PaperHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Author;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.ArrayList;


@SpringBootApplication
/*
	a class which is meant to be run only once, which is why it is separate from application. Starts Spring and adds
	data to an sql Database via hibernate
	contains methods which reformat ParserData into a hibernate digestible format
	@author Julian Steitz
 */
public class ParsedDataInserter {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		ParsedDataInserter parsedDataInserter = new ParsedDataInserter();
//		try {
//			parsedDataInserter.aclStorePapersAndAuthors();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		parsedDataInserter.acl2018StoreConferenceInformation();
	}

	/**
	 * Constructs Author and Paper Objects from ACL18Webparser().getPaperAuthor() and adds them to the database
	 * see its documentation for its makeup
	 *
	 * @throws IOException if jsoup was interrupted in the scraping process (during getPaperAuthor())
	 * @author Julian Steitz
	 * TODO: implement saveandupdate in Common Access? Otherwise implement check if entry exist. Expensive?
	 */
	private void aclStorePapersAndAuthors() throws IOException {
		ACL18WebParser acl18WebParser = new ACL18WebParser();
		System.out.println("Scraping, this can take a couple of minutes..");
		ArrayList<ArrayList<String>> listOfPaperAuthor = acl18WebParser.getPaperAuthor();
		PaperCommonAccess paperFiler = new PaperHibernateAccess();
		// PersonCommonAccess personfiler = new PersonHibernateAccess();

		for (ArrayList<String> paperAndAuthors : listOfPaperAuthor) {
			// only one Paper per paperandauthors
			Paper paper = new Paper();
			// clean up the titles in the form of [C18-1017] Simple Neologism Based Domain Independe...
			// C18-1017 would be the anthology - we remove [] because the rest API dislikes the characters and they
			// convey no meaning
			String rawTitle = paperAndAuthors.get(0);
			String[] splitRawTitle = rawTitle.split(", ", 2);
			String paperTitle = splitRawTitle[1];
			String anthology = splitRawTitle[0].replace("[", "").replace("]", "");
			paper.setTitle(paperTitle);
			paper.setAnthology(anthology);
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
	 * stores the acl2018 conference into the database
	 */
	private void acl2018StoreConferenceInformation() {
		ACL18WebParser acl18WebParser = new ACL18WebParser();
		ConferenceCommonAccess conferenceCommonAccess = new ConferenceHibernateAccess();
		Conference acl2018 = acl18WebParser.getConferenceInformation();
		conferenceCommonAccess.add(acl2018);
	}
}