package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.hibernate;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.PaperCommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;

/**
 * @author Daniel Lehmann
 */
@Deprecated
public class PaperHibernateAccess implements PaperCommonAccess {
	@Override
	public List<Paper> getByPaperID(Long paperID) {
		return getBy("paperID", paperID);
	}

	@Override
	public List<Paper> getByAuthor(String author) { //TODO: implement this
		return null;
	}

	@Override
	public List<Paper> getByReleaseDate(Integer year, Integer month, Integer day) {
		return getBy("releaseDate", LocalDate.of(year, month, day));
	}

	@Override
	public List<Paper> getByReleaseRange(Integer year1, Integer month1, Integer year2, Integer month2) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(Paper.class);
		List<Paper> result;
		LocalDate date1 = LocalDate.of(year1, month1, 1);
		LocalDate date2 = LocalDate.of(year2, month2, 1); //papers are only stored by release year/month

		criteria.add(Restrictions.between("releaseDate", date1, date2));
		result = criteria.list();
		session.close();
		return result;
	}

	@Override
	public List<Paper> getByTopic(String topic) {
		return getBy("topic", topic);
	}

	@Override
	public List<Paper> getByTitle(String title) {
		return getBy("title", title);
	}

	@Override
	public List<Paper> getByHref(String href) {
		return getBy("href", href);
	}

	@Override
	public List<Paper> getByPdfFileSize(Integer pdfFileSize) {
		return getBy("pdfFileSize", pdfFileSize);
	}

	@Override
	public List<Paper> getByAnthology(String anthology){ return getBy("anthology", anthology);}

	/**
	 * Common code used by all get methods above
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all papers with the given restriction
	 */
	private List<Paper> getBy(String name, Object value) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(Paper.class);
		List<Paper> result;

		criteria.add(Restrictions.eq(name, value));
		result = criteria.list();
		session.close();
		return result;
	}

	@Override
	public void add(Paper data) {
		Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.save(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void update(Paper data) {
		Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.update(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void delete(Paper data) {
		Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.remove(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public List<Paper> get() {
		Session session = HibernateUtils.getSessionFactory().openSession();
		List<Paper> result = session.createCriteria(Paper.class).list();

		session.close();
		return result;
	}
}
