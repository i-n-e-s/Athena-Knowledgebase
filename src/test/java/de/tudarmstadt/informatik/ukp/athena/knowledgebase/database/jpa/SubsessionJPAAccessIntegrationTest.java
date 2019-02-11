package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

//TODO: comment in when testbench is merged
public class SubsessionJPAAccessIntegrationTest {
	//	static Testdatabase testDB;
	//	static SubsessionJPAAccess uut;
	//	static Subsession testValue;
	//	static ConfigurableApplicationContext ctx;
	//
	//	@BeforeClass
	//	public static void setUpDatabase() {
	//		ctx = SpringApplication.run(Testdatabase.class, "");
	//		testDB = new Testdatabase();
	//		uut = new SubsessionJPAAccess();
	//		testDB.createDB();
	//	}
	//
	//	@AfterClass
	//	public static void shutdownDatabase() {
	//		ctx.close();
	//	}
	//
	//	public void resetValues() {
	//		testValue = new Subsession();
	//		testValue.setTitle("TestTitleTest");
	//		testValue.setDescription("TestDescriptionTest");
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
	//		List<Subsession> returnValues = uut.get();
	//		if(returnValues.size() != testDB.getConferenceQuantity()) fail("TestDatabase is not the expected size");
	//	}
	//
	//	@Test
	//	public void getByStartTimeTest() {
	//		List<Subsession> returnValue = uut.getByStartTime(2018, (9%12)+1, (9%28)+1, 9%24, 9%60);
	//		if(returnValue.size() == 0) fail("return of existing Database is empty");
	//		if(returnValue.size() > 1) fail("more than one returnValue ");
	//		assertEquals(LocalDateTime.of(2018, (9%12)+1, (9%28)+1, 9%24, 9%60), returnValue.get(0).getBegin());
	//	}
	//
	//	@Test
	//	public void getByStartTimeInvalidStartTimeTest() {
	//		assertTrue(uut.getByStartTime(1, 1, 1, 1, 1).size() == 0);
	//	}
	//
	//	@Test
	//	public void getByEndTimeTest() {
	//		List<Subsession> returnValue = uut.getByEndTime(2018, (11%12)+1, (11%28)+1, (11%24)+1, 11%60);
	//		if(returnValue.size() == 0) fail("return of existing Database is empty");
	//		if(returnValue.size() > 1) fail("more than one returnValue ");
	//		assertEquals(LocalDateTime.of(2018, (11%12)+1, (11%28)+1, (11%24)+1, 11%60), returnValue.get(0).getEnd());
	//	}
	//
	//	@Test
	//	public void getByEndTimeInvalidStartTimeTest() {
	//		assertTrue(uut.getByEndTime(1, 1, 1, 1, 1).size() == 0);
	//	}
	//
	//	@Test
	//	public void getByTitleTest() {
	//		List<Subsession> returnValue = uut.getByTitle("Title5");
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
	//		List<Subsession> returnValue = uut.getByDescription("Description10");
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
	//	public void addAndDeleteTest() {
	//		uut.add(testValue);
	//		List<Subsession> returnValue = uut.getById(testValue.getId());
	//		if(returnValue.size() == 0) fail("return of existing Database is empty");
	//		if(returnValue.size() > 1) fail("more than one return value");
	//		assertEquals(testValue.getTitle(), returnValue.get(0).getTitle());
	//		assertEquals(testValue.getDescription(), returnValue.get(0).getDescription());
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
	//		List<Subsession> returnValues = uut.getById(testValue.getId());
	//		if(returnValues.size() == 0) fail("return is empty");
	//		if(returnValues.size() > 1) fail("more than one return value");
	//		assertEquals("UpdatedTitle", returnValues.get(0).getTitle());
	//		testDB.createDB();//Don't pollute the Database
	//	}
}
