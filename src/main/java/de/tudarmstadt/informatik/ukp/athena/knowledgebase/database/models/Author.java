package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DiscriminatorValue(value = "author")
public class Author extends Person {

	/*Written papers*/
	@JsonIgnore //fixes infinite recursion
	@ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable(
			name = "author_paper",
			joinColumns = { @JoinColumn(name = "authorID") },
			inverseJoinColumns = { @JoinColumn(name = "paperID") }
			)
	private Set<Paper> papers = new HashSet<>();

	/**
	 * Gets the papers this author has written
	 * @return The papers this author has written
	 */
	public Set<Paper> getPapers() {
		return papers;
	}

	/**
	 * Sets this author's papers
	 * @param papers The new paper of this author
	 */
	public void setPapers(Set<Paper> papers) {
		this.papers = papers;
	}

	/**
	 * Adds a paper to this author's paper list
	 * @param p The paper to add
	 */
	public void addPaper(Paper p) {
		papers.add(p);
	}
}
