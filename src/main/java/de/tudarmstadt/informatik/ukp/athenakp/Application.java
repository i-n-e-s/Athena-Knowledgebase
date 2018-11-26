package de.tudarmstadt.informatik.ukp.athenakp;

import java.io.IOException;
import java.util.Date;

import de.tudarmstadt.informatik.ukp.athenakp.crawler.ACL18WebParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.InstitutionCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.PaperCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.PersonCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.hibernate.InstitutionHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.hibernate.PaperHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.hibernate.PersonHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Institution;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Person;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

		Institution i = new Institution();
		i.setName("Black Mesa");

		InstitutionCommonAccess ica = new InstitutionHibernateAccess();
		ica.add(i);

		Person p = new Person();
		p.setFirstName("Rumpo");
		p.setLastName("Derpel");
		p.setBirthdate(new Date(2010, 10, 10));

		Person p2 = new Person();
		p2.setPrefix("Prof. Dr.");
		p2.setFirstName("John");
		p2.setMiddleName("T.");
		p2.setLastName("Smith");
		p2.setBirthdate(new Date(1970 - 1900, 1 - 1, 1));
		p2.setObit(new Date(2038 - 1900, 1 - 1, 19));
		//				p2.setInstitution(i); FIXME if a person has this, a query with a result containing this person will result in an error

		PersonCommonAccess pca = new PersonHibernateAccess();
		pca.add(p);
		pca.add(p2);

		Paper pa = new Paper();
		pa.setHref("https://example.org");
		pa.setPdfFileSize(123456);
		pa.setReleaseDate(new Date(2018 - 1900, 11 - 1, 16));
		pa.setTopic("The Life, the Universe and Everything");
		pa.setTitle("42");

		PaperCommonAccess paca = new PaperHibernateAccess();
		paca.add(pa);

		ACL18WebParser acl18WebParser = new ACL18WebParser();
		try {
			acl18WebParser.storePapersandAuthors();
		}
		catch (IOException e){
			System.out.println("Jsoup broke:" + e.getCause());
		}
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