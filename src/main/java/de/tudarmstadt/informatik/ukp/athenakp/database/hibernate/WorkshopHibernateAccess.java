package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.WorkshopCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Workshop;

/**
 * @author Daniel Lehmann
 */
public class WorkshopHibernateAccess implements WorkshopCommonAccess {
	@Override
	public List<Workshop> getById(Long id) {
		return getBy("id", id);
	}

	@Override
	public List<Workshop> getByConference(String conference) {
		return getBy("conference", conference);
	}

	@Override
	public List<Workshop> getByDate(Integer year, Integer month, Integer day) {
		LocalDate localDate = LocalDate.of(year, month, day);
		return getBy("date", localDate);
	}

	@Override
	public List<Workshop> getByStartTime(Integer hour, Integer minute) {
		LocalTime localTime = LocalTime.of(hour, minute);
		return getBy("begin", localTime);
	}

	@Override
	public List<Workshop> getByEndTime(Integer hour, Integer minute) {
		LocalTime localTime = LocalTime.of(hour, minute);
		return getBy("end", localTime);
	}

	@Override
	public List<Workshop> getByPlace(String place) {
		return getBy("place", place);
	}

	@Override
	public List<Workshop> getByTitle(String title) {
		return getBy("title", title);
	}

	@Override
	public List<Workshop> getByAbbreviation(String abbreviation) {
		return getBy("abbreviation", abbreviation);
	}

	/**
	 * Common code used by all get methods above
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all persons with the given restriction
	 */
	private List<Workshop> getBy(String name, Object value) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(Workshop.class);
		List<Workshop> result;

		criteria.add(Restrictions.eq(name, value));
		result = criteria.list();
		session.close();
		return result;
	}

	@Override
	public void add(Workshop data) {
		Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.save(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void update(Workshop data) {
		Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.update(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void delete(Workshop data) {
		Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.remove(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public List<Workshop> get() {
		Session session = HibernateUtils.getSessionFactory().openSession();
		List<Workshop> result = session.createCriteria(Workshop.class).list();

		session.close();
		return result;
	}
}
