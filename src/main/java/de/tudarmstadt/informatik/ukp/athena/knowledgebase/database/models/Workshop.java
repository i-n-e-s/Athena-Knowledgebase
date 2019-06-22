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
@Table(name="workshop")
public class Workshop extends Model implements ScheduleEntry {
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

	/*Start time*/
	@Column(name="begin")
	private LocalDateTime begin;
	/*End time*/
	@Column(name="end")
	private LocalDateTime end;

	/* Place where this workshop happens */
	@Column(name = "place")
	private String place;
	/*Basically the schedule. Might be empty since not all workshops provide an easily scrapable schedule*/
	@Hierarchy(entityName="event")
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
	 * @param abbreviation The new abbreviation of the workshop
	 */
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	/**
	 * Gets this workshop's event (if any)
	 * @return This workshop's event
	 */
	public Set<Event> getEvents() {
		return events;
	}

	/**
	 * Sets this workshop's event (if any)
	 * @param event This workshop's new event
	 */
	public void setEvents(Set<Event> event) {
		this.events = event;
	}

	/**
	 * Adds an event to this workshop's event list
	 * @param e The event to add
	 */
	public void addEvent(Event e) {
		events.add(e);
	}
}
