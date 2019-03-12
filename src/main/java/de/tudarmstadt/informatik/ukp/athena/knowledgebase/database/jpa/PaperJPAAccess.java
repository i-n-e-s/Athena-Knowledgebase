package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;

public class PaperJPAAccess implements CommonAccess<Paper> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	/**
	 * Looks for equal attribute DB entries of Paper and returns the matching Paper Object
	 * If multiple Occurences are found in DB, return the first result
	 *
	 * @author Philipp Emmer
	 * @param paperToFind The paper object to search
	 * @return The first DB Entry of Paper with matching Attributes or null
	 */
	public Paper lookUpPaper( Paper paperToFind ) {
		List<Paper> matches = null;
		//1. Try to find matching SemanticScholarID
		if( paperToFind.getSemanticScholarID() != null ) {

			matches = getBy("semanticScholarID", paperToFind.getSemanticScholarID());
		}
		//Return first result of author with matching S2ID
		if( matches != null && matches.size() > 0 ) { return matches.get(0); }
		//2. If no results, search for title
		matches = getByTitle(paperToFind.getTitle());
		for ( Paper namesake : matches ) {
			//Choose the first one with matching attributes
			if ( namesake.equalsNullAsWildcard(paperToFind) ) { return namesake; }
		}

		//3. If nothing found, return null
		return null;
	}

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
}
