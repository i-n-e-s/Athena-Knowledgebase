package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="sessionPart")
public class SessionPart{
	/*Unique id*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="sessionPartID")
	private long sessionPartID;
	/* Title */
	@Column(name = "title")
	private String title;
	/* Brief Description */
	@Column(name = "description", columnDefinition = "VARCHAR(1000)") //fixes titles that are too long for being storable in the column
	private String description;

	/*Start time*/
	@Column(name="begin")
	private LocalDateTime begin;
	/*End time*/
	@Column(name="end")
	private LocalDateTime end;

	/* Place where this session happens */
	@Column(name = "place")
	private String place;

	/**
	 * Gets the unique id of this session part
	 * @return The unique id of this session part
	 */
	public long getId() {
		return sessionPartID;
	}

	/**
	 * Sets this session part's id
	 * @param id The new id
	 */
	public void setId(long id) {
		this.sessionPartID = id;
	}

	/**
	 * Gets the place where this session part happens
	 * @return The place where this session part happens
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * Sets the place where this session part happens
	 * @param place The new place where this session part happens
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * Gets the time this session part begins
	 * @return This session part's begin time
	 */
	public LocalDateTime getBegin() {
		return begin;
	}

	/**
	 * Sets the time this session part begins
	 * @param begin The time this session part begins
	 */
	public void setBegin(LocalDateTime begin) {
		this.begin = begin;
	}

	/**
	 * Gets the time this session part ends
	 * @return This session part's new end time
	 */
	public LocalDateTime getEnd() {
		return end;
	}

	/**
	 * Sets the time this session part ends
	 * @param end the new time this session part ends
	 */
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	/**
	 * Gets the title of this session part
	 * @return The title of this session part
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of this session part
	 * @param title The new title of this session part
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets a description of the session part
	 * @return A description of the session part
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets a description of the session part
	 * @param description A new description of the session part
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
