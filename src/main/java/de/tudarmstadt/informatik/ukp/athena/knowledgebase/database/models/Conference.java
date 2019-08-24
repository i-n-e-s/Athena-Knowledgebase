package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.ConferenceJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.EventJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PersistenceManager;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="conference")
public class Conference extends Model{
	/*Unique id*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="conferenceID")
	private Long conferenceID;
	/*Name of conference*/
	@Column(name="name")
	private String name;

	/*First day of conference, no need for the temporal annotation with java.time (indeed this would break it)*/
	@Column (name="begin")
	private String begin;
	/*Last day of conference*/
	@Column(name="end")
	private String end;

	@Column(name="country")
	private String country;
	@Column (name="city")
	private String city;
	@Column(name="address")
	private String address;

	/*Basically the schedule*/
	@Hierarchy(entityName="event")

	@JsonIgnore
	@OneToMany(orphanRemoval=true, fetch=FetchType.LAZY) //unidirectional relationship which
	@JoinColumn(name="conferenceID")					  //is saved in the Event table
	private Set<Event> events = new HashSet<>();
	/*The workshops*/
	@Hierarchy(entityName="workshop")
	@JsonIgnore
	@OneToMany(orphanRemoval=true, fetch=FetchType.LAZY) //unidirectional relationship which
	@JoinColumn(name="conferenceID")					  //is saved in the Workshop table
	private Set<Workshop> workshops = new HashSet<>();


	public static Conference findOrCreate(String name){
		System.out.println("Konferenz: " + name);
		ConferenceJPAAccess conferenceFiler = new ConferenceJPAAccess();
		if(name != null){
			System.out.println("schon drin");
			Conference c = conferenceFiler.getByName(name);
			if(c != null) return c;
		}
		System.out.println("neu");
		Conference c = new Conference();
		c.setName(name); //Achtung kann hier null werden
		conferenceFiler.add(c);
		return c;
	}
	/**
	 * Gets the unique id of this conference
	 * @return The unique id of this conference
	 */
	public Long getId() {
		return conferenceID;
	}

	/**
	 * Sets this conference's id
	 * @param id The new id
	 */
	public void setId(Long id) {
		this.conferenceID = id;
	}

	/**
	 * Gets the name of this conference
	 * @return The name of this conference
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this conference
	 * @param name The new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the date of the day this conference started
	 * @return The date of the day this conference started
	 */
	public String getBegin() {
		return begin;
	}

	/**
	 * Sets the date of the day this conference started
	 * @param begin The new start date
	 */
	public void setBegin(String begin) {
		this.begin = begin;
	}

	/**
	 * Gets the date of the day this conference ended
	 * @return The date of the day this conference ended
	 */
	public String getEnd() {
		return end;
	}

	/**
	 * Sets the date of the day this conference ended
	 * @param end The new end date
	 */
	public void setEnd(String end) {
		this.end = end;
	}

	/**
	 * Gets the city the conference was hosted at
	 * @return city The city the conference was hosted at
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the conference's city
	 * @param city the conference's city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Gets the country the conference was hosted in
	 * @return	the country the conference was hosted in
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Sets the country a conference was hosted in
	 * @param country the country a conference was hosted in
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * Gets the building address of the conference
	 * @return the building address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the conference's building address
	 * @param address the conference's building address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Gets the events building up this conference's schedule
	 * @return The events building up this conference's schedule
	 */
	public Set<Event> getEvents(){
		return events;
	}

	/**
	 * Sets the events building up this conference's schedule
	 * @param events The new events building up this conference's schedule
	 */
	public void setEvents(Set<Event> events){
		this.events = events;
	}

	/**
	 * Adds an event to this conference's list of events
	 * @param event The event to add
	 */
	public void addEvent(Event event) {
		EventJPAAccess eventFiler = new EventJPAAccess();
		if(eventFiler.alreadyExists(event.getTitle())){
			return; //Event already in Database
		}
		events.add(event);
	}

	/**
	 * Gets the workshops in this conference's schedule
	 * @return The workshops in this conference's schedule
	 */
	public Set<Workshop> getWorkshops(){
		return workshops;
	}


	/**
	 * Sets the workshop in this conference's schedule
	 * @param workshops The new workshops in this conference's schedule
	 */
	public void setWorkshops(Set<Workshop> workshops){
		this.workshops = workshops;
	}

	/**
	 * Adds a workshop to this conference's list of workshops
	 * @param workshop The workshop to add
	 */
	public void addWorkshop(Workshop workshop) {
		workshops.add(workshop);
	}

}

