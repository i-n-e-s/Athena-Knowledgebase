package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
	private List<Author> authors;
	/*Release date*/
	private Date releaseDate;
	/*Topic of the paper*/
	private String topic;
	/*Title of the paper*/
	private String title;
	/*URL to PDF*/
	private String href;
	/*PDF filesize in Bytes*/
	private int pdfFileSize;
	/*Papers quoted in this paper*/
	private List<Paper> quotations;
	/*Papers this paper is quoted in*/
	private List<Paper> isQuotedIn;
	/*Shown at conferences*/
	private Set<Conference> conferences = new HashSet<Conference>();
	//TODO: Metadata?

	/**
	 * Gets List of this paper's authors
	 * @return List of this paper's authors
	 */
	@ManyToMany
	@Column(name="authors", nullable=false)
	public List<Author> getAuthors()
	{
		return authors;
	}

	/**
	 * Sets this paper's authors
	 * @param authors The new author of this paper
	 */
	public void setAuthors(List<Author> authors)
	{
		this.authors = authors;
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
	 * Gets this papers topic
	 *
	 * @return The topic of this paper
	 */
	@Column(name = "topic")
	public String getTopic() {
		return topic;
	}

	/**
	 * Sets this institution's topic
	 *
	 * @param topic The new topic
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}


	/**
	 * Gets this papers title
	 *
	 * @return The title of this paper
	 */
	@Column(name = "title")
	public String getTitle() {
		return title;
	}

	/**
	 * Sets this institution's title
	 *
	 * @param title The new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the link to this papers PDF file
	 *
	 * @return The link to this papers PDF file
	 */
	@Column(name = "pdfHref")
	public String getHref() {
		return href;
	}
	/**
	 * Sets the link to this papers PDF file
	 *
	 * @param href The new link to this papers PDF file
	 */
	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * Gets the filesize of this papers PDF file in Bytes
	 *
	 * @return The filesize of this papers PDF file in Bytes
	 */
	@Column(name = "pdfFileSize")
	public int getPdfFileSize() {
		return pdfFileSize;
	}
	/**
	 * Sets the filesize of this papers PDF file in Bytes
	 *
	 * @param pdfFileSize The new filesize of this papers PDF file in Bytes
	 */
	public void setPdfFileSize(int pdfFileSize) {
		this.pdfFileSize = pdfFileSize;
	}


	/**
	 * Gets the papers this one is quoted in
	 * @return The papers this one is quoted in
	 */
	@ManyToMany
	@Column(name="isQuotedIn")
	public List<Paper> getIsQuotedIn()
	{
		return isQuotedIn;
	}

	/**
	 * Gets the papers this one has quoted
	 * @return The papers this one has quoted
	 */
	@ManyToMany
	@Column(name="quotations")
	public List<Paper> getQuotations()
	{
		return quotations;
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
