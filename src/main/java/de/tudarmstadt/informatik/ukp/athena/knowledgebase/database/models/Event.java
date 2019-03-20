package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
@Table(name="event")
public class Event extends Model implements ScheduleEntry {
	/*Unique id*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="eventID")
	private long eventID;
	/* Title */
	@Column(name = "title")
	private String title;
	/* Brief Description */
	@Column(name = "description")
	private String description;
	/* Category */
	@Column(name = "category")
	private EventCategory category;

	/*Start time*/
	@Column(name="begin")
	private LocalDateTime begin;
	/*End time*/
	@Column(name="end")
	private LocalDateTime end;

	/*Host*/
	//	@Column(name = "host")
	//	private Person host;
	/* Place where this session happens, if empty look in sessions */
	@Column(name = "place")
	private String place;

	/* Associated papers */
	@Column(name = "paperTitles")
	@ElementCollection(fetch = FetchType.EAGER) //similar to @JoinTable, but for model -> datatype relations instead of model -> model
	private Set<String> paperTitles = new HashSet<>();

	/* Papers, if any */
	//	@Column(name = "papers")
	//	private Set<Paper> papers;
	/* Event parts, if any */
	@Hierarchy(entityName="eventpart")
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable(
			name = "event_eventParts",
			joinColumns = { @JoinColumn(name = "eventID") },
			inverseJoinColumns = { @JoinColumn(name = "eventPartID") }
			)
	private Set<EventPart> eventparts = new HashSet<>(); //lowercase to make it work with the api

	/**
	 * Gets the unique id of this event
	 * @return The unique id of this event
	 */
	public long getId() {
		return eventID;
	}

	/**
	 * Sets this event's id
	 * @param id The new id
	 */
	public void setId(long id) {
		this.eventID = id;
	}

	/**
	 * Gets the time this event begins
	 * @return This event's begin time
	 */
	public LocalDateTime getBegin() {
		return begin;
	}

	/**
	 * Sets the time this event begins
	 * @param begin The time this event begins
	 */
	public void setBegin(LocalDateTime begin) {
		this.begin = begin;
	}

	/**
	 * Gets the time this event ends
	 * @return This event's new end time
	 */
	public LocalDateTime getEnd() {
		return end;
	}

	/**
	 * Sets the time this event ends
	 * @param end The new time this event ends
	 */
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	//	/**
	//	 * Gets the person who manages this event
	//	 * @return This event's manager
	//	 */
	//	public Person getHost() {
	//		return host;
	//	}
	//
	//	/**
	//	 * Sets the person who manages this event
	//	 * @param This event's new manager
	//	 */
	//	public void setHost(Person host) {
	//		this.host = host;
	//	}

	/**
	 * Gets the place where this event happens
	 * @return The place where this event happens
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * Sets the place where this event happens
	 * @param place The new place where this event happens
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * Gets the title of this event
	 * @return The title of this event
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of this event
	 * @param title The new title of this event
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets a description of the event
	 * @return A description of the event
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets a description of the event
	 * @param description A new description of the event
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets this event's category
	 * @return This event's category
	 */
	public EventCategory getCategory() {
		return category;
	}

	/**
	 * Sets this event's category
	 * @return This event's new category
	 */
	public void setCategory(EventCategory category) {
		this.category = category;
	}

	/**
	 * Gets this event's paper's titles (if any)
	 * @return This event's paper's titles
	 */
	public Set<String> getPaperTitles() {
		return paperTitles;
	}

	/**
	 * Sets this event's paper's titles (if any)
	 * @param paperTitles This event's new paper's titles
	 */
	public void setPaperTitles(Set<String> paperTitles) {
		this.paperTitles = paperTitles;
	}

	/**
	 * Adds a paper title to this event's paper's titles list
	 * @param s The paper title to add
	 */
	public void addPaperTitle(String s) {
		paperTitles.add(s);
	}

	/**
	 * Gets this event's event parts (if any)
	 * @return This event's event parts
	 */
	public Set<EventPart> getEventParts() {
		return eventparts;
	}

	/**
	 * Sets this event's event parts (if any)
	 * @param eventParts This event's new event parts
	 */
	public void setEventParts(Set<EventPart> eventParts) {
		this.eventparts = eventParts;
	}

	/**
	 * Adds a event part to this event's event part list
	 * @param e The event part to add
	 */
	public void addEventPart(EventPart e) {
		eventparts.add(e);
	}
}
