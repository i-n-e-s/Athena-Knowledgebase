package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="paper")
public class Paper
{
	/*Paper's author*/
	private Author author;
	/*Release date*/
	private Date releaseDate;
	/*Shown at conferences*/
	private Set<Conference> conferences = new HashSet<Conference>();
	//TODO: Metadata?

	/**
	 * Gets this paper's author
	 * @return This paper's author
	 */
	@Column(name="author", nullable=false)
	public Author getAuthor()
	{
		return author;
	}

	/**
	 * Sets this paper's author
	 * @param author The new author of this paper
	 */
	public void setAuthor(Author author)
	{
		this.author = author;
	}

	/**
	 * Gets this paper's release date
	 * @return This paper's release date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="release_date")
	public Date getReleaseDate()
	{
		return releaseDate;
	}

	/**
	 * Sets this paper's release date
	 * @param releaseDate The new release date of this paper
	 */
	public void setReleaseDate(Date releaseDate)
	{
		this.releaseDate = releaseDate;
	}

	/**
	 * Gets the conferences that this paper has been shown at
	 * @return The conferences that this paper has been shown at
	 */
	@ManyToMany
	@Column(name="conferences")
	public Set<Conference> getConferences()
	{
		return conferences;
	}
}
