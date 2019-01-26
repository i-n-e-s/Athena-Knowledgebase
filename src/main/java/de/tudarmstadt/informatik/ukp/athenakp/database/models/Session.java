package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="session")
public class Session{ //TODO: chair
	/*Unique id*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="sessionID")
	private long sessionID;
	/* Title */
	@Column(name = "title")
	private String title;
	/* Brief Description */
	@Column(name = "description", columnDefinition = "VARCHAR(1000)") //fixes titles that are too long for being storable in the column
	private String description;
	/* Place where this session happens */
	@Column(name = "place")
	private String place;
	/* Subessions, if any */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable(
			name = "session_subsession",
			joinColumns = { @JoinColumn(name = "sessionID") },
			inverseJoinColumns = { @JoinColumn(name = "subsessionID") }
			)
	private Set<Subsession> subsessions;

	/**
	 * Gets the unique id of this session
	 * @return The unique id of this session
	 */
	public long getId() {
		return sessionID;
	}

	/**
	 * Sets this session's id
	 * @param id The new id
	 */
	public void setId(long id) {
		this.sessionID = id;
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
	 * Gets a description of the session
	 *
	 * @return A description of the session
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets a description of the session
	 *
	 * @param description A new description of the session
	 */
	public void setDescription(String description) {
		this.description = description;
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
