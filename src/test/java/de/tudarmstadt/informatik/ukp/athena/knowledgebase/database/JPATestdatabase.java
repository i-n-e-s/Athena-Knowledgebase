package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.ConferenceJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.InstitutionJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PaperJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PersistenceManager;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PersonJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Institution;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;

@SpringBootApplication
public class JPATestdatabase {
	private int conferenceQuantity;
	private int institutionQuantity;
	private int authorQuantity;
	private int paperQuantity;
	private int eventQuantity;

	Conference conferences[];
	Institution institutions[];
	Person authors[];
	Paper papers[];

	public JPATestdatabase() {
		setDefaultParameters();
	}

	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		SpringApplication.run(JPATestdatabase.class,"");
		JPATestdatabase testdb = new JPATestdatabase();
		testdb.createDB();
	}

	/**
	 * Reset the Parameters to their default values
	 */
	public void setDefaultParameters() {
		conferenceQuantity = 2;
		institutionQuantity = 10;
		authorQuantity = 100;
		paperQuantity = 50;
		eventQuantity = 20;
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
		EntityManager entityManager = PersistenceManager.getEntityManager();
		entityManager.getTransaction().begin();
		List<String> list = entityManager.createNativeQuery("SHOW tables").getResultList();
		for (String tableName : list) {
			if(!tableName.equals("hibernate_sequence")) {//Needed because hibernate wont work without it
				entityManager.createNativeQuery(String.format("SET FOREIGN_KEY_CHECKS=0", tableName)).executeUpdate();
				entityManager.createNativeQuery(String.format("\nDELETE FROM %s \n WHERE 1", tableName)).executeUpdate();
				entityManager.createNativeQuery(String.format("SET FOREIGN_KEY_CHECKS=1", tableName)).executeUpdate();
			}
		}
		entityManager.getTransaction().commit();
		//entityManager.close();
		System.out.println("Done deleting");
	}

	/**
	 * Generates generic database objects based on set quantities
	 */
	public void generateData() {
		//Need to be one method, because data is linked
		System.out.println("Start creating data");
		conferences = new Conference[conferenceQuantity];
		institutions = new Institution[institutionQuantity];
		authors = new Person[authorQuantity];
		papers = new Paper[paperQuantity];

		for(int i = 0; i< conferences.length;i++) {
			conferences[i] = new Conference();
			conferences[i].setName("Conference" + i);
			LocalDate tmpDate = LocalDate.of(1960 + i, (i%12)+1 , (i%28)+1); 
			conferences[i].setBegin(tmpDate);
			conferences[i].setEnd(tmpDate.plusDays(1));
			conferences[i].setCountry("Testcountry" + i);
			conferences[i].setCity("Testcity" + i);
			conferences[i].setAddress("Testadress" + i);
		}

		for(int i = 0; i < institutions.length; i++) {
			institutions[i] = new Institution();
			institutions[i].setName("Institution" + i);
		}

		for(int i = 0; i<authors.length; i++) {
			authors[i] = new Person();
			authors[i].setPrefix("Prefix" + i%2);
			authors[i].setFullName("Author "+i);
			authors[i].setBirth(LocalDate.of(1900+(i%70 + 30), (i%12)+1 , (i%28)+1));
			authors[i].setInstitution(institutions[i%institutionQuantity]);//Maybe some Data are not available
		}

		for(int i = 0; i< papers.length; i++) {
			papers[i] = new Paper();
			HashSet<Person> tmpAuthors = findAuthorsForPaper(authors, i);
			for (Person a : tmpAuthors) {
				a.addPaper(papers[i]);
				papers[i].addAuthor(a);
			}
			papers[i].setTopic("Topic" + i%4);
			papers[i].setTitle("Title" + i);
			papers[i].setRemoteLink("Link.test/" + i);
			papers[i].setPdfFileSize(i+100);
			papers[i].setReleaseDate(LocalDate.of(i,i%12+1,i%28+1));
			papers[i].setAnthology("Ant" + i%25);

		}
		System.out.println("Done creating data");
	}

	/**
	 * insert data object into database. The data has to be generated or set first
	 */
	public void insertData() {
		ConferenceJPAAccess cjpaa = new ConferenceJPAAccess();
		InstitutionJPAAccess ijpaa = new InstitutionJPAAccess();
		PaperJPAAccess pajpaa = new PaperJPAAccess();
		PersonJPAAccess pejpaa = new PersonJPAAccess();
		//TODO add EventJPAAccess here
		System.out.println("Start inserting Data");

		for (Conference c : conferences) cjpaa.add(c);
		for (Institution i : institutions) ijpaa.add(i);
		for (Person a : authors) pejpaa.add(a);
		for (Paper p: papers)pajpaa.add(p);
		System.out.println("Done inserting data");
	}
	/**
	 * Generates a HashSet of Authors, which is deterministic.
	 * @param authors All authors
	 * @param paperidx The index of the paper
	 * @return 1-3 Authors for a paper
	 */
	private HashSet<Person> findAuthorsForPaper(Person[] authors, int paperidx){
		HashSet<Person> result = new HashSet<Person>();
		Person author1 = authors[(paperidx*1) %authors.length];
		Person author2 = authors[(paperidx*2) %authors.length];
		Person author3 = authors[(paperidx*3) %authors.length];
		result.add(author1);
		result.add(author2);
		result.add(author3);
		return result;
	}

	/**
	 * checks if an author with the same name already exists
	 * 
	 * @param a the author to be searched in the database
	 * @return true if an author-entry with the same name exists
	 */
/*	private boolean authorInDB(Author a) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		List<String> result = session.createSQLQuery(
				String.format("\nSELECT fullName FROM person \n WHERE fullName = '%s' \n LIMIT 1", a.getFullName() )).list();
		session.close();
		return !result.isEmpty();
	}*/

	/**
	 * checks if a paper with the same title already exists
	 * 
	 * @param a the paper to be searched in the database
	 * @return true if a paper-entry exists with the same title
	 */
/*	private boolean paperInDB(Paper p) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		List<String> result = session.createSQLQuery(
				String.format("\nSELECT title FROM paper \n WHERE title = '%s' \n LIMIT 1", p.getTitle() )).list();
		session.close();
		return !result.isEmpty();
	}*/

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
	 * institutionQuantity is the number of Institutions, which will be generated
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
	 * @param conferences The {@link Conference conferences}, which will be added
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
	 * @param institutions The {@link Institution institutions}, which will be added
	 */
	public void setInstitutions(Institution[] institutions) {
		this.institutions = institutions;
	}

	/**
	 * Returns the {@link Person authors}, which will be added to the database
	 * 
	 * @return {@link Person authors}, which were originally/will be added to the database
	 */
	public Person[] getAuthors() {
		return authors;
	}

	/**
	 * Set the {@link Person authors}, which will be added to the Database, when {@link #insertData()} is executed}
	 * 
	 * @param authors The {@link Person authors}, which will be added
	 */
	public void setAuthors(Person[] authors) {
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
}
