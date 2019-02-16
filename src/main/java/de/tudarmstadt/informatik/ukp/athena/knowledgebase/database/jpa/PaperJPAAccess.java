package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.PaperCommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;

public class PaperJPAAccess implements PaperCommonAccess {

	/**
	 * Common code used by all get methods which filter by simple column values.
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all papers with the given restriction
	 */
	private List<Paper> getBy(String name, Object value) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Paper> criteriaQuery = builder.createQuery(Paper.class);
		Root<Paper> root = criteriaQuery.from(Paper.class);
		criteriaQuery
		.select(root)
		.where(builder.equal(root.get(name), value));
		List<Paper> result = entityManager.createQuery(criteriaQuery).getResultList();
		entityManager.close();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Paper> getByPaperID(Long paperID) {
		return getBy("paperID", paperID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Paper> getByAuthor(String author) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Paper> getByReleaseDate(Integer year, Integer month, Integer day) {
		return getBy("releaseDate", LocalDate.of(year, month, day));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Paper> getByReleaseRange(Integer year1, Integer month1, Integer year2, Integer month2) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Paper> criteriaQuery = builder.createQuery(Paper.class);
		Root<Paper> root = criteriaQuery.from(Paper.class);
		LocalDate date1 = LocalDate.of(year1, month1, 1);
		LocalDate date2 = LocalDate.of(year2, month2, 1);
		criteriaQuery
		.select(root)
		.where(builder.between(root.get("releaseDate"), date1, date2));
		List<Paper> result = entityManager.createQuery(criteriaQuery).getResultList();
		entityManager.close();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Paper> getByTopic(String topic) {
		return getBy("topic", topic);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Paper> getByTitle(String title) {
		return getBy("title", title);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Paper> getByHref(String href) {
		return getBy("href", href);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Paper> getByPdfFileSize(Integer pdfFileSize) {
		return getBy("pdfFileSize", pdfFileSize);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Paper> getByAnthology(String anthology) {
		return getBy("anthology", anthology);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Paper data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		try {
			entityManager.persist(data);
		}catch(EntityExistsException e) {
			System.out.println(data.getID()+"already exist in the Database. Maybe try update");
		}catch(PersistenceException e) {
			System.err.println(data.getID() + String.format(" is detached and can not be added. Use update(%s data)",data.getClass().getSimpleName()));
		}
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Paper data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.merge(data);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Paper data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Paper> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Paper> result = entityManager.createQuery("FROM Paper").getResultList();
		entityManager.close();
		return result;
	}
}
