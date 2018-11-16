package de.tudarmstadt.informatik.ukp.athenakp.database.access;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.tudarmstadt.informatik.ukp.athenakp.database.HibernateUtils;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Institution;

/**
 * @author Daniel Lehmann
 */
public class InstitutionHibernateAccess implements InstitutionCommonAccess
{
	@Override
	public List<Institution> getByInstitutionID(Long institutionID)
	{
		return getBy("institutionID", institutionID);
	}

	@Override
	public List<Institution> getByName(String name)
	{
		return getBy("name", name);
	}

	@Override
	public List<Institution> getByPerson(String person) //TODO: implement
	{
		return null;
	}

	/**
	 * Common code used by all get methods above
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all persons with the given restriction
	 */
	private List<Institution> getBy(String name, Object value)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(Institution.class);
		List<Institution> result;

		criteria.add(Restrictions.eq(name, value));
		result = criteria.list();
		session.close();
		return result;
	}

	@Override
	public void add(Institution data)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.save(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void update(Institution data)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.update(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void delete(Institution data)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.remove(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public List<Institution> get()
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		List<Institution> result = session.createCriteria(Institution.class).list();

		session.close();
		return result;
	}
}
