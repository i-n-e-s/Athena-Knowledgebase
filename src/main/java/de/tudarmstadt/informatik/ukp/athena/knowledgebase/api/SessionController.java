package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.SessionCommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.SessionJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Session;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.SessionCategory;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.SessionPart;

/**
 * Serves as a REST API definition for accessing sessions. Any mapping defined by a
 * method in this class has to be prepended with "/sessions" since this class is annotated
 * with a RequestMapping.
 * @author Daniel Lehmann
 */
@RestController
@RequestMapping("/sessions")
public class SessionController {
	private final SessionCommonAccess access = new SessionJPAAccess();

	/**
	 * @return All sessions in the database
	 */
	@RequestMapping("") //default
	public List<Session> getAllSessions() {
		return access.get();
	}

	/**
	 * @param value The session ID
	 * @return The session with the specified id, if existing
	 */
	@RequestMapping("/bySessionID/{value}")
	public List<Session> bySessionID(@PathVariable("value")Long value) {
		return access.getBySessionId(value);
	}

	/**
	 * @param value The session ID
	 * @return All session parts of the session with the given ID, if existing
	 */
	@RequestMapping("/bySessionID/{value}/getSessionParts")
	public Set<SessionPart> getSessionParts(@PathVariable("value")Long value) {
		List<Session> session = bySessionID(value);

		if(session.size() > 0)
			return session.get(0).getSessionParts();
		else return null;
	}

	/**
	 * @param year The year in which the session started
	 * @param month The month in which the session started
	 * @param day The day on which the session started
	 * @param hour The hour in which the session started
	 * @param minute The minute in which the session started
	 * @return The sessions with the specified start time, if existing
	 */
	@RequestMapping("/byStartTime/{year}/{month}/{day}/{hour}/{minute}")
	public List<Session> byStartTime(@PathVariable("year")Integer year, @PathVariable("month")Integer month, @PathVariable("day")Integer day, @PathVariable("hour")Integer hour, @PathVariable("minute")Integer minute) {
		return access.getByStartTime(year, month, day, hour, minute);
	}

	/**
	 * @param year The year in which the session ended
	 * @param month The month in which the session ended
	 * @param day The day on which the session ended
	 * @param hour The hour in which the session ended
	 * @param minute The minute in which the session ended
	 * @return The sessions with the specified start time, if existing
	 */
	@RequestMapping("/byEndTime/{year}/{month}/{day}/{hour}/{minute}")
	public List<Session> byEndTime(@PathVariable("year")Integer year, @PathVariable("month")Integer month, @PathVariable("day")Integer day, @PathVariable("hour")Integer hour, @PathVariable("minute")Integer minute) {
		return access.getByEndTime(year, month, day, hour, minute);
	}

	/**
	 * @param value The session's place
	 * @return All sessions with the given place
	 */
	@RequestMapping("/byPlace/{value}")
	public List<Session> byPlace(@PathVariable("value")String value) {
		return access.getByPlace(value);
	}

	/**
	 * @param value The session's title
	 * @return All sessions with the given title
	 */
	@RequestMapping("/byTitle/{value}")
	public List<Session> byTitle(@PathVariable("value")String value) {
		return access.getByTitle(value);
	}

	/**
	 * @param value The session's description
	 * @return All session with the given description
	 */
	@RequestMapping("/byDescription/{value]")
	public List<Session> byDescription(@PathVariable("value")String value) {
		return access.getByDescription(value);
	}

	/**
	 * @param value The session's category
	 * @return All sessions with the given category
	 */
	@RequestMapping("/byCategory/{value}")
	public List<Session> byCategory(@PathVariable("value")Integer value) {
		if(value < 0 || value >= SessionCategory.values().length) //sanitizing user input
			return null;
		else return access.getByCategory(SessionCategory.values()[value]);
	}
}
