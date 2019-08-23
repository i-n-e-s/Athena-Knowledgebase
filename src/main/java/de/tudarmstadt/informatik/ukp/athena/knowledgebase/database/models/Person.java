package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PersonJPAAccess;

/**
 * @author Tristan Wettich, Philipp Emmer
 */
@Entity
@Table(name = "person")
public class Person extends Model {
	/*Unique id*/
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "personID", updatable = false, nullable = false)
	private Long personID;

	/*Prefixes like academic titles*/
	@Column(name = "prefix")
	private String prefix;
	/*Full Name*/
	@Column(name = "fullName", nullable = false)
	private String fullName;
	
	/*First Name*/
//	@Column(name = "firstName")
	private String firstName;
	
	/*Full Name*/
//	@Column(name = "lastName")
	private String lastName;
	
	
	

	/*Birthday and day of death*/
	@Column(name = "birth")
	private String birth;
	@Column(name = "obit")
	private String obit;

	/*The person's institution, eg. an university or a company*/
	//@Column(name = "institution")
	@JsonIgnore
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "institutionID")
	private Institution institution;

	/*Written papers*/
	@Hierarchy(entityName="paper")
	@JsonIgnore //fixes infinite recursion
	@ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable(
			name = "author_paper",
			joinColumns = { @JoinColumn(name = "authorID") },
			inverseJoinColumns = { @JoinColumn(name = "paperID") }
			)
	private Set<Paper> papers = new HashSet<>();

	/* The persons SemanticScholar ID */
	@Column(name = "semanticScholarID")
	private String semanticScholarID = null;

	@ManyToOne(cascade={ CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name="influencedBy1")
	private Person top1influencedBy = null;    //Authors that influenced this one
	@ManyToOne(cascade={ CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name="influencedBy2" )
	private Person top2influencedBy = null;    //Authors that influenced this one
	@ManyToOne(cascade={ CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name="influencedBy3" )
	private Person top3influencedBy = null;    //Authors that influenced this one
	@ManyToOne(cascade={ CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name="influencedBy4" )
	private Person top4influencedBy = null;    //Authors that influenced this one
	@ManyToOne(cascade={ CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name="influencedBy5" )
	private Person top5influencedBy = null;    //Authors that influenced this one

	@ManyToOne(cascade={ CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name="influenced1" )
	private Person top1influenced = null;    //Authors that this one influenced
	@ManyToOne(cascade={ CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name="influenced2" )
	private Person top2influenced = null;    //Authors that this one influenced
	@ManyToOne(cascade={ CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name="influenced3" )
	private Person top3influenced = null;    //Authors that this one influenced
	@ManyToOne(cascade={ CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name="influenced4" )
	private Person top4influenced = null;    //Authors that this one influenced
	@ManyToOne(cascade={ CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name="influenced5" )
	private Person top5influenced = null;    //Authors that this one influenced


	/**
	 * Gets the unique id of this person.
	 * @return The unique id of this person
	 */
	public Long getPersonID() {
		return personID;
	}

	/**
	 * Sets this person's id
	 * @param id The new id
	 */
	public void setPersonID(Long id) {
		this.personID = id;
	}

	/**
	 * Gets this person's prefixes as a single String.
	 * @return This person's prefixes
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Sets this person's prefixes as a single String.
	 * @param prefix This person's new prefixes
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Gets this person's full name.
	 * @return This person's full name.
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Sets this person's full name.
	 * @param fullName This person's full name.
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * Gets this person's full name.
	 * @return This person's full name.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets this person's full name.
	 * @param fullName This person's full name.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	/**
	 * Gets this person's full name.
	 * @return This person's full name.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets this person's full name.
	 * @param fullName This person's full name.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	
	/**
	 * Gets this person's birthday.
	 * @return This person's birthday
	 */
	public String getBirth() {
		return birth;
	}

	/**
	 * Sets this person's birthday
	 * @param birth This person's birthday
	 */
	public void setBirth(String birth) {
		this.birth = birth;
	}

	/**
	 * Gets this person's day of death.
	 * @return This person's day of death
	 */
	public String getObit() {
		return obit;
	}

	/**
	 * Sets this person's day of death.
	 * @param obit This person's day of death
	 */
	public void setObit(String obit) {
		this.obit = obit;
	}

	/**
	 * Gets this person's institution.
	 * @return This person's institution
	 */
	public Institution getInstitution() {
		return institution;
	}

	/**
	 * Sets this person's institution.
	 * @param institution This person's institution
	 */
	public void setInstitution(Institution institution) {
		this.institution = institution;
	}

	/**
	 * Gets the papers this author has written
	 * @return The papers this author has written
	 */
	public Set<Paper> getPapers() {
		return papers;
	}

	/**
	 * Sets this author's papers
	 * @param papers The new papers of this author
	 */
	public void setPapers(Set<Paper> papers) {
		this.papers = papers;
	}

	/**
	 * Adds a paper to this author's paper list
	 * @param p The paper to add
	 */
	public void addPaper(Paper p) {
		papers.add(p);
		if(!p.getAuthors().contains(this)) {
			p.addAuthor(this);
		}
	}

	/**
	 * Gets the Semantic Scholar ID of this author
	 *
	 * @return The Semantic Scholar ID of this author
	 */
	public String getSemanticScholarID() {
		return semanticScholarID;
	}

	/**
	 * Sets the Semantic Scholar ID of this author
	 *
	 * @param semanticScholarID The new Semantic Scholar ID of this author
	 */
	public void setSemanticScholarID(String semanticScholarID) {
		this.semanticScholarID = semanticScholarID;
	}


	/**
	 * Sets the top 5 authors this author was influenced by
	 * Sorted in decreasing order: List.get(0) is the author who has influenced this one the most
	 *
	 * @param top5influencedBy The new top 5 influencers of this author. The size must be something from 0-5 (inclusive)
	 * @return true if at least one influenced-by-author was set, false otherwhise
	 */
	public boolean setTop5influencedBy(List<Person> top5influencedBy) {
		flushInfluencedBy();
		boolean ret = false;
		for( Person a : top5influencedBy ) {
			ret = addInfluencedBy(a);
		}
		return ret;
	}

	/**
	 * Returns the top 5 of authors that were influenced by this author
	 * Sorted in decreasing order: List.get(0) is the author who has influenced this one the most
	 * @return The top 5 of authors that influenced this author as an ArrayList
	 */
	public ArrayList<Person> getTop5influencedBy() {
		ArrayList<Person> ret = new ArrayList<>();
		if(this.top1influencedBy != null) { ret.add(this.top1influencedBy); }
		if(this.top2influencedBy != null) { ret.add(this.top2influencedBy); }
		if(this.top3influencedBy != null) { ret.add(this.top3influencedBy); }
		if(this.top4influencedBy != null) { ret.add(this.top4influencedBy); }
		if(this.top5influencedBy != null) { ret.add(this.top5influencedBy); }
		return ret;
	}

	/**
	 * Adds an author this one was influenced by to this author, if he doesn't have 5 already
	 * The person added first is interpreted as being the person who has influenced this one the most
	 *
	 * @param influencedBy The influencing author to add
	 * @return true if the author was added, false otherwise
	 */
	public boolean addInfluencedBy(Person influencedBy) {
		if( this.top1influencedBy == null ) { this.top1influencedBy = influencedBy; }
		else if( this.top2influencedBy == null ) { this.top2influencedBy = influencedBy; }
		else if( this.top3influencedBy == null ) { this.top3influencedBy = influencedBy; }
		else if( this.top4influencedBy == null ) { this.top4influencedBy = influencedBy; }
		else if( this.top5influencedBy == null ) { this.top5influencedBy = influencedBy; }
		else { return false; }
		return true;
	}

	/**
	 * Sets all fields of authors that influenced this one to null
	 */
	private void flushInfluencedBy() {
		this.top1influencedBy = null;
		this.top2influencedBy = null;
		this.top3influencedBy = null;
		this.top4influencedBy = null;
		this.top5influencedBy = null;
	}

	/**
	 * Sets the top 5 authors that were influenced by this author the most
	 * Sorted in decreasing order: List.get(0) is the author who is influenced by this one the most
	 *
	 * @param top5influenced The new top 5 authors influenced by this one. The size must be something from 0-5 (inclusive)
	 * @return true if at least one influenced author was set, false otherwhise
	 */
	public boolean setTop5influenced(List<Person> top5influenced) {
		flushInfluenced();
		boolean ret = false;
		for( Person a : top5influenced ) {
			ret = addInfluenced(a);
		}
		return ret;
	}


	/**
	 * Sets the top 5 authors that were influenced by this author the most
	 * Sorted in decreasing order: List.get(0) is the author who is influenced by this one the most
	 *
	 * @return The top 5 of authors that were influenced by this author as an ArrayList
	 */
	public ArrayList<Person> getTop5influenced() {
		ArrayList<Person> ret = new ArrayList<>();
		if(this.top1influenced != null) { ret.add(this.top1influenced); }
		if(this.top2influenced != null) { ret.add(this.top2influenced); }
		if(this.top3influenced != null) { ret.add(this.top3influenced); }
		if(this.top4influenced != null) { ret.add(this.top4influenced); }
		if(this.top5influenced != null) { ret.add(this.top5influenced); }
		return ret;
	}

	/**
	 * Adds an author that was influenced by this one, if he doesn't have 5 already
	 * The person added first is interpreted as being the person who is influenced by this one the most
	 *
	 * @param influenced The influenced author to add
	 * @return true if the author was added, false otherwhise
	 */
	public boolean addInfluenced(Person influenced) {
		if( this.top1influenced == null ) { this.top1influenced = influenced; }
		else if( this.top2influenced == null ) { this.top2influenced = influenced; }
		else if( this.top3influenced == null ) { this.top3influenced = influenced; }
		else if( this.top4influenced == null ) { this.top4influenced = influenced; }
		else if( this.top5influenced == null ) { this.top5influenced = influenced; }
		else { return false; }
		return true;
	}

	/**
	 * Sets all fields of authors influenced by this one to null
	 */
	private void flushInfluenced() {
		this.top1influenced = null;
		this.top2influenced = null;
		this.top3influenced = null;
		this.top4influenced = null;
		this.top5influenced = null;
	}

	/**
	 * Creates a String representation of the person object.
	 * Warning: String does not contain all information in the object
	 * @return String description of the object
	 */
	@Override
	public String toString() {
		String ret = "name: " + String.valueOf( this.getFullName() ) + "\n";
		//ret = ret + "institution: " + this.getInstitution().getName() + "\n";
		ret = ret + "Papers: ";
		for (Paper p : this.getPapers()) {
			ret = ret + p.toString() + "\n";
		}
		ret = ret + "S2ID: " + String.valueOf( this.getSemanticScholarID() )+ "\n";
		ret = ret + "birthdate: " + String.valueOf( this.getBirth()) + "\n";
		ret = ret + "PersonID: " + String.valueOf( this.getPersonID()) + "\n";
		ret = ret + "prefix: " + String.valueOf( this.getPrefix() )+ "\n";
		ret = ret + "Influenced: { ";
		for (Person p : this.getTop5influenced()) {
			ret = ret + p.getFullName() + "("+ p.getPersonID() + ") S2ID:"+p.getSemanticScholarID()+")\n";
		}
		ret = ret + "}\nInfluencedBy: { ";
		for (Person p : this.getTop5influencedBy()) {
			ret = ret + p.getFullName() + "("+ p.getPersonID() + ") S2ID:"+p.getSemanticScholarID()+")\n";
		}
		return ret;
	}

	/**
	 * Looks for persons with equal attributes in the DB and returns found entities
	 * If no matching DB entry was found, create and return a new person object
	 * Read more about the search here {@link PersonJPAAccess#getByKnownAttributes(Person)}
	 * @param toFind The person object containing the query data
	 * @return A matching person from the DB or a new person
	 */
	public static Person findOrCreate(Person toFind) {

		//Check if person with same S2ID exists in DB
		PersonJPAAccess filer = new PersonJPAAccess();
		List<Person> searchResults = filer.getByKnownAttributes(toFind);

		if(searchResults == null || searchResults.size() < 1) { //No matching person could be found in the DB
			return new Person();
		}
		else { 		//Choose first result
			return searchResults.get(0);
		}

	}

	/**
	 * Looks for persons with defined title or SemanticScholarID and returns matching DB Entry
	 * If no match was found, create and return new Person Object
	 * @param s2id SemanticScholarID of searched person or null if unknown
	 * @param fullName Name of searched person or null if unknown
	 * @return matching DB entry or new Person
	 */
	public static Person findOrCreate(String s2id, String fullName) {
		PersonJPAAccess filer = new PersonJPAAccess();
		Person searchResult = null;
		if ( s2id == null && fullName == null ) { return new Person(); }
		else if ( s2id != null && fullName != null ) {
			Person query = new Person();
			query.setFullName(fullName);
			query.setSemanticScholarID(s2id);
			return findOrCreate(query);
		}
		else if ( fullName == null ) { searchResult = filer.getBySemanticScholarID(s2id); }
		else if ( s2id == null ) { searchResult = filer.getByFullName(fullName); }

		return searchResult != null ? searchResult : new Person();
	}


	/**
	 * Same as {@link Person#findOrCreate(String, String)}, but also searches in given list
	 * @param s2id Semantic Scholar of of the person to seach
	 * @param fullName Full name of the person to seach
	 * @param list List to be searched
	 * @return A matching person from the List or the DB, or a new person
	 */
	public static Person findOrCreateDbOrList(String s2id, String fullName, List<Person> list) {
		//Filter out any person who does not have either a matching SemanticScholarID or matching name
		List<Person> result = list.stream().filter( currPers -> (
				( currPers.getSemanticScholarID() != null && currPers.getSemanticScholarID().equals(s2id)) ||
				( currPers.getFullName() != null && currPers.getFullName().equals(fullName)))).collect(Collectors.toList());

		//Result now contains only persons with either matching SemanticScholarID or matching name
		return result.size() > 0 ? result.get(0) : findOrCreate(s2id, fullName);
	}

}
