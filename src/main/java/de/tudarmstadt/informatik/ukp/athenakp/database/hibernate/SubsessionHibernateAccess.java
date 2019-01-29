package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.SubsessionCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Subsession;

/**
 * @author Daniel Lehmann
 */
@Deprecated
public class SubsessionHibernateAccess implements SubsessionCommonAccess {
	@Override
	public List<Subsession> getById(Long id) {
		return getBy("subsessionID", id);
	}

	@Override
	public List<Subsession> getByStartTime(Integer year, Integer month, Integer day, Integer hour, Integer minute) {
		LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute);
		return getBy("begin", localDateTime);
	}

	@Override
	public List<Subsession> getByEndTime(Integer year, Integer month, Integer day, Integer hour, Integer minute) {
		LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute);
		return getBy("end", localDateTime);
	}

	@Override
	public List<Subsession> getByTitle(String title) {
		return getBy("title", title);
	}

	@Override
	public List<Subsession> getByDescription(String description) {
		return getBy("description", description);
	}

	/**
	 * Common code used by all get methods above
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all persons with the given restriction
	 */
	private List<Subsession> getBy(String name, Object value) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(Subsession.class);
		List<Subsession> result;

		criteria.add(Restrictions.eq(name, value));
		result = criteria.list();
		session.close();
		return result;
	}

	@Override
	public void add(Subsession data) {
		Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.save(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void update(Subsession data) {
		Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.update(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void delete(Subsession data) {
		Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.remove(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public List<Subsession> get() {
		Session session = HibernateUtils.getSessionFactory().openSession();
		List<Subsession> result = session.createCriteria(Subsession.class).list();

		session.close();
		return result;
	}
}
