package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="session")
public class Session{ //TODO: chair
	/*Unique id*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="sessionID")
	private long sessionID;
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
	/* Subessions, if any */
	@Column(name = "paperTitles")
	@ElementCollection
	private Set<String> paperTitles = new HashSet<>();

	/**
	 * Gets the unique id of this session
	 * @return The unique id of this session
	 */
	public long getId() {
		return sessionID;
	}

	/**
	 * Sets this session's id
	 * @param id The new id
	 */
	public void setId(long id) {
		this.sessionID = id;
	}

	/**
	 * Gets the place where this session happens
	 * @return The place where this session happens
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * Sets the place where this session happens
	 * @param place The new place where this session happens
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * Gets the time this session begins
	 * @return This session's begin time
	 */
	public LocalDateTime getBegin() {
		return begin;
	}

	/**
	 * Sets the time this session begins
	 * @param begin The time this session begins
	 */
	public void setBegin(LocalDateTime begin) {
		this.begin = begin;
	}

	/**
	 * Gets the time this session ends
	 * @return This session's new end time
	 */
	public LocalDateTime getEnd() {
		return end;
	}

	/**
	 * Sets the time this session ends
	 * @param end the new time this session ends
	 */
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	/**
	 * Gets the title of this session
	 * @return The title of this session
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of this session
	 * @param title The new title of this session
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets a description of the session
	 * @return A description of the session
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets a description of the session
	 * @param description A new description of the session
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets this session's paper's titles (if any)
	 * @return This session's paper's titles
	 */
	public Set<String> getPaperTitles() {
		return paperTitles;
	}

	/**
	 * Sets this session's paper's titles (if any)
	 * @param paperTitles This session's new paper's titles
	 */
	public void setPaperTitles(Set<String> paperTitles) {
		this.paperTitles = paperTitles;
	}

	/**
	 * Adds a paper title to this session's paper's titles list
	 * @param s The paper title to add
	 */
	public void addPaperTitle(String s) {
		paperTitles.add(s);
	}
}
