package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.time.LocalTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

//@Entity
//@Table(name="subsubsession")
public class Subsession
{
	/*Unique id*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="id")
	private long id;
	/*Start time*/
	@Column(name="begin")
	private LocalTime begin;
	/*End time*/
	@Column(name="end")
	private LocalTime end;
	/* Title */
	@Column(name = "title")
	private String title;
	/* Brief Description */
	@Column(name = "short_description")
	private String shortDescription;
	/* Papers, if any */
	@Column(name = "papers")
	private Set<Paper> papers;

	/**
	 * Gets the unique id of this subsession
	 * @return The unique id of this subsession
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets this subsession's id
	 * @param id The new id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the time this subsession begins
	 * @return This subsession's begin time
	 */
	public LocalTime getBegin() {
		return begin;
	}

	/**
	 * Sets the time this subsession begins
	 * @param begin The time this subsession begins
	 */
	public void setBegin(LocalTime begin) {
		this.begin = begin;
	}

	/**
	 * Gets the time this subsession ends
	 * @return This subsession's new end time
	 */
	public LocalTime getEnd() {
		return end;
	}

	/**
	 * Sets the time this subsession ends
	 * @param end the new time this subsession ends
	 */
	public void setEnd(LocalTime end) {
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
	 * Gets a short description of the subsession
	 *
	 * @return A short description of the subsession
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * Sets a short description of the subsession
	 *
	 * @param shortDescription A new short description of the subsession
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
}
