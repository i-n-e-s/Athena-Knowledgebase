package de.tudarmstadt.informatik.ukp.athenakp.database;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;

import de.tudarmstadt.informatik.ukp.athenakp.Application;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Author;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Event;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Institution;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;

/**
 * A class to create a uniform database for testing purposes
 * 
 * @author Jonas Hake
 *
 */
public class Testdatabase {
	
	int ConferenceQuantity = 2;
	int InstitutionQuantity = 10;
	int AuthorQuantity = 100;
	int PaperQuantity = 50;
	int EventQuantity = 20;
	
	
	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
	/**
	 * Creates a database for testing purposes. The created entries are deterministic based on the given parameters. 
	 * All fields are set, if some fields should be empty they have to be manually removed.
	 * 
	 * 
	 * @param InstitutionQuantity The number of institutions the database should contain
	 * @param AuthorQuantity The number of authors the database should contain
	 * @param PaperQuantity The number of papers the database should contain
	 */
	public void create() {
		SpringApplication.run(Application.class,"");
		Conference conferences[] = new Conference[ConferenceQuantity];
		Institution institutions[] = new Institution[InstitutionQuantity];
		Author authors[] = new Author[AuthorQuantity];
		Paper papers[] = new Paper[PaperQuantity];
		Event events[] = new Event[EventQuantity];
		
		for(int i = 0; i< conferences.length;i++) {
			Conference c = new Conference();
			c.setName("Conference" + i);
			LocalDate tmpDate = LocalDate.of(1960 + i, i%12, i%28); 
			c.setStartDate(tmpDate);
			c.setEndDate(tmpDate.plusDays(1));
			c.setCountry("Testcountry" + i);
			c.setAddress("Testadress" + i);
		}
		
		for(int i = 0; i < institutions.length; i++) {
			Institution in = new Institution();
			in.setName("Institution" + i);
		}

		for(int i = 0; i<authors.length; i++) {
			Author a = new Author();
			
			a.setPrefix("Prefix" + i%2);
			a.setFullName("Author "+i);
			a.setBirthdate(LocalDate.of(1900+(i%70 + 30), i%12, i%28));
			a.setInstitution(institutions[i%11]);//Maybe some Data are not available
		}

		for(int i = 0; i< papers.length; i++) {
			Paper p = new Paper();
			HashSet<Author> tmpAuthors = findAuthorsForPaper(authors, i);
			for (Author a : tmpAuthors) {
				p.addAuthor(a);
			}
			p.setTopic("Topic" + i%4);
			p.setTitle("Title" + i);
			p.setHref("Link.test/" + i);
			p.setPdfFileSize(i+100);
			p.setAnthology("Ant" + i);
			
		}
		
		for (int i = 0; i < events.length; i++) {
			Event e = new Event();
			e.setBegin(LocalDateTime.of(LocalDate.of(2018, i%12, i%28),LocalTime.of(i%24, i%60)));
			//TODO Continue Event
		}

	}
	
	private HashSet<Author> findAuthorsForPaper(Author[] authors, int paperidx){
		HashSet<Author> result = new HashSet<Author>();
		Author author1 = authors[(paperidx*1) %authors.length];
		Author author2 = authors[(paperidx*2) %authors.length];
		Author author3 = authors[(paperidx*3) %authors.length];
		result.add(author1);
		result.add(author2);
		result.add(author3);
		return result;
	}
}
