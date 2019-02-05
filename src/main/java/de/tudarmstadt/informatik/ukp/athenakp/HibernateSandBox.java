package de.tudarmstadt.informatik.ukp.athenakp;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.ConferenceCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.InstitutionCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.PaperCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.PersonCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.ConferenceHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.InstitutionHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.PaperHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.PersonHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Author;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Event;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Institution;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;

@SpringBootApplication
public class HibernateSandBox {

	public static void main(String[] args) {
		SpringApplication.run(HibernateSandBox.class, args);

		Institution dummyInstitution = new Institution();
		dummyInstitution.setName("Black Mesa");

		Author dummyAuthor = new Author();
		dummyAuthor.setFullName("Rumpo Derpel");
		dummyAuthor.setBirthdate(LocalDate.of(2010, 10, 10));

		Author dummyAuthor2 = new Author();
		dummyAuthor2.setPrefix("Prof. Dr.");
		dummyAuthor2.setFullName("John T. Smith");
		//		Date seems to be deprecated and its time segment can cause problems (does for me) if ignored
		//		https://stackoverflow.com/a/21598394 shows alternatives that could be useful (e.g. java.time)
		//		this might also fix the localhost:8080/persons answer birthdate	"3910-11-09T23:00:00.000+0000" for Rumo
		//      @author Julian Steitz

		//		the incorrect date is just because tristan didn't subtract 1900 when setting up Rumpo's data.
		// 		i am not sure if the
		//		alternatives proposed in the linked stackoverflow thread will work with hibernate, gotta test that
		//		-Daniel
		dummyAuthor2.setBirthdate(LocalDate.of(1970 - 1900, 1 - 1, 1));
		dummyAuthor2.setObit(LocalDate.of(2038 - 1900, 1 - 1, 19));
		//				p2.setInstitution(i); FIXME if a person has this, a query with a result containing this person will result in an error

		Paper dummyPaper = new Paper();
		dummyPaper.setRemoteLink("https://example.org");
		dummyPaper.setPdfFileSize(123456);
		dummyPaper.setReleaseDate(LocalDate.of(2018, 11, 16));
		dummyPaper.setTopic("The Life, the Universe and Everything");
		dummyPaper.setTitle("42");
		dummyPaper.setAnthology("C2PO");

		Paper dummyPaper2 = new Paper();
		dummyPaper2.setRemoteLink("https://example.org");
		dummyPaper2.setPdfFileSize(654321);
		dummyPaper2.setReleaseDate(LocalDate.of(2000, 7, 29));
		dummyPaper2.setTopic("Fiction");
		dummyPaper2.setTitle("Why Hoverboards will exist by 2015");

		dummyPaper.addAuthor(dummyAuthor);
		dummyPaper.addAuthor(dummyAuthor2);
		dummyPaper2.addAuthor(dummyAuthor2);
		dummyAuthor.addPaper(dummyPaper);
		dummyAuthor.addPaper(dummyPaper2);
		dummyAuthor2.addPaper(dummyPaper);

		// 		maybe check if the entry already exists. Otherwise duplicates could arise
		//		@author Julian Steitz

		//		every institution, paper, person has an id. for dummy data, it's not that bad if there are
		// 		duplicate names etc, they still have a unique id
		//		-Daniel
		InstitutionCommonAccess institutionAccess = new InstitutionHibernateAccess();
		institutionAccess.add(dummyInstitution);
		PersonCommonAccess personAccess = new PersonHibernateAccess();
		personAccess.add(dummyAuthor);
		personAccess.add(dummyAuthor2);
		PaperCommonAccess paperAccess = new PaperHibernateAccess();
		paperAccess.add(dummyPaper);
		paperAccess.add(dummyPaper2);

		// is supposed to illustrate how handy .parse might be after scraping
		Conference dummyConference = new Conference();
		dummyConference.setStartDate(LocalDate.parse("2012-06-30"));
		dummyConference.setEndDate(LocalDate.now());
		//		dummyConference.setStartDate(new Date(2017 - 1900, 8 - 1, 15));
		//		dummyConference.setEndDate(new Date(2017 - 1900, 9 - 1, 2));
		dummyConference.setName("Conference of Nerds");

		ConferenceCommonAccess conferenceAccess = new ConferenceHibernateAccess();
		conferenceAccess.add(dummyConference);

		Event dummyEvent = new Event();
		dummyEvent.setTitle("reeeing in public - how to channel your inner frog");
		dummyEvent.setBegin(LocalDateTime.of(2017, 8, 2, 14, 30));
		dummyEvent.setEnd(LocalDateTime.of(2017, 8, 2, 15, 0));

	}
}