package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.time.LocalDate;
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
	/* Place where this event happens, if empty look in eventparts */
	@Column(name = "place")
	private String place;

	/* Associated papers */
	@Hierarchy(entityName="paper")
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable(
			name = "event_papers",
			joinColumns = { @JoinColumn(name = "eventID") },
			inverseJoinColumns = { @JoinColumn(name = "paperID") }
			)
	private Set<Paper> papers = new HashSet<>();

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

	
	
	//@Column(name = "speaker")
//	private Person speaker;
	@Column(name = "link")
	private String link;
	/*Date*/
	@Column(name="date")
	private LocalDate date;
	
	/**
	 * Gets the date of this event
	 * @return event's date
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * Sets the date of this event
	 * @param event's date
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	/**
	 * Gets the speaker of a tutorial (the first listed person in the corresponding paper)
	 * @return speaker of tutorial
	 */
	//public Person getSpeaker() {
		//return speaker;
	//}

	/**
	 * Sets the speaker of a tutorial (the first listed person in the corresponding paper)
	 * @param speaker of tutorial
	 */
	//public void setSpeaker(Person speaker) {
	//	this.speaker = speaker;
	//}
	
	/**
	 * Gets the link of workshops to the corresponding workshop page
	 * @return link to workshop page
	 */
	public String getLink() {
		return link;
	}

	/**
	 * Sets the link of workshops to the corresponding workshop page
	 * @param link to workshop page
	 */
	public void setLink(String link) {
		this.link = link;
	}
	
	
	
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
	 * Gets this event's papers (if any)
	 * * @return This event's papers
	 */
	public Set<Paper> getPapers() {
		return papers;
	}

	/**
	 * Sets this event's papers (if any)
	 * @param papers This event's new papers
	 */
	public void setPapers(Set<Paper> papers) {
		this.papers = papers;
	}

	/**
	 * Adds a paper to this event's paper list
	 * @param p The paper to add
	 */
	public void addPaper(Paper p) {
		papers.add(p);
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
