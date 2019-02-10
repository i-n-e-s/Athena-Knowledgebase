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
@Table(name="event")
public class Event extends Model {
	/*Unique id*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="eventID")
	private long eventID;
	/*Name of conference this event belongs to*/
	@Column(name="conferenceName")
	private String conferenceName;
	/*Start time*/
	@Column(name="begin")
	private LocalDateTime begin;
	/*End time*/
	@Column(name="end")
	private LocalDateTime end;
	/*Host*/
	//	@Column(name = "host") //FIXME: crashes - perhaps save id?
	//	private Person host;                //TODO Person von Author abstrahieren
	/* Place where this event happens, if empty look in sessions */
	@Column(name = "place")
	private String place;
	/* Title */
	@Column(name = "title")
	private String title;
	/* Brief Description */
	@Column(name = "description")
	private String description;
	/* Category */
	@Column(name = "category")
	private EventCategory category;
	/* Papers, if any */
	//	@Column(name = "papers")
	//	private Set<Paper> papers;
	/* Sessions, if any */
	@Hierarchy(entityName="session")
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable(
			name = "event_session",
			joinColumns = { @JoinColumn(name = "eventID") },
			inverseJoinColumns = { @JoinColumn(name = "sessionID") }
			)
	private Set<Session> sessions = new HashSet<>();

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
	 * Gets the conference name this event belongs to
	 * @return The conference this event belongs to
	 */
	public String getConferenceName() {
		return conferenceName;
	}

	/**
	 * Sets this event's conference's name
	 * @param conferenceName The new conference
	 */
	public void setConferenceName(String conferenceName) {
		this.conferenceName = conferenceName;
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
	 * @param end the new time this event ends
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
	 * Gets a description of the event
	 *
	 * @return A description of the event
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets a description of the event
	 *
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

	//	/**
	//	 * Gets this event's papers (if any, usually used in poster sessions)
	//	 * @param papers This event's papers
	//	 */
	//	public Set<Paper> getPapers() {
	//		return papers;
	//	}
	//
	//	/**
	//	 * Sets this event's papers (if any, usually used in poster sessions)
	//	 * @return This event's new papers
	//	 */
	//	public void setPapers(Set<Paper> papers) {
	//		this.papers = papers;
	//	}

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
	 * Adds a session to this event's session list
	 * @param s The sessin to add
	 */
	public void addSession(Session s) {
		sessions.add(s);
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
