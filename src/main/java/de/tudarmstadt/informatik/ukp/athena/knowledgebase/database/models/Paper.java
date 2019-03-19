package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PaperJPAAccess;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="paper")
public class Paper extends Model {
	/*Identifier*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name = "paperID", updatable = false, nullable = false)
	private long paperID;

	/*Title of the paper*/
	@Column(name = "title", columnDefinition = "varchar(1023)") //fixes titles that are too long for being storable in the column
	private String title;
	/*Topic of the paper*/
	@Column(name = "topic")
	private String topic;
	/*Paper's authors*/
	@Hierarchy(entityName="person")
	@JsonIgnore //fixes infinite recursion
	@ManyToMany(cascade = { CascadeType.ALL }, mappedBy = "papers", fetch = FetchType.EAGER)
	private Set<Person> persons = new HashSet<>();

	/*Release date*/
	@Column(name = "releaseDate")
	private LocalDate releaseDate;

	/*URL to PDF*/
	@Column(name = "remoteLink")
	private String remoteLink;
	@Column(name = "localLink")
	private String localLink;

	/*PDF filesize in Bytes*/
	@Column(name = "pdfFileSize")
	private Integer pdfFileSize;
	/*anthology of paper as String*/
	@Column (name = "anthology")
	private String anthology;

	/*SemanticScholars PaperId as String*/
	@Column(name = "semanticScholarID")
	private String semanticScholarID;
	/*Abstract of paper as String*/
	@Column(name = "paperAbstract", columnDefinition="TEXT")
	private String paperAbstract;
	@Column(name = "amountOfCitations")
	private Integer amountOfCitations = -1;    //-1 if not known yet

	//	Removed all code concerning quotations and alike. Too time consuming right now.

	//	TODO: Metadata?

	/**
	 * Get this paper's ID
	 * @return This paper's ID
	 */
	public long getPaperID(){
		return paperID;
	}

	// TODO: Rename to getPersons after Testbench integration?
	/**
	 * Gets List of this paper's authors
	 * @return List of this paper's authors
	 */
	public Set<Person> getAuthors() {
		return persons;
	}

	// TODO: Rename to setPersons after Testbench integration?
	/**
	 * Sets this paper's authors
	 * @param authors The new author of this paper
	 */
	public void setAuthors(Set<Person> authors) {
		this.persons = authors;
	}

	// TODO: Rename to addPerson after Testbench integration?
	/**
	 * Adds an author to this paper's author list
	 * @param author The author to add
	 */
	public void addAuthor(Person author) {
		persons.add(author);
		if(!author.getPapers().contains(this)) {
			author.addPaper(this);
		}
	}

	/**
	 * Gets this paper's release date
	 * @return This paper's release date
	 */
	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	/**
	 * Sets this paper's release date
	 * @param releaseDate The new release date of this paper
	 */
	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}

	/**
	 * Gets this papers topic
	 *
	 * @return The topic of this paper
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * Sets this institution's topic
	 *
	 * @param topic The new topic
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	 * Gets this papers title
	 *
	 * @return The title of this paper
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets this institution's title
	 *
	 * @param title The new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the remote link to this papers PDF file
	 *
	 * @return The remote link to this papers PDF file
	 */
	public String getRemoteLink() {
		return remoteLink;
	}

	/**
	 * Sets the remote link to this papers PDF file
	 *
	 * @param remoteLink The new remote link to this papers PDF file
	 */
	public void setRemoteLink(String remoteLink) {
		this.remoteLink = remoteLink;
	}

	/**
	 * Gets the local link to this papers PDF file
	 *
	 * @return The local link to this papers PDF file
	 */
	public String getLocalLink() {
		return localLink;
	}

	/**
	 * Sets the local link to this PDF file
	 *
	 * @param localLink The new local link to this papers PDF file
	 */
	public void setLocalLink(String localLink) {
		this.localLink = localLink;
	}

	/**
	 * Gets the filesize of this papers PDF file in Bytes
	 *
	 * @return The filesize of this papers PDF file in Bytes
	 */
	public Integer getPdfFileSize() {
		return pdfFileSize;
	}

	/**
	 * Sets the filesize of this papers PDF file in Bytes
	 *
	 * @param pdfFileSize The new filesize of this papers PDF file in Bytes
	 */
	public void setPdfFileSize(Integer pdfFileSize) {
		this.pdfFileSize = pdfFileSize;
	}

	/**
	 * Gets the paper's anthology
	 * @return the paper's anthology
	 */
	public String getAnthology() {
		return anthology;
	}

	/**
	 * Sets the paper's anthology
	 * @param anthology anthology of the paper as String
	 */
	public void setAnthology(String anthology) {
		this.anthology = anthology;
	}

	/**
	 * Gets the paper's ID on Semantic Scholar
	 *
	 * @return the papers SemanticScholar ID
	 */
	public String getSemanticScholarID() {
		return semanticScholarID;
	}

	/**
	 * Sets the paper's ID on Semantic Scholar
	 *
	 * @param semanticScholarID the papers new SemanticScholar ID
	 */
	public void setSemanticScholarID(String semanticScholarID) {
		this.semanticScholarID = semanticScholarID;
	}

	/**
	 * Gets the paper's abstract
	 *
	 * @return the papers abstract
	 */
	public String getPaperAbstract() {
		return paperAbstract;
	}

	/**
	 * Sets the paper's abstract
	 *
	 * @param paperAbstract the papers abstract
	 */
	public void setPaperAbstract(String paperAbstract) {
		this.paperAbstract = paperAbstract;
	}

	/**
	 * Gets the amount of papers, this paper is cited in
	 * @return the amount of citations
	 */
	public Integer getAmountOfCitations() {
		return amountOfCitations;
	}

	/**
	 * Sets the amount of papers, this paper is cited in
	 * @param amountOfCitations the new amount of citations
	 */
	public void setAmountOfCitations(Integer amountOfCitations) {
		this.amountOfCitations = amountOfCitations;
	}


	/**
	 * Looks for Persons with equal attributes in the DB and returns found entities
	 * If no matching DB entry was found, create and return a new Person Object
	 * @param toFind The Person Object containing the query data
	 * @return A matching Person from the DB or a new Person
	 */
	public static Paper findOrCreate(Paper toFind) {
		//Check if Paper with same S2ID exists in DB
		PaperJPAAccess filer = new PaperJPAAccess();
		List<Paper> searchResults = filer.getByKnownAttributes(toFind);

		if(searchResults == null || searchResults.size() < 1) { //No matching Paper could be found in the DB
			return new Paper();
		}
		else { 		//Choose first result
			return searchResults.get(0);
		}
	}

	/**
	 * Looks for papers with defined title or SemanticScholarID and returns matching DB Entry
	 * If no match was found, create and return new Paper Object
	 * @param s2id SemanticScholarID of searched paper or null if unknown
	 * @param title Title of searched Paper or null if unknown
	 * @return matching DB entry or new Paper
	 */
	public static Paper findOrCreate(String s2id, String title) {
		Paper tmpQuery = new Paper();
		tmpQuery.setTitle(title);
		tmpQuery.setSemanticScholarID(s2id);
		return findOrCreate(tmpQuery);
	}


	/**
	 * Creates a String representation of the Person Object.
	 * Warning: String does not contain all information in the Object
	 * @return String description of the Object
	 */
	@Override
	public String toString() {
		String ret = "{title: " + this.getTitle() + ",";
		ret = ret + "PaperID: " + this.getPaperID() + "}";
		ret = ret + "topic: " + this.getTopic() + ",";
		ret = ret + "Authors: ";
		for (Person a : this.getAuthors()) {
			ret = ret + "{ name:"+ a.getFullName() +", personID:"+ a.getPersonID() +", S2ID:" +a.getSemanticScholarID() + "}";
		}
		ret = ret + "S2ID: " + this.getSemanticScholarID() + ",";
		ret = ret + "abstract: " + this.getPaperAbstract() + ",";
		// ret = ret + "prefix: " + this.get + "\n";
		return ret;
	}

}
