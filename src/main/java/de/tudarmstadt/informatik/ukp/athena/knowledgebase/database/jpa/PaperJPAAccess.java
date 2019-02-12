package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
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
	 * Looks for equal attribute DB entries of Paper and returns the matching Paper Object
	 * If multiple Occurences are found in DB, return the first result
	 *
	 * @author Philipp Emmer
	 * @param paperToFind The paper object to search
	 * @return The first DB Entry of Paper with matching Attributes or null
	 */
	public Paper lookUpPaper( Paper paperToFind ) {
		List<Paper> matches = null;
		//1. Try to find matching SemanticScholarID
		if( paperToFind.getSemanticScholarID() != null ) {
			matches = getBy("semanticScholarID", paperToFind.getSemanticScholarID());
		}
		//Return first result of author with matching S2ID
		if( matches != null && matches.size() > 0 ) { return matches.get(0); }

		//2. If no results, search for title
		matches = getByTitle(paperToFind.getTitle());
		for ( Paper namesake : matches ) {
			//Choose the first one with matching attributes
			if ( namesake.equalsNullAsWildcard(paperToFind) ) { return namesake; }
		}

		//3. If nothing found, return null
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Paper data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.persist(data);
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
