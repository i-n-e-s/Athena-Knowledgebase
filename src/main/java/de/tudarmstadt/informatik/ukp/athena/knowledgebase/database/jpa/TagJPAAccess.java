package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Event;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Tag;

/**
 * @author Daniel Lehmann
 */
public class TagJPAAccess implements CommonAccess<Tag> {
	private static Logger logger = LogManager.getLogger(TagJPAAccess.class);

	@Override
	public void add(Tag data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		try {
			entityManager.persist(data);
		}catch(EntityExistsException e) { //branch not tested because exception shouldn't be thrown again just so junit can test for it
			logger.warn("{} already exists in the database. Maybe try update", data.getID());
		}
	}

	@Override
	public void commitChanges(Tag data){
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		entityManager.getTransaction().commit();
	}

	@Override
	public boolean alreadyExists(String identifier){
		String query = "SELECT e FROM Tag e WHERE e.name = '"+identifier.replace("'","''") + "'";
		EntityManager entityManager = PersistenceManager.getEntityManager();

		//Execute query
		List<Event> matches = entityManager.createQuery(query).getResultList();

		//If results are found, return them. Otherwise return null
		return (matches.size() > 0) ? true : false;
	}

	public Tag getByName(String identifier){
		String query = "SELECT e FROM Tag e WHERE e.name = '"+identifier.replace("'","''") + "'";
		EntityManager entityManager = PersistenceManager.getEntityManager();

		//Execute query
		List<Tag> matches = entityManager.createQuery(query).getResultList();

		//If results are found, return the first. Otherwise return null
		return (matches.size() > 0) ? matches.get(0) : null;
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
	public Tag getByKnownAttributes(String name) {
		//1. Build JPQL query for combined search
		EntityManager entityManager = PersistenceManager.getEntityManager();
		String query = "SELECT c FROM Tag c WHERE ";
		if ( name != null) query = query + "c.name LIKE '" +name+ "'";
		else {
			System.out.println("No title and no ID given");//no title and no id given
			return null;
		}
		List<Tag> result = entityManager.createQuery(query).getResultList();
		if( result.size() > 0 ) { return result.get(0); }
		return null;
	}


	@Override
	public void delete(Tag data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
	}

	@Override
	public List<Tag> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Tag> result = entityManager.createQuery("FROM Tag").getResultList();
		return result;
	}
}
