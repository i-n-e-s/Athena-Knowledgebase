package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Institution;

public class InstitutionJPAAccess implements CommonAccess<Institution> {
	private static Logger logger = LogManager.getLogger(InstitutionJPAAccess.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Institution data) {
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
	public boolean alreadyExists(String identifier){
		String query = "SELECT i FROM institution i WHERE i.name = '"+identifier.replace("'","''") + "'";
		EntityManager entityManager = PersistenceManager.getEntityManager();
		//Execute query
		List<Institution> matches = entityManager.createQuery(query).getResultList();
		//If results are found, return true.
		return (matches.size() > 0) ? true : false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commitChanges(Institution data){
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		entityManager.getTransaction().commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Institution data) {
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
	public List<Institution> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Institution> result = entityManager.createQuery("FROM Institution").getResultList();
		return result;
	}
}
