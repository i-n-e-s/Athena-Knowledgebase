package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PaperJPAAccess implements CommonAccess<Paper> {
	private static Logger logger = LogManager.getLogger(PaperJPAAccess.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Paper data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		try {
			entityManager.persist(data);
		}catch(EntityExistsException e) { //branch not tested because exception shouldn't be thrown again just so junit can test for it
			logger.warn("{} already exists in the database. Maybe try update", data.getID());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commitChanges(Paper data){
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		entityManager.getTransaction().commit();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Paper data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
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
	 * @return An object from the DB with matching attributes, null if no object found or no search constraint set
	 */
	public Paper getByKnownAttributes(String id, String name) {
		//1. Build JPQL query for combined search
		EntityManager entityManager = PersistenceManager.getEntityManager();
		String query = "SELECT c FROM Paper c WHERE ";
		if( id != null )
			query = query + "c.semanticScholarID LIKE '"+id + "'";
		else if ( name != null) query = query +"c.title LIKE '" +name+ "'";
		
		else {
			System.out.println("No title and no ID given");//no title and no id given
			return null;
		}
		List<Paper> result = entityManager.createQuery(query).getResultList();
		if( result.size() > 0 ) { return result.get(0); }
		return null;
	}

	/**
	 * Finds a matching DB entry by the paperID of a given paper object
	 * If no attribute is specified, return null
	 * If multiple occurrences are found in DB, return the first result
	 *
	 * @param paperID of paper to be found
	 * @return An object from the DB with matching attributes, null if no object found or no search constraint set
	 */	
	public Paper getByPaperId(String id) {
		//1. Build JPQL query for combined search
		EntityManager entityManager = PersistenceManager.getEntityManager();
		String query = "SELECT c FROM Paper c WHERE ";
		if ( id != null) query = query + "c.paperID LIKE '" +id+ "'";//"c.title LIKE '" +name+ "'";
		
		else {
			System.out.println("No title and no ID given");//no title and no id given
			return null;
		}
		List<Paper> result = entityManager.createQuery(query).getResultList();
		if( result.size() > 0 ) { return result.get(0); }
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
		//1. Try to find matching Semantic Scholar ID
		if( semanticScholarID != null ) {
			Paper query = new Paper();
			query.setSemanticScholarID(semanticScholarID);
			Paper result = getByKnownAttributes(semanticScholarID, null);
			return result;
		}
		return null;
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean alreadyExists(String identifier){
		String query = "SELECT p FROM paper p WHERE p.title = '"+identifier.replace("'","''") + "'";
		EntityManager entityManager = PersistenceManager.getEntityManager();
		//Execute query
		List<Paper> matches = entityManager.createQuery(query).getResultList();
		//If results are found, return true.
		return (matches.size() > 0) ? true : false;
	}

	/**
	 * Looks for DB entries with matching title
	 *
	 * @author Philipp Emmer
	 * @param title The title of the wanted paper object to search
	 * @return DB entry of paper with matching S2ID, null if not found
	 */
	public Paper getByTitle( String title ) {
		//1. Build JPQL query
		if( title != null ) {
			String query = "SELECT c FROM Paper c WHERE c.title = '"+title.replace("'","''") + "'";
			logger.info(query);
			EntityManager entityManager = PersistenceManager.getEntityManager();
			List<Paper> matches = entityManager.createQuery(query).getResultList();

			if(matches.size() < 1) { //No matching paper could be found in the DB
				return null;
			}
			else { 					//Choose first result
				return matches.get(0);
			}
		}
		return null;
	}
}
