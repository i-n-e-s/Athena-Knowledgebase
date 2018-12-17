package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.time.LocalDate;
import java.time.LocalTime;
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
public class Workshop implements ScheduleEntry{
	/*Unique id*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="workshopID")
	private long workshopID;
	/*Name of conference this workshop belongs to*/
	@Column(name="conference")
	private String conference;
	/*The date of this workshop*/
	@Column(name="date")
	private LocalDate date;
	/*Start time*/
	@Column(name="begin")
	private LocalTime begin;
	/*End time*/
	@Column(name="end")
	private LocalTime end;
	/* Place where this workshop happens */
	@Column(name = "place")
	private String place;
	/* Title */
	@Column(name = "title")
	private String title;
	/* Abbreviation */
	@Column(name = "abbreviation")
	private String abbreviation;
	/* Events, not all workshops provide easily scrapable schedule*/
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable(
			name = "workshop_event",
			joinColumns = { @JoinColumn(name = "workshopID") },
			inverseJoinColumns = { @JoinColumn(name = "eventID") }
			)
	private Set<Event> events = new HashSet<>();

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
	public String getConference() {
		return conference;
	}

	/**
	 * Sets this workshop's conference's name
	 * @param conference The new conference
	 */
	public void setConference(String conference) {
		this.conference = conference;
	}

	/**
	 * Gets the date this workshop happens on
	 * @return This workshop's date
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * Sets the date this workshop happens on
	 * @param date The date this workshop happens on
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}

	/**
	 * Gets the time this workshop begins
	 * @return This workshop's begin time
	 */
	public LocalTime getBegin() {
		return begin;
	}

	/**
	 * Sets the time this workshop begins
	 * @param begin The time this workshop begins
	 */
	public void setBegin(LocalTime begin) {
		this.begin = begin;
	}

	/**
	 * Gets the time this workshop ends
	 * @return This workshop's new end time
	 */
	public LocalTime getEnd() {
		return end;
	}

	/**
	 * Sets the time this workshop ends
	 * @param end the new time this workshop ends
	 */
	public void setEnd(LocalTime end) {
		this.end = end;
	}

	/**
	 * Gets the place where this workshop happens
	 *
	 * @return The place where this workshop happens
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * Sets the place where this workshop happens
	 *
	 * @param place The new place where this workshop happens
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * Gets the title of this workshop
	 *
	 * @return The title of this workshop
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of this workshop
	 *
	 * @param title The new title of this workshop
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets a abbreviation of the workshop
	 *
	 * @return A abbreviation of the workshop
	 */
	public String getAbbreviation() {
		return abbreviation;
	}

	/**
	 * Sets a abbreviation of the workshop
	 *
	 * @param description A new abbreviation of the workshop
	 */
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	/**
	 * Gets this workshop's sessions (if any)
	 * @return This workshop's sessions
	 */
	public Set<Event> getEvents() {
		return events;
	}

	/**
	 * Sets this workshop's events (if any)
	 * @param sessions This workshop's new events
	 */
	public void setEvents(Set<Event> events) {
		this.events = events;
	}

	/**
	 * Adds a session to this workshop's session list
	 * @param s The session to add
	 */
	public void addEvent(Event e) {
		events.add(e);
	}
}
