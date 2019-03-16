package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;

public class PaperJPAAccess implements CommonAccess<Paper> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Paper data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		try {
			entityManager.persist(data);
		}catch(EntityExistsException e) {
			System.out.println(data.getID()+" already exists in the Database. Maybe try update");
		}
		entityManager.getTransaction().commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Paper data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Paper> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Paper> result = entityManager.createQuery("FROM Paper").getResultList();
		return result;
	}

	/**
	 * Finds a matching DB Entry by the attributes of a given Paper Object, null is seen as wildcard
	 * Currently Only uses (decreasing priority): S2ID, Title
	 * If multiple occurrences are found in DB, return the first result
	 * @param toFind Paper Object to get the search constraints from
	 * @return An Object from the DB with matching attributes
	 */
	public List<Paper> getByKnownAttributes(Paper toFind) {

		//1. Build JPQL query
		String query = "SELECT c FROM Paper c WHERE ";
		boolean addedConstraint = false;
		if( toFind.getSemanticScholarID() != null ) {
			query = query + "c.semanticScholarID LIKE '"+toFind.getSemanticScholarID() + "'";
			addedConstraint = true;
		}
		if ( toFind.getTitle() != null && toFind.getTitle() != "" ) {
			if (addedConstraint) { query = query + " and "; }
			query = query + "c.title = '"+toFind.getTitle() + "'";
			addedConstraint = true;
		}

		System.out.println(query);

		//2. Execute query und return results
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Paper> result = entityManager.createQuery(query).getResultList();
		return result;

	}

	/**
	 * Looks for DB entries with matching SemanticScholar ID
	 *
	 * @author Philipp Emmer
	 * @param semanticScholarID The semanticScholarID of the wanted paper object to search
	 * @return DB Entry of Paper with matching S2ID or null
	 */
	@Deprecated
	public Paper getBySemanticScholarID( String semanticScholarID ) {
		List<Paper> matches = null;
		//1. Try to find matching SemanticScholarID
		if( semanticScholarID != null ) {
			Paper query = new Paper();
			query.setSemanticScholarID(semanticScholarID);
			return Paper.findOrCreate(query);
		}
		return null;
	}

	/**
	 * Looks for DB entries with matching title
	 *
	 * @author Philipp Emmer
	 * @param title The title of the wanted Paper object to search
	 * @return DB Entry of Paper with matching S2ID or null
	 */
	@Deprecated
	public Paper getByTitle( String title ) {
		List<Paper> matches = null;
		//1. Try to find matching SemanticScholarID
		if( title != null ) {
			Paper query = new Paper();
			query.setTitle(title);
			return Paper.findOrCreate(query);
		}
		return null;
	}
}
