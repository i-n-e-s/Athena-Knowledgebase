package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Institution;

public class InstitutionJPAAccess implements CommonAccess<Institution> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Institution data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		try {
			entityManager.persist(data);
		}catch(EntityExistsException e) { //branch not tested because exception shouldn't be thrown again just so junit can test for it
			System.out.println(data.getID()+" already exists in the Database.");
		}
		entityManager.getTransaction().commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Institution data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
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
