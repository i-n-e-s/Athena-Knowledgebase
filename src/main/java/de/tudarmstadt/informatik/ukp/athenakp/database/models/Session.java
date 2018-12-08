package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="session")
public class Session
{
	/*Unique id*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="id")
	private long id;
	/* Title */
	@Column(name = "title")
	private String title;
	/* Brief Description */
	@Column(name = "short_description")
	private String shortDescription;
	/* Place where this session happens */
	@Column(name = "place")
	private String place;
	/* Subessions, if any */
	@Column(name = "subsessions")
	private Set<Subsession> subsessions;

	/**
	 * Gets the unique id of this session
	 * @return The unique id of this session
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets this session's id
	 * @param id The new id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the place where this session happens
	 *
	 * @return The place where this session happens
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * Sets the place where this session happens
	 *
	 * @param place The new place where this session happens
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * Gets the title of this session
	 *
	 * @return The title of this session
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of this session
	 *
	 * @param title The new title of this session
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets a short description of the session
	 *
	 * @return A short description of the session
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * Sets a short description of the session
	 *
	 * @param shortDescription A new short description of the session
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	/**
	 * Gets this event's sessions (if any)
	 * @return This event's sessions
	 */
	public Set<Subsession> getSubsessions() {
		return subsessions;
	}

	/**
	 * Sets this event's sessions (if any)
	 * @param sessions This event's new sessions
	 */
	public void setSubsessions(Set<Subsession> subsessions) {
		this.subsessions = subsessions;
	}
}
