package de.tudarmstadt.informatik.ukp.athenakp.database;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.ConferenceHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.InstitutionHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.PaperHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.PersonHibernateAccess;
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
@SpringBootApplication
public class Testdatabase {

	private int conferenceQuantity = 2;
	private int institutionQuantity = 10;
	private int authorQuantity = 100;
	private int paperQuantity = 50;
	private int eventQuantity = 20;

	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Testdatabase.class,"");
		Testdatabase testdb = new Testdatabase();
		testdb.createDB();
	}
	
	/**
	 * Creates a database for testing purposes. The created entries are deterministic based on the given parameters. 
	 * All fields are set, if some fields should be empty they have to be manually removed. The 
	 */
	public void createDB() {
		System.out.println("Start creating Data");
		Conference conferences[] = new Conference[conferenceQuantity];
		Institution institutions[] = new Institution[institutionQuantity];
		Author authors[] = new Author[authorQuantity];
		Paper papers[] = new Paper[paperQuantity];
		Event events[] = new Event[eventQuantity];

		for(int i = 0; i< conferences.length;i++) {
			conferences[i] = new Conference();
			conferences[i].setName("Conference" + i);
			LocalDate tmpDate = LocalDate.of(1960 + i, (i%12)+1 , (i%28)+1); 
			conferences[i].setStartDate(tmpDate);
			conferences[i].setEndDate(tmpDate.plusDays(1));
			conferences[i].setCountry("Testcountry" + i);
			conferences[i].setAddress("Testadress" + i);
		}

		for(int i = 0; i < institutions.length; i++) {
			institutions[i] = new Institution();
			institutions[i].setName("Institution" + i);
		}

		for(int i = 0; i<authors.length; i++) {
			authors[i] = new Author();

			authors[i].setPrefix("Prefix" + i%2);
			authors[i].setFullName("Author "+i);
			authors[i].setBirthdate(LocalDate.of(1900+(i%70 + 30), (i%12)+1 , (i%28)+1));
			authors[i].setInstitution(institutions[i%institutionQuantity]);//Maybe some Data are not available
		}

		for(int i = 0; i< papers.length; i++) {
			papers[i] = new Paper();
			HashSet<Author> tmpAuthors = findAuthorsForPaper(authors, i);
			for (Author a : tmpAuthors) {
				papers[i].addAuthor(a);
			}
			papers[i].setTopic("Topic" + i%4);
			papers[i].setTitle("Title" + i);
			papers[i].setHref("Link.test/" + i);
			papers[i].setPdfFileSize(i+100);
			papers[i].setAnthology("Ant" + i);

		}

		for (int i = 0; i < events.length; i++) {
			events[i] = new Event();
			LocalDateTime tmpDateTime= LocalDateTime.of(LocalDate.of(2018, (i%12)+1 , (i%28)+1),LocalTime.of(i%24, i%60)); 
			events[i].setBegin(tmpDateTime);
			events[i].setEnd(tmpDateTime.plusHours(1));
			events[i].setPlace("Place" + i);
			events[i].setTitle("EventTitle" + i);
			events[i].setShortDescription("Description" + i);
		}

		ConferenceHibernateAccess cha = new ConferenceHibernateAccess();
		InstitutionHibernateAccess iha = new InstitutionHibernateAccess();
		PaperHibernateAccess paha = new PaperHibernateAccess();
		PersonHibernateAccess peha = new PersonHibernateAccess();

		System.out.println("Start inserting Data");
		
		for (Conference c : conferences) cha.add(c);
		for (Institution i : institutions) iha.add(i);
		for (Author a : authors) peha.add(a);
		for (Paper p: papers) paha.add(p);
		
		System.out.println("Done inserting Data");
	}

	/**
	 * Generates a HashSet of Authors, which is deterministic.
	 * @param authors All authors
	 * @param paperidx The index of the paper
	 * @return 1-3 Authors for a paper
	 */
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

	/**
	 * conferenceQuantity is the number of Conferences, which will be generated
	 * 
	 * @return The current ConferenceQuantity
	 */
	public int getConferenceQuantity() {
		return conferenceQuantity;
	}
	
	/**
	 * conferenceQuantity is the number of Conferences, which will be generated
	 * 
	 * @param conferenceQuantity The desired ConferenceQuantity
	 */
	public void setConferenceQuantity(int conferenceQuantity) {
		this.conferenceQuantity = conferenceQuantity;
	}
	
	/**
	 * institutionQuantity is the number of Institution, which will be generated
	 * 
	 * @return The current institutionQuantity
	 */
	public int getInstitutionQuantity() {
		return institutionQuantity;
	}
	
	/**
	 * institutionQuantity is the number of Institution, which will be generated
	 * 
	 * @param institutionQuantity The desired institutionQuantity
	 */
	public void setInstitutionQuantity(int institutionQuantity) {
		this.institutionQuantity = institutionQuantity;
	}
	
	/**
	 * authorQuantity is the number of Authors, which will be generated
	 * 
	 * @return authorQuantity The current authorQuantity
	 */
	public int getAuthorQuantity() {
		return authorQuantity;
	}
	
	/**
	 * paperQuantity is the number of Papers, which will be generated
	 * 
	 * @param authorQuantity The desired paperQuantity
	 */
	public void setAuthorQuantity(int authorQuantity) {
		this.authorQuantity = authorQuantity;
	}
	
	/**
	 * paperQuantity is the number of Papers, which will be generated
	 * 
	 * @return paperQuantity The current paperQuantity
	 */
	public int getPaperQuantity() {
		return paperQuantity;
	}
	
	/**
	 * paperQuantity is the number of Papers, which will be generated
	 * 
	 * @param paperQuantity The desired paperQuantity
	 */
	public void setPaperQuantity(int paperQuantity) {
		this.paperQuantity = paperQuantity;
	}
	
	/**
	 * eventQuantity is the number of Events, which will be generated
	 * 
	 * @return The current eventQuantity
	 */
	public int getEventQuantity() {
		return eventQuantity;
	}
	
	/**
	 * eventQuantity is the number of Events, which will be generated
	 * 
	 * @param eventQuantity The desired eventQuantity
	 */
	public void setEventQuantity(int eventQuantity) {
		this.eventQuantity = eventQuantity;
	}
}
