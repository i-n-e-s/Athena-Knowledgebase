package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

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
	private Institution institute;
	/*Written papers*/
	private Set<Paper> papers = new HashSet<>();
	/*Authors, this Author has collaborated with*/
	private Set<Author> coAuthors;
	/*Papers this Author has quoted*/
	private Set<Paper> quotations;
	/*Birthdate of the Author*/
	private Date birthDate;

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
	public Institution getInstitute()
	{
		return institute;
	}

	/**
	 * Sets this author's Institute
	 * @param institute The new institute
	 */
	public void setInstitute(Institution institute)
	{
		this.institute = institute;
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

	/**
	 * Gets the papers that this author has quoted
	 * @return The papers that this author has quoted
	 */
	@ManyToMany
	@Column(name="quotations")
	public Set<Paper> getQuotations()
	{
		return quotations;
	}

	/**
	 * Gets the authors, this one has collaborated with
	 * @return The authors, this one has collaborated with
	 */
	@ManyToMany
	@Column(name="coAuthors")
	public Set<Author> getCoAuthors()
	{
		return coAuthors;
	}



	/**
	 * Gets the author's birthdate
	 * @return This author's birth date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="birth_date")
	public Date getBirthDate()
	{
		return birthDate;
	}

	/**
	 * Sets the author's birthdate
	 * @param birthDate This author's new birth date
	 */
	public void setBirthDate(Date birthDate)
	{
		this.birthDate = birthDate;
	}


}
