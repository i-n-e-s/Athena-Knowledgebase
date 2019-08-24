package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.EventPart;

/**
 * @author Daniel Lehmann
 */
public class EventPartJPAAccess implements CommonAccess<EventPart> {
	private static Logger logger = LogManager.getLogger(EventPartJPAAccess.class);

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

	@Override
	public void commitChanges(EventPart data){
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		entityManager.getTransaction().commit();
	}

	@Override
	public void delete(EventPart data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
	}

	@Override
	public boolean alreadyExists(String identifier){
		String query = "SELECT p FROM eventpart p WHERE p.title = '"+identifier.replace("'","''") + "'";
		EntityManager entityManager = PersistenceManager.getEntityManager();
		//Execute query
		List<EventPart> matches = entityManager.createQuery(query).getResultList();
		//If results are found, return true.
		return (matches.size() > 0) ? true : false;
	}

	@Override
	public List<EventPart> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<EventPart> result = entityManager.createQuery("FROM EventPart").getResultList();
		return result;
	}
}
