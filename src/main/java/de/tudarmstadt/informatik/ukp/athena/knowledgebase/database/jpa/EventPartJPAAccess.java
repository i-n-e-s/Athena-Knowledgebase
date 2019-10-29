package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Event;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.EventPart;

/**
 * @author Daniel Lehmann
 */
public class EventPartJPAAccess implements CommonAccess<EventPart> {
	private static Logger logger = LogManager.getLogger(EventPartJPAAccess.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(EventPart data) {
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
	public void commitChanges(EventPart data){
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		entityManager.getTransaction().commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(EventPart data) {
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
	public boolean alreadyExists(String identifier){
		String query = "SELECT p FROM EventPart p WHERE p.title = '"+identifier.replace("'","''") + "'";
		EntityManager entityManager = PersistenceManager.getEntityManager();
		//Execute query
		List<EventPart> matches = entityManager.createQuery(query).getResultList();
		//If results are found, return true.
		return (matches.size() > 0) ? true : false;
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EventPart> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<EventPart> result = entityManager.createQuery("FROM EventPart").getResultList();
		return result;
	}
	
	/**
	 * Returns event by name.
	 * @param name of the event
	 * @return The event or null if not found 
	 */
	public EventPart getByName(String identifier){
		String query = "SELECT e FROM EventPart e WHERE e.title = '"+identifier.replace("'","''") + "'";
		EntityManager entityManager = PersistenceManager.getEntityManager();

		//Execute query
		List<EventPart> matches = entityManager.createQuery(query).getResultList();

		//If results are found, return the first. Otherwise return null
		return (matches.size() > 0) ? matches.get(0) : null;
	}
	
}
