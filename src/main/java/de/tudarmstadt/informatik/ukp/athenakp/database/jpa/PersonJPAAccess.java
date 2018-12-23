package de.tudarmstadt.informatik.ukp.athenakp.database.jpa;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.PersonCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Person;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.List;

public class PersonJPAAccess implements PersonCommonAccess {

    /**
     * Common code used by all get methods which filter by simple column values.
     * @param name The name of the column to restrict
     * @param value The value to restrict the selection to
     * @return A List of all persons with the given restriction
     */
    private List<Person> getBy(String name, Object value) {
        EntityManager entityManager = PersistenceManager.getEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Person> criteriaQuery = builder.createQuery(Person.class);
        Root<Person> root = criteriaQuery.from(Person.class);
        criteriaQuery
                .select(root)
                .where(builder.equal(root.get(name), value));
        List<Person> result = entityManager.createQuery(criteriaQuery).getResultList();
        entityManager.close();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Person> getByPersonID(Long personID) {
        return getBy("personID", personID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Person> getByPrefix(String prefix) {
        return getBy("prefix", prefix);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Person> getByFullName(String fullName) {
        return getBy("fullName", fullName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Person> getByBirthdate(Integer year, Integer month, Integer day) {
        return getBy("birthdate", Timestamp.valueOf(String.format("%s-%s-%s 00:00:00", year, month, day)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Person> getByObit(Integer year, Integer month, Integer day) {
        return getBy("obit", Timestamp.valueOf(String.format("%s-%s-%s 00:00:00", year, month, day)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Person> getByInstitutionID(long institutionID) {
        return getBy("institutionID", institutionID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(Person data) {
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
    public void update(Person data) {
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
    public void delete(Person data) {
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
    public List<Person> get() {
        EntityManager entityManager = PersistenceManager.getEntityManager();
        List<Person> result = entityManager.createQuery("FROM Person").getResultList();
        entityManager.close();
        return result;
    }
}
