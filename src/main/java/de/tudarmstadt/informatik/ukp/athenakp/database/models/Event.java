package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="events")
public class Event {
	/*Unique id*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="id")
	private long id;
	/*Name of conference this event belongs to*/
	@Column(name="conference")
	private String conference;
	/*The date of this event*/
	@Column(name="date")
	private LocalDate date;
	/*Start time*/
	@Column(name="begin")
	private LocalTime begin;
	/*End time*/
	@Column(name="end")
	private LocalTime end;
	/*Host*/
	//	@Column(name = "host") //FIXME: crashes - perhaps save id?
	private Person host;                //TODO Person von Author abstrahieren
	/* Place where this event happens, if empty look in sessions */
	@Column(name = "place")
	private String place;
	/* Title */
	@Column(name = "title")
	private String title;
	/* Brief Description */
	@Column(name = "short_description")
	private String shortDescription;
	/* Category */
	@Column(name = "category")
	private EventCategory category;
	/* Papers, if any */
	@Column(name = "papers")
	private Set<Paper> papers;
	/* Sessions, if any */
	@Column(name = "sessions")
	private Set<Session> sessions;

	/**
	 * Gets the unique id of this event
	 * @return The unique id of this event
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets this event's id
	 * @param id The new id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the conference name this event belongs to
	 * @return The conference this event belongs to
	 */
	public String getConference() {
		return conference;
	}

	/**
	 * Sets this event's conference's name
	 * @param conference The new conference
	 */
	public void setConference(String conference) {
		this.conference = conference;
	}

	/**
	 * Gets the date this event happens on
	 * @return This event's date
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * Sets the date this event happens on
	 * @param date The date this event happens on
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}

	/**
	 * Gets the time this event begins
	 * @return This event's begin time
	 */
	public LocalTime getBegin() {
		return begin;
	}

	/**
	 * Sets the time this event begins
	 * @param begin The time this event begins
	 */
	public void setBegin(LocalTime begin) {
		this.begin = begin;
	}

	/**
	 * Gets the time this event ends
	 * @return This event's new end time
	 */
	public LocalTime getEnd() {
		return end;
	}

	/**
	 * Sets the time this event ends
	 * @param end the new time this event ends
	 */
	public void setEnd(LocalTime end) {
		this.end = end;
	}

	/**
	 * Gets the person who manages this event
	 * @return This event's manager
	 */
	public Person getHost() {
		return host;
	}

	/**
	 * Sets the person who manages this event
	 * @param This event's new manager
	 */
	public void setHost(Person host) {
		this.host = host;
	}

	/**
	 * Gets the place where this event happens
	 *
	 * @return The place where this event happens
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * Sets the place where this event happens
	 *
	 * @param place The new place where this event happens
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * Gets the title of this event
	 *
	 * @return The title of this event
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of this event
	 *
	 * @param title The new title of this event
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets a short description of the event
	 *
	 * @return A short description of the event
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * Sets a short description of the event
	 *
	 * @param shortDescription A new short description of the event
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
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
	 * Gets this event's papers (if any, usually used in poster sessions)
	 * @param papers This event's papers
	 */
	public Set<Paper> getPapers() {
		return papers;
	}

	/**
	 * Sets this event's papers (if any, usually used in poster sessions)
	 * @return This event's new papers
	 */
	public void setPapers(Set<Paper> papers) {
		this.papers = papers;
	}

	/**
	 * Gets this event's sessions (if any)
	 * @return This event's sessions
	 */
	public Set<Session> getSessions() {
		return sessions;
	}

	/**
	 * Sets this event's sessions (if any)
	 * @param sessions This event's new sessions
	 */
	public void setSessions(Set<Session> sessions) {
		this.sessions = sessions;
	}

	/**
	 * Gets the attendend persons of this event
	 * @return The attendend persons of this event

    @ManyToMany
    @Column(name="attendees")
    public Set<Author> getAttendees()
    {
        return attendees;
    }
	 */
}
