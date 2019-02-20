package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.InstitutionCommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Institution;

public class InstitutionJPAAccess implements InstitutionCommonAccess {

	/**
	 * Common code used by all get methods which filter by simple column values.
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all institutions with the given restriction
	 */
	private List<Institution> getBy(String name, Object value) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Institution> criteriaQuery = builder.createQuery(Institution.class);
		Root<Institution> root = criteriaQuery.from(Institution.class);
		criteriaQuery
		.select(root)
		.where(builder.equal(root.get(name), value));
		List<Institution> result = entityManager.createQuery(criteriaQuery).getResultList();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Institution> getByInstitutionID(Long institutionID) {
		return getBy("institutionID", institutionID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Institution> getByName(String name) {
		return getBy("name", name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Institution> getByPersonID(long personID) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Institution data) {
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
	public void update(Institution data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.merge(data);
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
