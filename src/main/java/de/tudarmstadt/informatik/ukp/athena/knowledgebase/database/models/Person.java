package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

/**
 * @author Tristan Wettich
 */
@Entity
@Table(name = "person")
public class Person extends Model {
	/*Unique id*/
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "personID", updatable = false, nullable = false)
	private long personID;

	/*Prefixes like academic titles*/
	@Column(name = "prefix")
	private String prefix;
	/*Full Name*/
	@Column(name = "fullName", nullable = false)
	private String fullName;

	/*Birthday and day of death*/
	@Column(name = "birthday")
	private LocalDate birth;
	@Column(name = "obit")
	private LocalDate obit;

	/*The person's institution, eg. an university or a company*/
	//@Column(name = "institution")
	@ManyToOne
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
	private Person top1influenced = null;    //Authors that influenced this one
	@ManyToOne(cascade={ CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name="influenced2" )
	private Person top2influenced = null;    //Authors that influenced this one
	@ManyToOne(cascade={ CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name="influenced3" )
	private Person top3influenced = null;    //Authors that influenced this one
	@ManyToOne(cascade={ CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name="influenced4" )
	private Person top4influenced = null;    //Authors that influenced this one
	@ManyToOne(cascade={ CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name="influenced5" )
	private Person top5influenced = null;    //Authors that influenced this one


	/**
	 * Gets the unique id of the person.
	 * @return The unique id of the person
	 */
	public long getPersonID() {
		return personID;
	}

	/**
	 * Sets the person's id
	 * @param id The new id
	 */
	public void setPersonID(long id) {
		this.personID = id;
	}

	/**
	 * Gets the person's prefixes as single String.
	 * @return The person's prefixes
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Sets the person's prefixes as single String.
	 * @param prefix The persons new prefixes
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Gets the person's full name.
	 * @return Gets the person's full name.
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Sets the person's full name.
	 * @param fullName The person's full name.
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * Gets the person's birthday.
	 * @return The person's birthday
	 */
	public LocalDate getBirth() {
		return birth;
	}

	/**
	 * Sets the person's birthday
	 * @param birth The person's birthday
	 */
	public void setBirth(LocalDate birth) {
		this.birth = birth;
	}

	/**
	 * Gets the person's day of death.
	 * @return The person's day of death
	 */
	public LocalDate getObit() {
		return obit;
	}

	/**
	 * Sets the person's day of death.
	 * @param obit The person's day of death
	 */
	public void setObit(LocalDate obit) {
		this.obit = obit;
	}

	/**
	 * Gets the person's institution.
	 * @return The person's institution
	 */
	public Institution getInstitution() {
		return institution;
	}

	/**
	 * Sets the person's institution.
	 * @param institution The person's institution
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
	 * @param papers The new paper of this author
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
	}

	/**
	 * Gets the SemanticScholar ID of this author
	 *
	 * @return SemanticScholarID of the author
	 */
	public String getSemanticScholarID() {
		return semanticScholarID;
	}

	/**
	 * Sets the SemanticScholar ID of this author
	 *
	 * @param semanticScholarID New SemanticScholarID of the author
	 */
	public void setSemanticScholarID(String semanticScholarID) {
		this.semanticScholarID = semanticScholarID;
	}


	/**
	 * Sets the top 5 authors this author was influenced by
	 *
	 * @param top5influencedBy The new top 5 influencers of this author. Must be 5 or less
	 * @return true if the new list of authors was set
	 */
	public boolean setTop5influencedBy(List<Person> top5influencedBy) {
		flushInfluencedBy();
		boolean ret = false;
		for( Person a : top5influencedBy ) {
			ret = addInfluencedBy(a);
		}
		return ret;
	}

	public ArrayList<Person> getTop5influencedBy() {
		ArrayList<Person> ret = new ArrayList<Person>();
		ret.add(this.top1influencedBy);
		ret.add(this.top2influencedBy);
		ret.add(this.top3influencedBy);
		ret.add(this.top4influencedBy);
		ret.add(this.top5influencedBy);
		return ret;
	}
	/**
	 * Adds an author, this one was influenced by to the author, if he doesn't have 5 already
	 *
	 * @param influencedBy The influencing author to add
	 * @return true if the influencer was added
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
	 * Deletes the List of Authors influenced by this one
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
	 *
	 * @param top5influenced The new top 5 authors influenced by this one. Must be 5 or less
	 * @return true if the new list was set
	 */
	public boolean setTop5influenced(List<Person> top5influenced) {
		flushInfluenced();
		boolean ret = false;
		for( Person a : top5influenced ) {
			ret = addInfluenced(a);
		}
		return ret;
	}


	public ArrayList<Person> getTop5influenced() {
		ArrayList<Person> ret = new ArrayList<Person>();
		ret.add(this.top1influenced);
		ret.add(this.top2influenced);
		ret.add(this.top3influenced);
		ret.add(this.top4influenced);
		ret.add(this.top5influenced);
		return ret;
	}

	/**
	 * Adds an author that was influenced by this one, if he doesn't have 5 already
	 *
	 * @param influenced The influenced author to add
	 * @return true if the author was added
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
	 * Deletes the List of Authors this Author has influenced
	 */
	private void flushInfluenced() {
		this.top1influenced = null;
		this.top2influenced = null;
		this.top3influenced = null;
		this.top4influenced = null;
		this.top5influenced = null;
	}

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
		return ret;
	}

	/**
	 * Complements the Author Object by the information of the given one
	 * @param srcAuthor
	 */

	public boolean complementBy(Person srcAuthor) {

		boolean changed = false;
		//1. Copy prime attributes without overwriting
		if(this.getBirth() == null && srcAuthor.getBirth() != null) {
			this.setBirth( srcAuthor.getBirth() );
			changed = true;
		}
		if(this.getInstitution() == null && srcAuthor.getInstitution() != null) {
			this.setInstitution( srcAuthor.getInstitution() );
			changed = true;
		}
		if(this.getPrefix() == null && srcAuthor.getPrefix() != null) {
			this.setPrefix( srcAuthor.getPrefix() );
			changed = true;
		}
		if(this.getFullName() == null && srcAuthor.getFullName() != null) {
			this.setFullName( srcAuthor.getFullName() );
			changed = true;
		}
		if(this.getObit() == null && srcAuthor.getObit() != null) {
			this.setInstitution( srcAuthor.getInstitution() );
			changed = true;
		}
		if(this.getSemanticScholarID() == null && srcAuthor.getSemanticScholarID() != null) {
			this.setInstitution( srcAuthor.getInstitution() );
			changed = true;
		}

		//2. Copy papers
		loopAllSrcPapers:
		for ( Paper srcP : srcAuthor.getPapers() ) {

			//2.1 check if paper with same name/S2ID already in known
			for ( Paper thisP : this.getPapers() ) {
				if ( equalsNotNull( thisP.getSemanticScholarID(), srcP.getSemanticScholarID()) ||
						equalsNotNull( thisP.getTitle(), srcP.getTitle())) {
					//If matching paper is found:
					changed = thisP.complementBy(srcP);     //TODO Paper complementBy override
					continue loopAllSrcPapers;
				}
			}
			//2.2 if not, save pointer to paper
			if( !this.getPapers().contains(srcP) ) { this.addPaper(srcP); }
			if( !srcP.getAuthors().contains(this) ) { srcP.addAuthor(this); }
			changed = true;
		}

		//3. Copy influences
		for ( int i = 0; i < 5; i++ ) {
			if ( this.getTop5influenced().get(i) == null && srcAuthor.getTop5influenced().get(i) != null ) {
				this.addInfluenced(srcAuthor.getTop5influenced().get(i));
			}
			if ( this.getTop5influencedBy().get(i) == null && srcAuthor.getTop5influencedBy().get(i) != null ) {
				this.addInfluencedBy(srcAuthor.getTop5influencedBy().get(i));
			}
		}


		return changed;
	}
}
