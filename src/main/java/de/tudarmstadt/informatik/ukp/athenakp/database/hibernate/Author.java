package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="author")
public class Author
{
	/*Unique id*/
	private long id;
	/*Author's name*/
	private String name;
	/*Work place, so to say*/
	private String university; //TODO: Make this its own entity?
	/*Written papers*/
	private Set<Paper> papers = new HashSet<>();

	/**
	 * Gets the unique id of this author
	 * @return The unique id of this author
	 */
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="id")
	public long getId()
	{
		return id;
	}

	/**
	 * Sets this author's id
	 * @param id The new id
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	/**
	 * Gets the name of this author
	 * @return The name of this author
	 */
	@Column(name="name", nullable=false)
	public String getName()
	{
		return name;
	}

	/**
	 * Sets this author's name
	 * @param name The new name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the university that this author works at
	 * @return The university that this author works at
	 */
	@Column(name="university")
	public String getUniversity()
	{
		return university;
	}

	/**
	 * Sets this author's university
	 * @param university The new university
	 */
	public void setUniversity(String university)
	{
		this.university = university;
	}

	/**
	 * Gets the papers that this author has written
	 * @return The papers that this author has written
	 */
	@ManyToMany
	@Column(name="papers")
	public Set<Paper> getPapers()
	{
		return papers;
	}
}
