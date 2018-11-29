package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="conference")
public class Conference {
	/*Name of conference*/
	@Id
	@Column(name="name")
	private String name;
	/*First day of conference can include time - timezoned*/
	private ZonedDateTime startDate;
	/*Last day of conference can include time - timezoned*/
	@Column(name="end_date")
	private ZonedDateTime endDate;
	/*Authors that talked*/
	/*@ManyToMany  TODO: fix, currently comes up with an error
	@Column(name="authors")
	private Set<Author> authors = new HashSet<Author>();*/
	/*Shown papers*/
/*	@ManyToMany TODO: fix, currently comes up with an error (probably same as above) uncomment once the implementation
	is of interest
	@Column(name="papers")
	private Set<Paper> papers = new HashSet<Paper>();*/
	//TODO: Workshops? Other data?

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
	public ZonedDateTime getStartDate() {
		return startDate;
	}

	/**
	 * Sets the date of the day this conference started
	 * @param startDate The new start date
	 */
	public void setStartDate(ZonedDateTime startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the date of the day this conference ended
	 * @return The date of the day this conference ended
	 */
	public ZonedDateTime getEndDate() {
		return endDate;
	}

	/**
	 * Sets the date of the day this conference ended
	 * @param endDate The new end date
	 */
	public void setEndDate(ZonedDateTime endDate) {
		this.endDate = endDate;
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
