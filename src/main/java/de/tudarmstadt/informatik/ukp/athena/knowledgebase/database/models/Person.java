package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.time.LocalDate;
import java.util.HashSet;
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
	@Column(name = "birth")
	private LocalDate birth;
	@Column(name = "obit")
	private LocalDate obit;

	/*The person's institution, eg. an university or a company*/
	//@Column(name = "institution")
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

}
