package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;

public class PersonJPAAccess implements CommonAccess<Person> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Person data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		try {
			entityManager.persist(data);
		}catch(EntityExistsException e) { //branch not tested because exception shouldn't be thrown again just so junit can test for it
			System.out.println(data.getID()+" already exists in the Database");
		}
		entityManager.getTransaction().commit();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Person data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Person> result = entityManager.createQuery("FROM Person").getResultList();
		return result;
	}
}
