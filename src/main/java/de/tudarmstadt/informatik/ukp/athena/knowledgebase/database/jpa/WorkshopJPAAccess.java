package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Workshop;

/**
 * @author Daniel Lehmann
 */
public class WorkshopJPAAccess implements CommonAccess<Workshop> {
	private static Logger logger = LogManager.getLogger(WorkshopJPAAccess.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Workshop data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		try {
			entityManager.persist(data);
		}catch(EntityExistsException e) {  //branch not tested because exception shouldn't be thrown again just so junit can test for it
			logger.warn("{} already exists in the database. Maybe try update", data.getID());
		}
	}

	@Override
	public void commitChanges(Workshop data){
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		entityManager.getTransaction().commit();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Workshop data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
		//entityManager.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Workshop> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Workshop> result = entityManager.createQuery("FROM Workshop").getResultList();
		return result;
	}
}
