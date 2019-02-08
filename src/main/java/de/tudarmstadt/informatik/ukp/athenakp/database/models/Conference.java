package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="conference")
public class Conference {
	/*Unique id*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="conferenceID")
	private long conferenceID;
	/*Name of conference*/
	@Column(name="name")
	private String name;
	/*First day of conference no need for the temporal annotation with java.time (indeed this would break it)*/

	@Column (name="begin")
	private LocalDate begin;
	/*Last day of conference*/
	@Column(name="end")
	private LocalDate end;

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
	private Set<Person> authors = new HashSet<Person>();

	//TODO: Workshops? Other data? How about Duration? java.time would make that possible

	/**
	 * Gets the unique id of this conference
	 * @return The unique id of this conference
	 */
	public long getId() {
		return conferenceID;
	}

	/**
	 * Sets this conference's id
	 * @param id The new id
	 */
	public void setId(long id) {
		this.conferenceID = id;
	}

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
	public LocalDate getBegin() {
		return begin;
	}

	/**
	 * Sets the date of the day this conference started
	 * @param begin The new start date
	 */
	public void setBegin(LocalDate begin) {
		this.begin = begin;
	}

	/**
	 * Gets the date of the day this conference ended
	 * @return The date of the day this conference ended
	 */
	public LocalDate getEnd() {
		return end;
	}

	/**
	 * Sets the date of the day this conference ended
	 * @param end The new end date
	 */
	public void setEnd(LocalDate end) {
		this.end = end;
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
	public Set<Person> getAuthors() {
		return authors;
	}*/
}
