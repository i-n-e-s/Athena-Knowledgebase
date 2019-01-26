package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Tristan Wettich
 */
@Entity(name = "person")
@Table(name = "person")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "person")
public class Person {
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
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "birthday")
	private LocalDate birthdate;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "obit")
	private LocalDate obit;

	/*The person's institution, eg. an university or a company*/
	//@Column(name = "institution")
	@ManyToOne
	@JoinColumn(name = "institutionID")
	private Institution institution;

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
	public LocalDate getBirthdate() {
		return birthdate;
	}

	/**
	 * Sets the person's birthday
	 * @param birthdate The person's birthday
	 */
	public void setBirthdate(LocalDate birthdate) {
		this.birthdate = birthdate;
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

}
