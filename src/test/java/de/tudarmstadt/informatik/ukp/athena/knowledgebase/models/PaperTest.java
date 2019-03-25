package de.tudarmstadt.informatik.ukp.athena.knowledgebase.models;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PersistenceManager;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.JPATestdatabase;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@SuppressWarnings("javadoc")
public class PaperTest {

    static JPATestdatabase testDB;

    static ConfigurableApplicationContext ctx;

    @BeforeClass
    public static void setUpDatabase() {
        ctx = SpringApplication.run(JPATestdatabase.class,"");
        testDB = new JPATestdatabase();
        testDB.createDB();
    }

    @AfterClass
    public static void shutdownDatabase() {
        ctx.close();
    }

    @Before
    public void resetDB() {
        testDB.createDB(); //Performance hungry if done before every test
    }


    @Test
    public void paperFindOrCreateTest() {
       Paper query = new Paper();
        query.setTitle("Title5");
        assertEquals("0", String.valueOf(query.getPaperID()));
        Paper uut = Paper.findOrCreate(query);
        assertEquals("Ant5", String.valueOf(uut.getAnthology()));
        assertEquals("0", String.valueOf(query.getPaperID()));
    }

    @Test
    public void paperFindOrCreateStrStrTest() {
        Paper uut = Paper.findOrCreate(null, "Title5");
        assertEquals("Ant5", String.valueOf(uut.getAnthology()));

        uut = Paper.findOrCreate("44962368", null);
        assertEquals("Ant6", String.valueOf(uut.getAnthology()));
        uut = Paper.findOrCreate("27393377", "Title17");
        assertEquals("Ant17", String.valueOf(uut.getAnthology()));
        uut = Paper.findOrCreate("34887105", "Title17");
        assertEquals("Ant18", String.valueOf(uut.getAnthology()));
        uut = Paper.findOrCreate(null, null);
        assertNull(uut.getAnthology());
    }
}
