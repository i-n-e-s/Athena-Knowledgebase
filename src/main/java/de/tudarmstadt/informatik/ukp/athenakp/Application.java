package de.tudarmstadt.informatik.ukp.athenakp;

import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.InstitutionCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.PaperCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.PersonCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.hibernate.InstitutionHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.hibernate.PaperHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.hibernate.PersonHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Author;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Institution;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

		Institution i = new Institution();
		i.setName("Black Mesa");

		//		I got confused because mainly because I am daft, but also because "p" variable names are maybe not ideal
		//		maybe call p "dummyauthor"
		//		@author Julian Steitz
		Author p = new Author();
		p.setFirstName("Rumpo");
		p.setLastName("Derpel");
		p.setBirthdate(new Date(2010, 10, 10));

		Author p2 = new Author();
		p2.setPrefix("Prof. Dr.");
		p2.setFirstName("John");
		p2.setMiddleName("T.");
		p2.setLastName("Smith");
		//		Date seems to be deprecated and its time segment can cause problems (does for me) if ignored
		//		https://stackoverflow.com/a/21598394 shows alternatives that could be useful (e.g. java.time)
		//		this might also fix the localhost:8080/persons answer birthdate	"3910-11-09T23:00:00.000+0000" for Rumo
        //      @author Julian Steitz
		p2.setBirthdate(new Date(1970 - 1900, 1 - 1, 1));
		//		the incorrect date is just because tristan didn't subtract 1900 when setting up Rumpo's data. i am not sure if the
		//		alternatives proposed in the linked stackoverflow thread will work with hibernate, gotta test that
		//		-Daniel
		//				p2.setInstitution(i); FIXME if a person has this, a query with a result containing this person will result in an error

		//		pa to dummypaper?
		Paper pa = new Paper();
		pa.setHref("https://example.org");
		pa.setPdfFileSize(123456);
		pa.setReleaseDate(new Date(2018 - 1900, 11 - 1, 16));
		pa.setTopic("The Life, the Universe and Everything");
		pa.setTitle("42");

		Paper pa2 = new Paper();
		pa2.setHref("https://example.org");
		pa2.setPdfFileSize(654321);
		pa2.setReleaseDate(new Date(2000 - 1900, 7 - 1, 29));
		pa2.setTopic("Fiction");
		pa2.setTitle("Why Hoverboards will exist by 2015");

		pa.addAuthor(p);
		pa.addAuthor(p2);
		pa2.addAuthor(p2);
		p.addPaper(pa);
		p.addPaper(pa2);
		p2.addPaper(pa);

		// 		maybe check if the entry already exists. Otherwise duplicates could arise
		//		@author Julian Steitz
		InstitutionCommonAccess ica = new InstitutionHibernateAccess();
		//		every institution, paper, person has an id. for dummy data, it's not that bad if there are duplicate names etc, they still have a unique id.
		//		-Daniel
		pca.add(p);
		pca.add(p2);
		PaperCommonAccess paca = new PaperHibernateAccess();
		paca.add(pa);
		paca.add(pa2);

		//TODO add to database and if done, enable ConferenceController
		//		Conference c = new Conference();
		//		c.setStartDate(new Date(2017 - 1900, 8 - 1, 15));
		//		c.setEndDate(new Date(2017 - 1900, 9 - 1, 2));
		//		c.setName("Conference of Nerds");
		//
		//		ConferenceCommonAccess cca = new ConferenceHibernateAccess();
		//		cca.add(c);
	}
}