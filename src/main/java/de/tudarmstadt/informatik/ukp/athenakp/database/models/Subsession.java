package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="subsession")
public class Subsession {
	/*Unique id*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="subsessionID")
	private long subsessionID;
	/*Start time*/
	@Column(name="begin")
	private LocalDateTime begin;
	/*End time*/
	@Column(name="end")
	private LocalDateTime end;
	/* Title */
	@Column(name = "title")
	private String title;
	/* Brief Description */
	@Column(name = "description", columnDefinition = "VARCHAR(3000)") //fixes titles that are too long for being storable in the column
	private String description;
	//	/* Papers, if any */
	//	@Column(name = "papers") //so far, a subsession only ever seems to have one paper - so its title is stored as the subsession's title, same for the description
	//	private Set<Paper> papers;

	/**
	 * Gets the unique id of this subsession
	 * @return The unique id of this subsession
	 */
	public long getId() {
		return subsessionID;
	}

	/**
	 * Sets this subsession's id
	 * @param id The new id
	 */
	public void setId(long id) {
		this.subsessionID = id;
	}

	/**
	 * Gets the time this subsession begins
	 * @return This subsession's begin time
	 */
	public LocalDateTime getBegin() {
		return begin;
	}

	/**
	 * Sets the time this subsession begins
	 * @param begin The time this subsession begins
	 */
	public void setBegin(LocalDateTime begin) {
		this.begin = begin;
	}

	/**
	 * Gets the time this subsession ends
	 * @return This subsession's new end time
	 */
	public LocalDateTime getEnd() {
		return end;
	}

	/**
	 * Sets the time this subsession ends
	 * @param end the new time this subsession ends
	 */
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	/**
	 * Gets the title of this subsession
	 *
	 * @return The title of this subsession
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of this subsession
	 *
	 * @param title The new title of this subsession
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets a description of the subsession
	 *
	 * @return A description of the subsession
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets a description of the subsession
	 *
	 * @param description A new description of the subsession
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
