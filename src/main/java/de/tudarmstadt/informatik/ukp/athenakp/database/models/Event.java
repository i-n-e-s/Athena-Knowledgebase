package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
	/*Start time*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="begin_date")
	private Date begin;
	/*End time*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_date")
	private Date end;
	/*Host*/
	//private Person host;                //TODO Person von Author abstrahieren
	/* Place where this event happens */
	@Column(name = "place")
	private String place;
	/* Title */
	@Column(name = "title")
	private String title;
	/* Brief Description */
	@Column(name = "short_description")
	private String shortDescription;
	/* Attendees */
	//private Set<Person> attendees;      //TODO Hibernatemäßige Setter für alle Set<> oder List<> fehlen
	/* Category */
	private EventCategory category;   //TODO Hibernatemäßige Getter/Setter für diese ENUM implementieren


	/**
	 * Gets the unique id of this event
	 * @return The unique id of this event
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * Sets this event's id
	 * @param id The new id
	 */
	public void setId(long id)
	{
		this.id = id;
	}


	/**
	 * Gets the time this event begins
	 * @return This event's begin time/date
	 */
	public Date getBegin()
	{
		return begin;
	}

	/**
	 * Sets the time this event begins
	 * @param begin The time this event begins
	 */
	public void setBegin(Date begin)
	{
		this.begin = begin;
	}

	/**
	 * Gets the time this event ends
	 * @return This event's new end time/date
	 */
	public Date getEnd()
	{
		return end;
	}

	/**
	 * Sets the time this event ends
	 * @param end the new time this event ends
	 */
	public void setEnd(Date end)
	{
		this.end = end;
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
