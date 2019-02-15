package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.hibernate.PersonHibernateAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PaperJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PersonJPAAccess;
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
	private Set<Person> authors = new HashSet<>();

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

	/**
	 * Gets List of this paper's authors
	 * @return List of this paper's authors
	 */
	public Set<Person> getAuthors() {
		return authors;
	}

	/**
	 * Sets this paper's authors
	 * @param authors The new author of this paper
	 */
	public void setAuthors(Set<Person> authors) {
		this.authors = authors;
	}

	/**
	 * Adds an author to this paper's author list
	 * @param author The author to add
	 */
	public void addAuthor(Person author) {
		authors.add(author);
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


	public boolean complementBy(Paper srcPaper, boolean overwrite) {

		//Initialize return value
		boolean changed = false;
		PersonJPAAccess personFiler = new PersonJPAAccess();

		//1. Complement all prime Attributes
		changed = complementPrimeAttributesBy(srcPaper, overwrite);

		//2. Copy all Authors
		copyAllAuthorsLoop:
		for (Person srcAuthor : srcPaper.getAuthors()) {

			//Check if Author is already known in paper
			for ( Person author : this.getAuthors() ) {
				if ( srcAuthor.equalsNullAsWildcard(author) ) {
					changed = Model.connectAuthorPaper( author, this );
					author.complementBy(srcAuthor);
				}
			}

			//Check if Author is already in DB
			Person inDB = personFiler.lookUpPerson(srcAuthor);    //Check if we know the author already in DB
			if (inDB != null) {
				inDB.complementBy(srcAuthor);
				changed = Model.connectAuthorPaper(inDB, this);
			}


			Model.connectAuthorPaper(srcAuthor, this);
		}

		//3. Eliminate duplicates
		return changed;
	}

	private boolean complementPrimeAttributesBy( Paper srcPaper, boolean overwrite ) {

		boolean changed = false;

		//1. Copy all prime Attributes
		if ( (this.getTopic() == null && srcPaper.getTopic() != null) || overwrite) {
			this.setTopic(srcPaper.getTopic());
			changed = true;
		}
		if ( (this.getPaperAbstract() == null && srcPaper.getPaperAbstract() != null) || overwrite) {
			this.setPaperAbstract(srcPaper.getPaperAbstract());
			changed = true;
		}
		if ( (this.getTitle() == null && srcPaper.getTitle() != null) || overwrite) {
			this.setTitle(srcPaper.getTitle());
			changed = true;
		}
		if ( (this.getReleaseDate() == null && srcPaper.getReleaseDate() != null) || overwrite) {
			this.setReleaseDate(srcPaper.getReleaseDate());
			changed = true;
		}
		if ( (this.getRemoteLink() == null && srcPaper.getRemoteLink() != null) || overwrite) {
			this.setRemoteLink(srcPaper.getRemoteLink());
			changed = true;
		}
		if ( (this.getPdfFileSize() == null && srcPaper.getPdfFileSize() != null) || overwrite) {
			this.setPdfFileSize(srcPaper.getPdfFileSize());
			changed = true;
		}
		if ( (this.getAnthology() == null && srcPaper.getAnthology() != null) || overwrite) {
			this.setAnthology(srcPaper.getAnthology());
			changed = true;
		}
		if ( (this.getSemanticScholarID() == null && srcPaper.getSemanticScholarID() != null) || overwrite) {
			this.setSemanticScholarID(srcPaper.getSemanticScholarID());
			changed = true;
		}
		if ( (this.getAmountOfCitations() == null && srcPaper.getAmountOfCitations() != null) || overwrite) {
			this.setAmountOfCitations(srcPaper.getAmountOfCitations());
			changed = true;
		}
		return changed;
	}


	//TODO nochmal richtig
	@Override
	public String toString() {
		String ret = "{title: " + this.getTitle() + ",";
		ret = ret + "topic: " + this.getTopic() + ",";
		ret = ret + "Authors: ";
		for (Person a : this.getAuthors()) {
			ret = ret + "{ name:"+ a.getFullName() +", personID:"+ a.getPersonID() +", S2ID:" +a.getSemanticScholarID() + "}";
		}
		ret = ret + "S2ID: " + this.getSemanticScholarID() + ",";
		ret = ret + "abstract: " + this.getPaperAbstract() + ",";
		ret = ret + "PaperID: " + this.getPaperID() + "}";
		// ret = ret + "prefix: " + this.get + "\n";
		return ret;
	}
}
