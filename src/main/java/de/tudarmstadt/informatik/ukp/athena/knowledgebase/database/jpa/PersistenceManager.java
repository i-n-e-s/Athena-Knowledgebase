package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

public class PersistenceManager {

	/**
	 * This field should be configurable via commandline arguments or configuration files to support multiple persistence units.
	 */
	@PersistenceUnit
	private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernate");

	static EntityManager entityManager;
	
	/**
	 * Get the Hibernate-SessionFactory.
	 * @return the SessionFactory
	 */
	public static EntityManager getEntityManager() {
		if(entityManager == null) {
			entityManager = entityManagerFactory.createEntityManager();
		}
		return entityManager;
	}

	/**
	 * Shutdown SessionFactory.
	 * Warning: Since the field is final this cannot be undone at runtime.
	 */
	public static void closeEntityManagerFactory(){
		entityManagerFactory.close();
	}

}
