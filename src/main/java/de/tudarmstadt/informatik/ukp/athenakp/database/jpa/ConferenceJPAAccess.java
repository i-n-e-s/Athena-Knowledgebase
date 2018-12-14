package de.tudarmstadt.informatik.ukp.athenakp.database.jpa;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.ConferenceCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.List;

public class ConferenceJPAAccess implements ConferenceCommonAccess {

    /**
     * Common code used by all get methods which filter by simple column values.
     * @param name The name of the column to restrict
     * @param value The value to restrict the selection to
     * @return A List of all persons with the given restriction
     */
    private List<Conference> getBy(String name, Object value) {
        EntityManager entityManager = PersistenceManager.getEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Conference> criteriaQuery = builder.createQuery(Conference.class);
        Root<Conference> root = criteriaQuery.from(Conference.class);
        criteriaQuery
                .select(root)
                .where(builder.equal(root.get(name), value));
        List<Conference> result = entityManager.createQuery(criteriaQuery).getResultList();
        entityManager.close();
        return result;
    }

    @Override
    public List<Conference> getByName(String name) {
        return getBy("name", name);
    }

    @Override
    public List<Conference> getByStartDate(Integer year, Integer month, Integer day) {
        LocalDate localDate = LocalDate.of(year,month,day);
        return getBy("startDate", localDate);
    }

    @Override
    public List<Conference> getByEndDate(Integer year, Integer month, Integer day) {
        LocalDate localDate = LocalDate.of(year,month,day);
        return getBy("endDate", localDate);
    }

    @Override
    public List<Conference> getByAuthor(long personID) {
        return null;
    }

    @Override
    public List<Conference> getByPaper(long paperID) {
        return null;
    }

    @Override
    public List<Conference> getByCountry(String country) {
        return getBy("country", country);
    }

    @Override
    public List<Conference> getByCity(String city) {
        return getBy("city", city);
    }

    @Override
    public void add(Conference data) {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        entityManager.getTransaction().begin();
        entityManager.persist(data);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void update(Conference data) {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        entityManager.getTransaction().begin();
        entityManager.merge(data);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void delete(Conference data) {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        entityManager.getTransaction().begin();
        entityManager.remove(data);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public List<Conference> get() {
        EntityManager entityManager = PersistenceManager.getEntityManager();
        List<Conference> result = entityManager.createQuery("FROM Conference").getResultList();
        entityManager.close();
        return result;
    }
}
