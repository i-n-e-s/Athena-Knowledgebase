package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.Test;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Event;

public class ACL18WorkshopParserTest {
	@Test
	public void testExtractBeginEnd() {
		String[] input = {"12:23", "15:07"};
		LocalTime[] output = {
				LocalTime.of(12, 23),
				LocalTime.of(15, 07)
		};

		assertArrayEquals(output, ACL18WorkshopParser.extractBeginEnd(input));
	}

	@Test
	public void testSetEventBeginEnd() {
		LocalTime[] input = {
				LocalTime.of(12, 23),
				LocalTime.of(15, 07)
		};
		LocalDate beginDate = LocalDate.of(2018, 7, 7);
		LocalDate endDate = LocalDate.of(2018, 7, 8);
		Event session = new Event();

		ACL18WorkshopParser.setSessionBeginEnd(input, beginDate, endDate, session);
		assertEquals(beginDate, session.getBegin().toLocalDate());
		assertEquals(endDate, session.getEnd().toLocalDate());
		assertEquals(input[0], session.getBegin().toLocalTime());
		assertEquals(input[1], session.getEnd().toLocalTime());
	}
}
