package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.EventJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.TagJPAAccess;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="tag")
public class Tag extends Model{
	/*Unique id*/
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="tagID")
	private Long tagID;
	
	/* name of the associated content */
	@Column(name = "name")
	private String name;

	/* Category */
	@Column(name = "category")
	private TagCategory category;

	
	/*Taged papers*/
	@JsonIgnore
	@Hierarchy(entityName="paper")
	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(
			name = "tag_paper",
			joinColumns = { @JoinColumn(name = "tagID") },
			inverseJoinColumns = { @JoinColumn(name = "paperID") }
			)
	private Set<Paper> papers = new HashSet<>();

	
	/**
	 * Gets the unique id of this tag
	 * @return The unique id of this tag
	 */
	public Long getId() {
		return tagID;
	}

	/**
	 * Sets this tag's id
	 * @param id The new id
	 */
	public void setId(Long id) {
		this.tagID = id;
	}

	/**
	 * Gets the name of this tag
	 * @return The name of this tag
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this tag
	 * @param title The new name of this tag
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	/**
	 * Gets this event's category
	 * @return This event's category
	 */
	public TagCategory getCategory() {
		return category;
	}

	/**
	 * Sets this event's category
	 * @return This event's new category
	 */
	public void setCategory(TagCategory category) {
		this.category = category;
	}
	
	/**
	 * Gets the papers this tag is associated to
	 * @return The papers this author has written
	 */
	public Set<Paper> getPapers() {
		return papers;
	}

	/**
	 * Sets this tag's papers
	 * @param papers The new papers of this tag
	 */
	public void setPapers(Set<Paper> papers) {
		this.papers = papers;
	}

	/**
	 * Adds a paper to this tag's paper list
	 * @param p The paper to add
	 */
	public void addPaper(Paper p) {
		papers.add(p);
		if(!p.getTags().contains(this)) {
			p.addTag(this);
		}
	}
	
	
	/**
	 * Looks through the database if a tag of this name exists and returns it
	 * or creates a new tag of the same name.
	 * @return A tag of this name either from the database or freshly created
	 */

	
	public static Tag findOrCreate(String name){
		TagJPAAccess tagFiler = new TagJPAAccess();
		if(name != null){
			Tag t = tagFiler.getByName(name);
			if(t != null) return t;
		}
		Tag t = new Tag();
		t.setName(name); //Achtung kann hier null werden
		tagFiler.add(t);
		return t;
	}
	
	
	
}