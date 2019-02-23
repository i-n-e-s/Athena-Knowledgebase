package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Workshop;

/**
 * @author Daniel Lehmann
 */
public class WorkshopJPAAccess implements CommonAccess<Workshop> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Workshop data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		try {
			entityManager.persist(data);
		}catch(EntityExistsException e) {
			System.out.println(data.getID()+"already exist in the Database. Maybe try update");
		}
		entityManager.getTransaction().commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Workshop data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
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
