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
	@Id
	@Column(name="name")
	private String name;
	/*First day of conference*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_date")
	private Date startDate;
	/*Last day of conference*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_date")
	private Date endDate;
	/*Authors that talked*/
	@ManyToMany
	@Column(name="authors")
	private Set<Author> authors = new HashSet<Author>();
	/*Shown papers*/
	@ManyToMany
	@Column(name="papers")
	private Set<Paper> papers = new HashSet<Paper>();
	//TODO: Workshops? Other data?

	/**
	 * Gets the name of this conference
	 * @return The name of this conference
	 */
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
	public Set<Author> getAuthors()
	{
		return authors;
	}

	/**
	 * Gets the papers that were shown at this conference
	 * @return The papers that were shown at this conference
	 */
	public Set<Paper> getPapers()
	{
		return papers;
	}
}
