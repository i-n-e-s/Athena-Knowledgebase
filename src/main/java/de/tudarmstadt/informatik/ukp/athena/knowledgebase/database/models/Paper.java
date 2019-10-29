package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PaperJPAAccess;

@Entity
@Table(name="paper")
public class Paper extends Model {
	/*Identifier*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name = "paperID", updatable = false, nullable = false)
	private Long paperID;
	/*Title of the paper*/
	@Column(name = "title", columnDefinition = "varchar(1023)") //fixes titles that are too Long for being storable in the column
	private String title;
	/*Topic of the paper*/
	@Column(name = "topic")
	private String topic;
	/*Paper's authors*/
	@Hierarchy(entityName="person")
	@JsonIgnore
	@ManyToMany(mappedBy = "papers")
	private Set<Person> persons = new HashSet<>();
	
	
	@JsonIgnore
	/*Papers tags*/
	@Hierarchy(entityName="tag")
	@ManyToMany(mappedBy = "papers")
	private Set<Tag> tags = new HashSet<>();

	/*event the paper was published in*/
	@JsonIgnore
	@Hierarchy(entityName="event")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_paper")
	private Event event;
	

	/*eventpart the paper was published in*/
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@Hierarchy(entityName="eventpart")
	@JoinColumn(name = "eventpart_paper")
	private EventPart eventpart;
	

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

	/*amount of  Citations set by the SemanticScholar API*/
	@Column(name = "amountOfCitations")
	private Long amountOfCitations = (long)-1;    //-1 if not known yet
	/*Whole text of the paper by pdf parsing*/

	/*Abstract of paper as String either from pdf parsing or from aclanthology.info*/
	@Column(name = "paperAbstract", columnDefinition="LONGTEXT")
	private String paperAbstract;
	
	@Column(name= "plainText", columnDefinition = "LONGTEXT")
	private String paperPlainText;
	/*introduction of the paper from pdf parsing */
	@Column(name= "introduction", columnDefinition = "LONGTEXT")
	private String introduction;
	/* the related work section of the paper from pdf parsing */
	@Column(name= "relatedWork", columnDefinition = "LONGTEXT")
	private String relatedWork;
	/*result of the paper from pdf parsing */
	@Column(name= "result", columnDefinition = "LONGTEXT")
	private String result;
	/*discussion of the paper from pdf parsing */
	@Column(name= "discussion", columnDefinition = "LONGTEXT")
	private String discussion;
	/*conclusion of the paper from pdf parsing */
	@Column(name= "conclusion", columnDefinition = "LONGTEXT")
	private String conclusion;
	
	@Column(name= "dataset", columnDefinition = "LONGTEXT")
	private String dataset;
	@Column(name= "sectionNames")
	private String sectionNames;

	/**
	 * Get this paper's ID
	 * @return This paper's ID
	 */
	public Long getPaperID(){
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
	 * Gets this paper's authors
	 * @return A Set of this paper's authors
	 */
	public Set<Tag> getTags() {
		return tags;
	}

	/**
	 * Sets this paper's authors
	 * @param authors The new authors of this paper
	 */
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * Adds an author to this paper's author list
	 * @param author The author to add
	 */
	public void addTag(Tag tag) {
		tags.add(tag);
		if(!tag.getPapers().contains(this)) {
			tag.addPaper(this);
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
	 * @param localDate The new release date of this paper
	 */
	public void setReleaseDate(LocalDate localDate) {
		this.releaseDate = localDate;
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
	 * Gets the parsed plaintext of the paper
	 */
	public String getPaperPlainText() {
		return paperPlainText;
	}

	/**
	 * Sets this paper's plain text
	 *
	 * @param paperPlainText this paper's abstract
	 */
	public void setPaperPlainText(String paperPlainText) {
		this.paperPlainText = paperPlainText;
	}

	/**
	 * Gets the parsed introduction of the paper
	 * @return parsed introduction
	 */
	public String getIntroduction() {
		return introduction;
	}

	/**
	 * Sets this paper's introduction
	 *
	 * @param paperPlainText this paper's introduction
	 */
	public void setIntroduction(String introduction) { this.introduction = introduction;
	}

	/**
	 * Gets the parsed discussion of the paper
	 * @return paper discussion
	 */
	public String getDiscussion() {
		return discussion;
	}

	/**
	 * Sets this paper's discussion
	 *
	 * @param discussion this paper's discussion
	 */
	public void setDiscussion(String discussion) {
		this.discussion = discussion;
	}

	/**
	 * Gets the parsed result section of the paper
	 * @return paper result section
	 */
	public String getResult() {
		return result;
	}

	/**
	 * Sets this paper's plain text
	 *
	 * @param paperPlainText this paper's abstract
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * Gets the parsed related work section of the paper
	 */
	public String getRelatedWork() {
		return relatedWork;
	}

	/**
	 * Sets this paper's related work section
	 *
	 * @param paperPlainText this paper's abstract
	 */
	public void setRelatedWork(String relatedWork) {
		this.relatedWork = relatedWork;
	}

	/**
	 * Gets the parsed conclusion of the paper
	 * @return paper conclusion
	 */
	public String getConclusion() {
		return conclusion;
	}

	/**
	 * Sets this paper's conclusion
	 *
	 * @param paperPlainText this paper's conclusion
	 */
	public void setConclusion(String conclusion) {
		this.conclusion = conclusion;
	}

	/**
	 * Gets the parsed dataset section of the paper
	 */
	public String getDataset() {
		return dataset;
	}

	/**
	 * Sets this paper's dataset section
	 *
	 * @param paperPlainText this paper's abstract
	 */
	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	/**
	 *  Gets the section Names of the paper as one string
	 */
	public String getSectionNames() {
		return sectionNames;
	}

	/**
	 * Sets this paper's section names as one string
	 *
	 * @param paperPlainText this paper's abstract
	 */
	public void setSectionNames(String sectionNames) {
		this.sectionNames = sectionNames;
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
	 * @param name The paper title
	 * @param id the paper id, if unknown null
	 * @return A matching paper from the DB or a new paper
	 */
	public static Paper findOrCreate(String id, String name) {
		//Check if paper with same S2ID exists in DB
		PaperJPAAccess filer = new PaperJPAAccess();
		Paper searchResults = filer.getByKnownAttributes(id, name);

		if(searchResults == null){
			Paper p = new Paper();
			p.setTitle(name);
			p.setSemanticScholarID(id);
			filer.add(p);
			return p;
		}
		return searchResults;
	}
	
	
	/**
	 * Looks for papers with equal paperID in the DB and returns found entities
	 * If no matching DB entry was found, return null.
	 * Read more about the search here {@link PaperJPAAccess#getByPaperId(Paper)}
	 * @param id the paperID
	 * @return A matching paper from the DB or null
	 */
	public static Paper findById(String id) {
		//Check if paper with same S2ID exists in DB
				PaperJPAAccess filer = new PaperJPAAccess();
				Paper searchResult = filer.getByPaperId(id);
				return searchResult;
		
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
