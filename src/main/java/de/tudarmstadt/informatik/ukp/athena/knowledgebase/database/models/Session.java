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
@Table(name="session")
public class Session {
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
	@Column(name = "description")
	private String description;
	/* Category */
	@Column(name = "category")
	private SessionCategory category;

	/*Start time*/
	@Column(name="begin")
	private LocalDateTime begin;
	/*End time*/
	@Column(name="end")
	private LocalDateTime end;

	/*Host*/
	//	@Column(name = "host") //FIXME: crashes - perhaps save id?
	//	private Person host;
	/* Place where this session happens, if empty look in sessions */
	@Column(name = "place")
	private String place;

	/* Associated papers */
	@Column(name = "paperTitles")
	@ElementCollection //similar to @JoinTable, but for model -> datatype relations instead of model -> model
	private Set<String> paperTitles = new HashSet<>();

	/* Papers, if any */
	//	@Column(name = "papers")
	//	private Set<Paper> papers;
	/* Sessions, if any */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable(
			name = "session_sessionParts",
			joinColumns = { @JoinColumn(name = "sessionID") },
			inverseJoinColumns = { @JoinColumn(name = "sessionPartID") }
			)
	private Set<SessionPart> sessionParts = new HashSet<>();

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
	 * @param end The new time this session ends
	 */
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	//	/**
	//	 * Gets the person who manages this session
	//	 * @return This session's manager
	//	 */
	//	public Person getHost() {
	//		return host;
	//	}
	//
	//	/**
	//	 * Sets the person who manages this session
	//	 * @param This session's new manager
	//	 */
	//	public void setHost(Person host) {
	//		this.host = host;
	//	}

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
	 * Gets this session's category
	 * @return This session's category
	 */
	public SessionCategory getCategory() {
		return category;
	}

	/**
	 * Sets this session's category
	 * @return This session's new category
	 */
	public void setCategory(SessionCategory category) {
		this.category = category;
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

	/**
	 * Gets this session's session parts (if any)
	 * @return This session's session parts
	 */
	public Set<SessionPart> getSessionParts() {
		return sessionParts;
	}

	/**
	 * Sets this session's session parts (if any)
	 * @param sessions This session's new session parts
	 */
	public void setSessionParts(Set<SessionPart> sessionParts) {
		this.sessionParts = sessionParts;
	}

	/**
	 * Adds a session part to this session's session part list
	 * @param s The session part to add
	 */
	public void addSessionPart(SessionPart s) {
		sessionParts.add(s);
	}
}
