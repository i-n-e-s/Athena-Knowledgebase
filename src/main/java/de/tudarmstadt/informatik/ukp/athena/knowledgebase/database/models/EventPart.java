package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.EventJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.EventPartJPAAccess;

@Entity
@Table(name="eventpart")
public class EventPart extends Model{
	/*Unique id*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="eventPartID")
	private Long eventPartID;
	/* Title */
	@Column(name = "title")
	private String title;
	/* Brief Description */

	@Column(name = "description", columnDefinition = "VARCHAR(3000)") //fixes titles that are too long for being storable in the column
	private String description;

	/*Start time*/
	@Column(name="begin")
	private LocalDateTime begin;
	/*End time*/
	@Column(name="end")
	private LocalDateTime end;

	/* Place where this event happens */
	@Column(name = "place")
	private String place;
	
	
	/* Associated papers */

	@Hierarchy(entityName="paper")
	@OneToMany(mappedBy = "eventpart")
	private Set<Paper> papers = new HashSet<>();

	@Hierarchy(entityName="person")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eventpart_person")
	private Person person;
	
//	@Column(name = "speaker")
//	private Person speaker;
//	
	//@Column(name = "paper")
	//private Paper paper;
	
	
	/**
	 * Gets the unique id of this event part
	 * @return The unique id of this event part
	 */
	public Long getId() {
		return eventPartID;
	}

	/**
	 * Sets this event part's id
	 * @param id The new id
	 */
	public void setId(Long id) {
		this.eventPartID = id;
	}

	/**
	 * Gets the place where this event part happens
	 * @return The place where this event part happens
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * Sets the place where this event part happens
	 * @param place The new place where this event part happens
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * Gets the time this event part begins
	 * @return This event part's begin time
	 */
	public LocalDateTime getBegin() {
		return begin;
	}

	/**
	 * Sets the time this event part begins
	 * @param sessStart The time this event part begins
	 */
	public void setBegin(LocalDateTime begin) {
		this.begin = begin;
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
	 * Gets the speaker of a EventPart (the first listed person in the corresponding paper)
	 * @return speaker of tutorial
//	 */
//	public Person getSpeaker() {
//		return speaker;
//	}
//
//	/**
//	 * Sets the speaker of a EventPart (the first listed person in the corresponding paper)
//	 * @param speaker of tutorial
//	 */
//	public void setSpeaker(Person speaker) {
//		this.speaker = speaker;
//	}
//	
//	
	/**
	 * Gets the corresponding paper
	 * @return speaker of tutorial
	 */
//	public Paper getPaper() {
//		return paper;
//	}
//
//	/**
//	 * Sets the the corresponding paper
//	 * @param speaker of tutorial
//	 */
//	public void setPaper(Paper paper) {
//		this.paper= paper;
//	}
//	
	
	/**
	 * Gets the time this event part ends
	 * @return This event part's new end time
	 */
	public LocalDateTime getEnd() {
		return end;
	}

	/**
	 * Sets the time this event part ends
	 * @param sessEnd the new time this event part ends
	 */
	public void setEnd(LocalDateTime sessEnd) {
		this.end = sessEnd;
	}

	/**
	 * Gets the title of this event part
	 * @return The title of this event part
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of this event part
	 * @param title The new title of this event part
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets a description of the event part
	 * @return A description of the event part
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets a description of the event part
	 * @param description A new description of the event part
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Gets this event's category
	 * @return This event's category
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * Sets this event's category
	 * @return This event's new category
	 */
	public void setPerson(Person person) {
		this.person = person;
	}
	
	
	public static EventPart findOrCreate(String name){
		EventPartJPAAccess eventPartFiler = new EventPartJPAAccess();
		if(name != null){
			EventPart e = eventPartFiler.getByName(name);
			if(e != null) return e;
		}
		EventPart e = new EventPart();
		e.setTitle(name); //Achtung kann hier null werden
		eventPartFiler.add(e);
		return e;
	}
	
}
