package de.tudarmstadt.informatik.ukp.athenakp.database;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.hibernate.Session;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.ConferenceHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.HibernateUtils;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.InstitutionHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.PaperHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.PersonHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.WorkshopHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Author;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Event;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Institution;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Workshop;

/**
 * A class to create a uniform database for testing purposes
 *
 * @author Jonas Hake
 *
 */
@SpringBootApplication
public class Testdatabase {

	private int conferenceQuantity;
	private int institutionQuantity;
	private int authorQuantity;
	private int paperQuantity;
	private int eventQuantity;
	private int workshopQuantity;

	Conference conferences[];
	Institution institutions[];
	Author authors[];
	Paper papers[];
	Event events[];
	Workshop workshops[];

	public Testdatabase() {
		setDefaultParameters();
	}

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
	 * Reset the Parameters to there default values
	 */
	public void setDefaultParameters() {
		conferenceQuantity = 2;
		institutionQuantity = 10;
		authorQuantity = 100;
		paperQuantity = 50;
		eventQuantity = 20;
		workshopQuantity = 20;
	}

	/**
	 * Creates a database for testing purposes. The created entries are deterministic based on the given parameters.
	 * The set
	 * <b>WARNING:</b> This method deletes the whole database 'athena' and replaces it! If you just want to insert new
	 * data use {@link #generateData()} and {@link #insertData()}
	 */
	public void createDB() {
		deleteOldData();
		generateData();
		insertData();
	}

	/**
	 * Deletes all tables in the athena-database, except hibernate_sequence
	 */
	public void deleteOldData() {
		System.out.println("Start deleting");
		Session session = HibernateUtils.getSessionFactory().openSession();
		List<String> list = session.createSQLQuery("SHOW tables").list();
		session.beginTransaction();
		for (String tableName : list) {
			if(!tableName.equals("hibernate_sequence")) {//Needed because hibernate wont work without it
				session.createSQLQuery(String.format("SET FOREIGN_KEY_CHECKS=0", tableName)).executeUpdate();
				session.createSQLQuery(String.format("\nDELETE FROM %s \n WHERE 1", tableName)).executeUpdate();
				session.createSQLQuery(String.format("SET FOREIGN_KEY_CHECKS=1", tableName)).executeUpdate();
			}
		}
		session.getTransaction().commit();
		session.close();
		System.out.println("Done deleting");
	}

	/**
	 * Generates generic hibernate objects based on set quantities
	 */
	public void generateData() {
		//Need to be one method, because data is linked
		System.out.println("Start creating data");
		conferences = new Conference[conferenceQuantity];
		institutions = new Institution[institutionQuantity];
		authors = new Author[authorQuantity];
		papers = new Paper[paperQuantity];
		events = new Event[eventQuantity];
		workshops = new Workshop[workshopQuantity];

		for(int i = 0; i< conferences.length;i++) {
			conferences[i] = new Conference();
			conferences[i].setName("Conference" + i);
			LocalDate tmpDate = LocalDate.of(1960 + i, (i%12)+1 , (i%28)+1);
			conferences[i].setStartDate(tmpDate);
			conferences[i].setEndDate(tmpDate.plusDays(1));
			conferences[i].setCountry("Testcountry" + i);
			conferences[i].setCity("Testcity" + i);
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
				a.addPaper(papers[i]);
				papers[i].addAuthor(a);
			}
			papers[i].setTopic("Topic" + i%4);
			papers[i].setTitle("Title" + i);
			papers[i].setHref("Link.test/" + i);
			papers[i].setPdfFileSize(i+100);
			papers[i].setReleaseDate(LocalDate.of(i,i%12+1,i%28+1));
			papers[i].setAnthology("Ant" + i%25);

		}

		for (int i = 0; i < events.length; i++) {
			events[i] = new Event();
			LocalDateTime tmpDateTime= LocalDateTime.of(LocalDate.of(2018, (i%12)+1 , (i%28)+1),LocalTime.of(i%24, i%60));
			events[i].setBegin(tmpDateTime);
			events[i].setEnd(tmpDateTime.plusHours(1));
			events[i].setPlace("Place" + i);
			events[i].setTitle("EventTitle" + i);
			events[i].setDescription("Description" + i);
		}

		for(int i = 0; i< workshops.length; i++) {
			workshops[i] = new Workshop();
			workshops[i].setConference("Conference" + i);
			LocalDateTime tmpDateTime= LocalDateTime.of(LocalDate.of(2018, (i%12)+1 , (i%28)+1),LocalTime.of(i%24, i%60));
			workshops[i].setBegin(tmpDateTime);
			workshops[i].setEnd(tmpDateTime.plusHours(1));
			workshops[i].setPlace("Place" + i);
			workshops[i].setTitle("Title" + i);
			workshops[i].setAbbreviation("Abbreviation" + i);
		}
		System.out.println("Done creating data");
	}

	/**
	 * insert data object into database. The data have to be generated or set first
	 */
	public void insertData() {
		System.out.println("Start inserting data");
		ConferenceHibernateAccess cha = new ConferenceHibernateAccess();
		InstitutionHibernateAccess iha = new InstitutionHibernateAccess();
		PaperHibernateAccess paha = new PaperHibernateAccess();
		PersonHibernateAccess peha = new PersonHibernateAccess();
		//TODO add EventHibernateAccess here
		WorkshopHibernateAccess wha = new WorkshopHibernateAccess();
		System.out.println("Start inserting Data");

		//FIXME multiple Persons and Papers are added into db, presumably because of the multiple Accesses
		for (Conference c : conferences) cha.add(c);
		for (Institution i : institutions) iha.add(i);
		for (Author a : authors) {
			if(!authorInDB(a)) peha.add(a);
		}
		for (Paper p: papers) {
			if(!paperInDB(p)) paha.add(p);
		}
		for(Workshop w : workshops) wha.add(w);
		System.out.println("Done inserting data");
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
	 *
	 * checks if an author with the same name already exist
	 *
	 * @param a the author, which should be searched in the database
	 * @return true if an author-entry exist with the same name
	 */
	private boolean authorInDB(Author a) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		List<String> result = session.createSQLQuery(
				String.format("\nSELECT fullName FROM person \n WHERE fullName = '%s' \n LIMIT 1", a.getFullName() )).list();
		session.close();
		return !result.isEmpty();
	}

	/**
	 *
	 * checks if an paper with the same title already exist
	 *
	 * @param a the paper, which should be searched in the database
	 * @return true if an paper-entry exist with the same title
	 */
	private boolean paperInDB(Paper p) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		List<String> result = session.createSQLQuery(
				String.format("\nSELECT title FROM paper \n WHERE title = '%s' \n LIMIT 1", p.getTitle() )).list();
		session.close();
		return !result.isEmpty();
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

	/**
	 * Returns the {@link Conference conferences}, which will be added to the conference
	 *
	 * @return {@link Conference conferences}, which will be added to the Database
	 */
	public Conference[] getConferences() {
		return conferences;
	}

	/**
	 * Set the {@link Conference conferences}, which will be added to the Database, when {@link #insertData()} is executed}
	 *
	 * @param Conferences The {@link Conference conferences}, which will be added
	 */
	public void setConferences(Conference[] conferences) {
		this.conferences = conferences;
	}

	/**
	 * Returns the {@link Institution institutions}, which will be added to the database
	 *
	 * @return {@link Institution institutions}, which were originally/will be added to the Database
	 */
	public Institution[] getInstitutions() {
		return institutions;
	}

	/**
	 * Set the {@link Institution institutions}, which will be added to the Database, when {@link #insertData()} is executed}
	 *
	 * @param Institutions The {@link Institution institutions}, which will be added
	 */
	public void setInstitutions(Institution[] institutions) {
		this.institutions = institutions;
	}

	/**
	 * Returns the {@link Author authors}, which will be added to the database
	 *
	 * @return {@link Author authors}, which were originally/will be added to the database
	 */
	public Author[] getAuthors() {
		return authors;
	}

	/**
	 * Set the {@link Author authors}, which will be added to the Database, when {@link #insertData()} is executed}
	 *
	 * @param authors The {@link Author authors}, which will be added
	 */
	public void setAuthors(Author[] authors) {
		this.authors = authors;
	}

	/**
	 * Returns the {@link Paper papers}, which will be added to the database
	 *
	 * @return {@link Paper papers}, which were originally/will be added to the database
	 */
	public Paper[] getPapers() {
		return papers;
	}

	/**
	 * Set the {@link Paper papers}, which will be added to the Database, when {@link #insertData()} is executed}
	 *
	 * @param papers The {@link Paper papers}, which will be added
	 */
	public void setPapers(Paper[] papers) {
		this.papers = papers;
	}

	/**
	 * Returns the {@link Event events}, which will be added to the database
	 *
	 * @return {@link Event events}, which were originally/will be added to the Database
	 */
	public Event[] getEvents() {
		return events;
	}

	/**
	 * Set the {@link Event events}, which will be added to the Database, when {@link #insertData()} is executed}
	 *
	 * @param events The {@link Event events}, which will be added
	 */
	public void setEvents(Event[] events) {
		this.events = events;
	}

	/**
	 * Returns the {@link Workshop workshops}, which will be added to the database
	 *
	 * @return {@link Workshop workshops}, which were originally/will be added to the Database
	 */
	public Workshop[] getWorkshops() {
		return workshops;
	}

	/**
	 * Set the {@link Workshop workshops}, which will be added to the Database, when {@link #insertData()} is executed}
	 *
	 * @param workshops The {@link Workshop workshops}, which will be added
	 */
	public void setWorkshops(Workshop[] workshops) {
		this.workshops = workshops;
	}
}
