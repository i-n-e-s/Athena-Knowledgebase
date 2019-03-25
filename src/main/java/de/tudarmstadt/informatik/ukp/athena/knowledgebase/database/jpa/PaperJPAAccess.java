package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;

public class PaperJPAAccess implements CommonAccess<Paper> {
	private static Logger logger = LogManager.getLogger(PaperJPAAccess.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Paper data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		try {
			entityManager.persist(data);
		}catch(EntityExistsException e) { //branch not tested because exception shouldn't be thrown again just so junit can test for it
			logger.warn("{} already exists in the database. Maybe try update", data.getID());
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
	 * Finds a matching DB entry by the attributes of a given paper object, null is seen as wildcard
	 * If no attribute is specified, return null
	 * Currently only uses (decreasing priority): S2ID, Title
	 * If multiple occurrences are found in DB, return the first result
	 *
	 * @param toFind Paper object to get the search constraints from
	 * @return An object from the DB with matching attributes
	 */
	public List<Paper> getByKnownAttributes(Paper toFind) {

		//1. Build JPQL query for combined search
		String query = "SELECT c FROM Paper c WHERE ";
		boolean addedConstraint = false;
		if( toFind.getSemanticScholarID() != null ) {
			System.out.println("Got parameter s2id");
			query = query + "c.semanticScholarID LIKE '"+toFind.getSemanticScholarID() + "'";
			addedConstraint = true;
		}
		if ( toFind.getTitle() != null && toFind.getTitle() != "" ) {
			System.out.println("Got parameter title");
			if (addedConstraint) { query = query + " and "; }
			query = query + "c.title = '"+toFind.getTitle().replace("'", "''") + "'";
			addedConstraint = true;
		}

		if ( !addedConstraint ) { return null; }
		logger.info(query);

		//2. Execute query
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Paper> result = entityManager.createQuery(query).getResultList();
		System.out.println("Got "+ result.size()+ " results");

		if( result.size() > 0 ) { return result; }

		//3. If nothing found, try searching for Attributes separately
		if( toFind.getSemanticScholarID() != null ) {
			query = "SELECT c FROM Paper c WHERE c.semanticScholarID LIKE '"+toFind.getSemanticScholarID() + "'";
			result = entityManager.createQuery(query).getResultList();
			if( result.size() > 0 ) { return result; }
		}
		if ( toFind.getTitle() != null && toFind.getTitle() != "" ) {
			query = "SELECT c FROM Paper c WHERE c.title = '"+toFind.getTitle().replace("'", "''") + "'";
			result = entityManager.createQuery(query).getResultList();
			if( result.size() > 0 ) { return result; }
		}

		//4. If still nothing found, return null
		return null;
	}

	/**
	 * Looks for DB entries with matching Semantic Scholar ID
	 *
	 * @author Philipp Emmer
	 * @param semanticScholarID The Semantic Scholar ID of the wanted paper object to search
	 * @return DB entry of paper with matching S2ID, null if not found
	 */
	public Paper getBySemanticScholarID( String semanticScholarID ) {
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
	 * @param title The title of the wanted paper object to search
	 * @return DB entry of Paper with matching S2ID, null if not found
	 */
	public Paper getByTitle( String title ) {
		//1. Build JPQL query
		if( title != null ) {
			String query = "SELECT c FROM Paper c WHERE c.title = '"+title.replace("'","''") + "'";
			logger.info(query);
			EntityManager entityManager = PersistenceManager.getEntityManager();
			List<Paper> matches = entityManager.createQuery(query).getResultList();

			if(matches.size() < 1) { //No matching paper could be found in the DB
				return new Paper();
			}
			else { 					//Choose first result
				return matches.get(0);
			}
		}
		return null;
	}
}
