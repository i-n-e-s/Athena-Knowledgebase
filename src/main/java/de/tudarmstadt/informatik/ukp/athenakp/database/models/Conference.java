package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="conference")
public class Conference {
	/*Name of conference*/
	@Id
	@Column(name="name")//TODO Wouldn't an auto-generated id  like in paper be better? What if someone misspelled the name?
	private String name;
	/*First day of conference no need for the temporal annotation with java.time (indeed this would break it)*/
	@Column (name="startDate")
	private LocalDate startDate;
	/*Last day of conference*/
	@Column(name="endDate")
	private LocalDate endDate;
	@Column(name="country")
	private String country;
	@Column (name = "city")
	private String city;
	@Column(name = "address")
	private String address;
	/*Authors that talked*/
	@ManyToMany
	@JsonIgnore
	@Column(name="authors")
	private Set<Author> authors = new HashSet<Author>();
	/*Shown papers*/
	@ManyToMany
	@JsonIgnore
	@Column(name="papers")
	private Set<Paper> papers = new HashSet<Paper>();
	//TODO: Workshops? Other data? How about Duration? java.time would make that possible

	/**
	 * Gets the name of this conference
	 * @return The name of this conference
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this conference
	 * @param name The new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the date of the day this conference started
	 * @return The date of the day this conference started
	 */
	public LocalDate getStartDate() {
		return startDate;
	}

	/**
	 * Sets the date of the day this conference started
	 * @param startDate The new start date
	 */
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the date of the day this conference ended
	 * @return The date of the day this conference ended
	 */
	public LocalDate getEndDate() {
		return endDate;
	}

	/**
	 * Sets the date of the day this conference ended
	 * @param endDate The new end date
	 */
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets the city the conference was hosted at
	 * @return city The city the conference was hosted at
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the conference's city
	 * @param city the conference's city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Gets the country the conference was hosted in
	 * @return	the country the conference was hosted in
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Sets the country a conference was hosted in
	 * @param country the country a conference was hosted in
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * gets the building address of the conference
	 * @return the building address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * sets the conference's building address
	 * @param address the conference's building address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/*
	 * Gets the authors that talked at this conference
	 * @return The authors that talked at this conference
	TODO: fix, see above at authors attribute
	public Set<Author> getAuthors() {
		return authors;
	}*/

	/*
	 * Gets the papers that were shown at this conference
	 * @return The papers that were shown at this conference
	 * TODO: implement then uncomment (see above)
	 */
	/* public Set<Paper> getPapers() {
		return papers;
	}*/
}
