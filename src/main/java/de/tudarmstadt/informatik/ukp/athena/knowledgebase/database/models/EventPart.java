package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="eventPart")
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
	@Column(name = "description", columnDefinition = "VARCHAR(1000)") //fixes titles that are too Long for being storable in the column
	private String description;

	/*Start time*/
	@Column(name="begin")
	private String begin;
	/*End time*/
	@Column(name="end")
	private String end;

	/* Place where this event happens */
	@Column(name = "place")
	private String place;

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
	public String getBegin() {
		return begin;
	}

	/**
	 * Sets the time this event part begins
	 * @param sessStart The time this event part begins
	 */
	public void setBegin(String sessStart) {
		this.begin = sessStart;
	}

	/**
	 * Gets the time this event part ends
	 * @return This event part's new end time
	 */
	public String getEnd() {
		return end;
	}

	/**
	 * Sets the time this event part ends
	 * @param sessEnd the new time this event part ends
	 */
	public void setEnd(String sessEnd) {
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
}
