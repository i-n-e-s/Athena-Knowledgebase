package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

//TODO: comment in when testbench is merged
public class SessionJPAAccessIntegrationTest {
	//	static Testdatabase testDB;
	//	static SessionJPAAccess uut;
	//	static Session testValue;
	//	static Subsession testSubsession1;
	//	static Subsession testSubsession2;
	//	static ConfigurableApplicationContext ctx;
	//
	//	@BeforeClass
	//	public static void setUpDatabase() {
	//		ctx = SpringApplication.run(Testdatabase.class, "");
	//		testDB = new Testdatabase();
	//		uut = new SessionJPAAccess();
	//		testDB.createDB();
	//	}
	//
	//	@AfterClass
	//	public static void shutdownDatabase() {
	//		ctx.close();
	//	}
	//
	//	public void resetValues() {
	//		java.util.Set<Subsession> subsessions = new HashSet<Subsession>();
	//		testValue = new Session();
	//		testSubsession1 = new Subsession();
	//		testSubsession1.setTitle("TestTitleTest1");
	//		testSubsession2 = new Subsession();
	//		testSubsession2.setTitle("TestTitleTest2");
	//		testValue.setTitle("TestTitleTest");
	//		testValue.setDescription("TestDescriptionTest");
	//		testValue.setPlace("TestPlaceTest");
	//		subsessions.add(testSubsession1);
	//		subsessions.add(testSubsession2);
	//		testValue.setSubsessions(subsessions);
	//	}
	//
	//	@Before
	//	public void resetDB() {
	//		resetValues();
	//	}
	//
	//	@Test
	//	public void getTest() {
	//		testDB.setDefaultParameters();
	//		testDB.createDB();
	//		List<Session> returnValues = uut.get();
	//		if(returnValues.size() != testDB.getConferenceQuantity()) fail("TestDatabase is not the expected size");
	//	}
	//
	//	@Test
	//	public void getByTitleTest() {
	//		List<Session> returnValue = uut.getByTitle("Title5");
	//		if(returnValue.size() == 0) fail("return of existing Database is empty");
	//		if(returnValue.size() > 1) fail("more than one returnValue ");
	//		assertEquals("Title5", returnValue.get(0).getTitle());
	//	}
	//
	//	@Test
	//	public void getByTitleInvalidTitleTest() {
	//		assertTrue(uut.getByTitle("InvalidPlace").size() == 0);
	//	}
	//
	//	@Test
	//	public void getByDescriptionTest() {
	//		List<Session> returnValue = uut.getByDescription("Description10");
	//		if(returnValue.size() == 0) fail("return of existing Database is empty");
	//		if(returnValue.size() > 1) fail("more than one returnValue ");
	//		assertEquals("Description10", returnValue.get(0).getDescription());
	//	}
	//
	//	@Test
	//	public void getByDescriptionInvalidDescriptionTest() {
	//		assertTrue(uut.getByDescription("InvalidDescription").size() == 0);
	//	}
	//
	//	@Test
	//	public void getByPlaceTest() {
	//		List<Session> returnValue = uut.getByPlace("Place2");
	//		if(returnValue.size() == 0) fail("return of existing Database is empty");
	//		if(returnValue.size() > 1) fail("more than one returnValue ");
	//		assertEquals("Place2", returnValue.get(0).getPlace());
	//	}
	//
	//	@Test
	//	public void getByPlaceInvalidPlaceTest() {
	//		assertTrue(uut.getByPlace("InvalidPlace").size() == 0);
	//	}
	//
	//	@Test
	//	public void addAndDeleteTest() {
	//		uut.add(testValue);
	//		List<Session> returnValue = uut.getById(testValue.getId());
	//		if(returnValue.size() == 0) fail("return of existing Database is empty");
	//		if(returnValue.size() > 1) fail("more than one return value");
	//		assertEquals(testValue.getTitle(), returnValue.get(0).getTitle());
	//		assertEquals(testValue.getDescription(), returnValue.get(0).getDescription());
	//		assertEquals(testValue.getPlace(), returnValue.get(0).getPlace());
	//		int correctSubsessions = 2; //will be 0 if both test sessions have been found
	//		for(Subsession subsessionReturn : returnValue.get(0).getSubsessions()) {
	//			for(Subsession subsessionTestVal : testValue.getSubsessions()) {
	//				if(subsessionReturn.getTitle().equals(subsessionTestVal.getTitle()))
	//					correctSubsessions--;
	//			}
	//		}
	//		assertEquals(0, correctSubsessions);
	//		uut.delete(testValue);
	//		assertTrue(uut.getById(testValue.getId()).size() == 0);
	//		testDB.createDB();//If delete is broken don't pollute DB
	//	}
	//
	//	@Test
	//	public void updateTest() {
	//		uut.add(testValue);
	//		testValue.setTitle("UpdatedTitle");
	//		uut.update(testValue);
	//		List<Session> returnValues = uut.getById(testValue.getId());
	//		if(returnValues.size() == 0) fail("return is empty");
	//		if(returnValues.size() > 1) fail("more than one return value");
	//		assertEquals("UpdatedTitle", returnValues.get(0).getTitle());
	//		testDB.createDB();//Don't pollute the Database
	//	}
}
