package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="conference")
public class Conference
{
	/*Name of conference*/
	private String name;
	/*First day of conference*/
	private Date startDate;
	/*Last day of conference*/
	private Date endDate;
	/*Authors that talked*/
	private Set<Author> authors = new HashSet<Author>();
	/*Shown papers*/
	private Set<Paper> papers = new HashSet<Paper>();
	//TODO: Workshops? Other data?

	/**
	 * Gets the name of this conference
	 * @return The name of this conference
	 */
	@Id
	@Column(name="name")
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this conference
	 * @param name The new name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the date of the day this conference started
	 * @return The date of the day this conference started
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_date")
	public Date getStartDate()
	{
		return startDate;
	}

	/**
	 * Sets the date of the day this conference started
	 * @param startDate The new start date
	 */
	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	/**
	 * Gets the date of the day this conference ended
	 * @return The date of the day this conference ended
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_date")
	public Date getEndDate()
	{
		return endDate;
	}

	/**
	 * Sets the date of the day this conference ended
	 * @param endDate The new end date
	 */
	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}

	/**
	 * Gets the authors that talked at this conference
	 * @return The authors that talked at this conference
	 */
	@ManyToMany
	@Column(name="authors")
	public Set<Author> getAuthors()
	{
		return authors;
	}

	/**
	 * Gets the papers that were shown at this conference
	 * @return The papers that were shown at this conference
	 */
	@ManyToMany
	@Column(name="papers")
	public Set<Paper> getPapers()
	{
		return papers;
	}
}
