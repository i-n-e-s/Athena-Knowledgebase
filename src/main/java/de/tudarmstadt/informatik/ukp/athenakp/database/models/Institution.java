package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="institutions")
public class Institution {

	/*Unique institutionID*/
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "institutionID", updatable = false, nullable = false)
	private long institutionID;

	@Column(name = "name", nullable = false)
	private String name;

	@OneToMany(mappedBy = "institution")
	private Set<Person> persons;

	/**
	 * Gets the institution's institutionID.
	 * @return The institution's institutionID.
	 */
	public long getInstitutionID() {
		return institutionID;
	}

	/**
	 * Sets the institution's institutionID.
	 * @param institutionID The institution's institutionID.
	 */
	public void setInstitutionID(long institutionID) {
		this.institutionID = institutionID;
	}

	/**
	 * Gets the name of this Institution
	 *
	 * @return The name of this Institution
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets this institution's name
	 *
	 * @param name The new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the institution's affiliated persons.
	 * @return The institution's affiliated persons
	 */
	public Set<Person> getPersons() {
		return persons;
	}

	/**
	 * Sets the institution's affiliated persons.
	 * @param persons The institution's affiliated persons
	 */
	public void setPersons(Set<Person> persons) {
		this.persons = persons;
	}
}