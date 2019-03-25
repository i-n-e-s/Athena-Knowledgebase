package de.tudarmstadt.informatik.ukp.athena.knowledgebase.models;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.JPATestdatabase;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;

@SuppressWarnings("javadoc")
public class PersonTest {

	static JPATestdatabase testDB;

	static ConfigurableApplicationContext ctx;

	@BeforeClass
	public static void setUpDatabase() {
		ctx = SpringApplication.run(JPATestdatabase.class, "");
		testDB = new JPATestdatabase();
		testDB.createDB();
	}

	@Before
	public void resetDB() {
		testDB.createDB(); //Performance hungry if done before every test
	}

	@AfterClass
	public static void shutdownDatabase() {
		ctx.close();
	}

	@Test
	public void personFindOrCreateTest() {
		Person query = new Person();
		query.setFullName("Author 5");
		assertEquals("0", String.valueOf(query.getPersonID()));
		Person uut = Person.findOrCreate(query);
		assertEquals("Prefix" + (5 % 2), String.valueOf(uut.getPrefix()));
		assertEquals("0", String.valueOf(query.getPersonID()));
		assertEquals("Prefix" + (5 % 2), String.valueOf(uut.getPrefix()));
	}

}


