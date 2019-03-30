package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PaperJPAAccess;

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

	/*Semantic Scholar's PaperId as String*/
	@Column(name = "semanticScholarID")
	private String semanticScholarID;
	/*Abstract of paper as String*/
	@Column(name = "paperAbstract", columnDefinition="TEXT")
	private String paperAbstract;
	@Column(name = "amountOfCitations")
	private long amountOfCitations = -1;    //-1 if not known yet

	/**
	 * Get this paper's ID
	 * @return This paper's ID
	 */
	public long getPaperID(){
		return paperID;
	}

	/**
	 * Gets this paper's authors
	 * @return A Set of this paper's authors
	 */
	public Set<Person> getAuthors() {
		return persons;
	}

	/**
	 * Sets this paper's authors
	 * @param authors The new authors of this paper
	 */
	public void setAuthors(Set<Person> authors) {
		this.persons = authors;
	}

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
	 * Gets this paper's topic
	 *
	 * @return The topic of this paper
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * Sets this paper's topic
	 *
	 * @param topic The new topic
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	 * Gets this paper's title
	 *
	 * @return The title of this paper
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets this paper's title
	 *
	 * @param title The new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the remote link to this paper's PDF file
	 *
	 * @return The remote link to this paper's PDF file
	 */
	public String getRemoteLink() {
		return remoteLink;
	}

	/**
	 * Sets the remote link to this paper's PDF file
	 *
	 * @param remoteLink The new remote link to this paper's PDF file
	 */
	public void setRemoteLink(String remoteLink) {
		this.remoteLink = remoteLink;
	}

	/**
	 * Gets the local link to this paper's PDF file
	 *
	 * @return The local link to this paper's PDF file
	 */
	public String getLocalLink() {
		return localLink;
	}

	/**
	 * Sets the local link to this paper's PDF file
	 *
	 * @param localLink The new local link to this paper's PDF file
	 */
	public void setLocalLink(String localLink) {
		this.localLink = localLink;
	}

	/**
	 * Gets the filesize of this paper's PDF file in bytes
	 *
	 * @return The filesize of this paper's PDF file in bytes
	 */
	public Integer getPdfFileSize() {
		return pdfFileSize;
	}

	/**
	 * Sets the filesize of this paper's PDF file in bytes
	 *
	 * @param pdfFileSize The new filesize of this paper's PDF file in bytes
	 */
	public void setPdfFileSize(Integer pdfFileSize) {
		this.pdfFileSize = pdfFileSize;
	}

	/**
	 * Gets this paper's anthology
	 * @return this paper's anthology
	 */
	public String getAnthology() {
		return anthology;
	}

	/**
	 * Sets this paper's anthology
	 * @param anthology The anthology of this paper as a string
	 */
	public void setAnthology(String anthology) {
		this.anthology = anthology;
	}

	/**
	 * Gets this paper's ID on Semantic Scholar
	 *
	 * @return this paper's Semantic Scholar ID
	 */
	public String getSemanticScholarID() {
		return semanticScholarID;
	}

	/**
	 * Sets this paper's Semantic Scholar ID
	 *
	 * @param semanticScholarID the paper's updated Semantic Scholar ID
	 */
	public void setSemanticScholarID(String semanticScholarID) {
		this.semanticScholarID = semanticScholarID;
	}

	/**
	 * Gets this paper's abstract
	 *
	 * @return this paper's abstract
	 */
	public String getPaperAbstract() {
		return paperAbstract;
	}

	/**
	 * Sets this paper's abstract
	 *
	 * @param paperAbstract this paper's abstract
	 */
	public void setPaperAbstract(String paperAbstract) {
		this.paperAbstract = paperAbstract;
	}

	/**
	 * Gets the amount of papers this paper is cited in
	 * @return the amount of citations, -1 if not known
	 */
	public Long getAmountOfCitations() {
		return amountOfCitations;
	}

	/**
	 * Sets the amount of papers this paper is cited in
	 * @param amountOfCitations the new amount of citations
	 */
	public void setAmountOfCitations(Long amountOfCitations) {
		this.amountOfCitations = amountOfCitations;
	}


	/**
	 * Looks for papers with equal attributes in the DB and returns found entities
	 * If no matching DB entry was found, create and return a new paper object
	 * Read more about the search here {@link PaperJPAAccess#getByKnownAttributes(Paper)}
	 * @param toFind The paper object containing the query data
	 * @return A matching paper from the DB or a new paper
	 */
	public static Paper findOrCreate(Paper toFind) {
		//Check if paper with same S2ID exists in DB
		PaperJPAAccess filer = new PaperJPAAccess();
		List<Paper> searchResults = filer.getByKnownAttributes(toFind);

		if(searchResults == null || searchResults.size() < 1) { //No matching paper could be found in the DB
			return new Paper();
		}
		else { 		//Choose first result
			return searchResults.get(0);
		}
	}

	/**
	 * Looks for papers with defined title or Semantic Scholar ID and returns matching DB entry
	 * If no match was found, create and return a new paper object
	 * @param s2id Semantic Scholar ID of the searched paper or null if unknown
	 * @param title The title of the searched paper or null if unknown
	 * @return matching DB entry or new paper
	 */
	public static Paper findOrCreate(String s2id, String title) {
		Paper tmpQuery = new Paper();
		tmpQuery.setTitle(title);
		tmpQuery.setSemanticScholarID(s2id);
		return findOrCreate(tmpQuery);
	}


	/**
	 * Same as {@link Paper#findOrCreate(String, String)}, but also searches in given list
	 * @param s2id Semantic Scholar id of the paper to search
	 * @param title Title of the paper to seach
	 * @param list List to be searched
	 * @return A matching paper from the list or the DB, or a new paper
	 */
	public static Paper findOrCreateDbOrList(String s2id, String title, List<Paper> list) {
		//Filter out any paper who does not have either a matching SemanticScholarID or matching title
		List<Paper> result = list.stream().filter( currPaper -> (
				( currPaper.getSemanticScholarID() != null && currPaper.getSemanticScholarID().equals(s2id)) ||
				( currPaper.getTitle() != null && currPaper.getTitle().equals(title) ))).collect(Collectors.toList());

		//Result now contains only persons with either matching SemanticScholarID or matching name
		return result.size() > 0 ? result.get(0) : findOrCreate(s2id, title);
	}


	/**
	 * Creates a String representation of this paper object.
	 * Warning: String does not contain all information in the object
	 * @return String description of the object
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
