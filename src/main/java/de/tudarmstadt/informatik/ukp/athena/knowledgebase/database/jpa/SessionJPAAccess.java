package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Session;

/**
 * @author Daniel Lehmann
 */
public class SessionJPAAccess implements CommonAccess<Session> {
	@Override
	public void add(Session data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		try {
			entityManager.persist(data);
		}catch(EntityExistsException e) { //branch not tested because exception shouldn't be thrown again just so junit can test for it
			System.out.println(data.getID()+" already exists in the Database. Maybe try update");
		}
		entityManager.getTransaction().commit();
	}

	@Override
	public void delete(Session data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
	}

	@Override
	public List<Session> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Session> result = entityManager.createQuery("FROM Session").getResultList();
		return result;
	}
}
