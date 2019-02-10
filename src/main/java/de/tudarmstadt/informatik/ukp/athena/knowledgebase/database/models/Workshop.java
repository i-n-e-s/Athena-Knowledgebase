package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.time.LocalDateTime;
import java.util.HashSet;
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
@Table(name="workshops")
public class Workshop implements ScheduleEntry {
	/*Unique id*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="workshopID")
	private long workshopID;
	/* Title */
	@Column(name = "title")
	private String title;
	/* Abbreviation */
	@Column(name = "abbreviation")
	private String abbreviation;
	/*Name of conference this workshop belongs to*/
	@Column(name="conferenceName")
	private String conferenceName;

	/*Start time*/
	@Column(name="begin")
	private LocalDateTime begin;
	/*End time*/
	@Column(name="end")
	private LocalDateTime end;

	/* Place where this workshop happens */
	@Column(name = "place")
	private String place;
	/* Events, not all workshops provide easily scrapable schedule*/
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable(
			name = "workshop_session",
			joinColumns = { @JoinColumn(name = "workshopID") },
			inverseJoinColumns = { @JoinColumn(name = "sessionID") }
			)
	private Set<Session> sessions = new HashSet<>();

	/**
	 * Gets the unique id of this workshop
	 * @return The unique id of this workshop
	 */
	public long getId() {
		return workshopID;
	}

	/**
	 * Sets this workshop's id
	 * @param id The new id
	 */
	public void setId(long id) {
		this.workshopID = id;
	}

	/**
	 * Gets the conference name this workshop belongs to
	 * @return The conference this workshop belongs to
	 */
	public String getConferenceName() {
		return conferenceName;
	}

	/**
	 * Sets this workshop's conference's name
	 * @param conferenceName The new conference
	 */
	public void setConferenceName(String conferenceName) {
		this.conferenceName = conferenceName;
	}

	/**
	 * Gets the time this workshop begins
	 * @return This workshop's begin time
	 */
	public LocalDateTime getBegin() {
		return begin;
	}

	/**
	 * Sets the time this workshop begins
	 * @param begin The time this workshop begins
	 */
	public void setBegin(LocalDateTime begin) {
		this.begin = begin;
	}

	/**
	 * Gets the time this workshop ends
	 * @return This workshop's new end time
	 */
	public LocalDateTime getEnd() {
		return end;
	}

	/**
	 * Sets the time this workshop ends
	 * @param end the new time this workshop ends
	 */
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	/**
	 * Gets the place where this workshop happens
	 * @return The place where this workshop happens
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * Sets the place where this workshop happens
	 * @param place The new place where this workshop happens
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * Gets the title of this workshop
	 * @return The title of this workshop
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of this workshop
	 * @param title The new title of this workshop
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the abbreviation of the workshop
	 * @return The abbreviation of the workshop
	 */
	public String getAbbreviation() {
		return abbreviation;
	}

	/**
	 * Sets the abbreviation of the workshop
	 * @param description The new abbreviation of the workshop
	 */
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	/**
	 * Gets this workshop's sessions (if any)
	 * @return This workshop's sessions
	 */
	public Set<Session> getSessions() {
		return sessions;
	}

	/**
	 * Sets this workshop's sessions (if any)
	 * @param sessions This workshop's new sessions
	 */
	public void setSessions(Set<Session> sessions) {
		this.sessions = sessions;
	}

	/**
	 * Adds a session to this workshop's session list
	 * @param s The session to add
	 */
	public void addSession(Session s) {
		sessions.add(s);
	}
}
