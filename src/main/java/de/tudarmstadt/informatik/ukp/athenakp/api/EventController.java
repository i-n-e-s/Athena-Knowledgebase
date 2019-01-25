package de.tudarmstadt.informatik.ukp.athenakp.api;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.EventCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.jpa.EventJPAAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Event;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.EventCategory;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Session;

/**
 * Serves as a REST API definition for accessing events. Any mapping defined by a
 * method in this class has to be prepended with "/events" since this class is annotated
 * with a RequestMapping.
 * @author Daniel Lehmann
 */
@RestController
@RequestMapping("/events")
public class EventController {
	private final EventCommonAccess access = new EventJPAAccess();

	/**
	 * @return All events in the database
	 */
	@RequestMapping("") //default
	public List<Event> getAllEvents() {
		return access.get();
	}

	/**
	 * @param value The event ID
	 * @return The event with the specified id, if existing
	 */
	@RequestMapping("/byEventID/{value}")
	public List<Event> byEventID(@PathVariable("value")Long value) {
		return access.getById(value);
	}

	/**
	 * @param value The event ID
	 * @return All sessions of the event with the given ID, if existing
	 */
	@RequestMapping("/byEventID/{value}/getSessions")
	public Set<Session> getSessions(@PathVariable("value")Long value) {
		List<Event> events = byEventID(value);

		if(events.size() > 0)
			return events.get(0).getSessions();
		else return null;
	}

	/**
	 * @param value The event's conference's name
	 * @return All events held at the conference with the given name
	 */
	@RequestMapping("/byConference/{value}")
	public List<Event> byConference(@PathVariable("value")String value) {
		return access.getByConference(value);
	}

	/**
	 * @param year The year in which the event started
	 * @param month The month in which the event started
	 * @param day The day on which the event started
	 * @param hour The hour in which the event started
	 * @param minute The minute in which the event started
	 * @return The events with the specified start time, if existing
	 */
	@RequestMapping("/byStartTime/{year}/{month}/{day}/{hour}/{minute}")
	public List<Event> byStartTime(@PathVariable("year")Integer year, @PathVariable("month")Integer month, @PathVariable("day")Integer day, @PathVariable("hour")Integer hour, @PathVariable("minute")Integer minute) {
		return access.getByStartTime(year, month, day, hour, minute);
	}

	/**
	 * @param year The year in which the event ended
	 * @param month The month in which the event ended
	 * @param day The day on which the event ended
	 * @param hour The hour in which the event ended
	 * @param minute The minute in which the event ended
	 * @return The events with the specified start time, if existing
	 */
	@RequestMapping("/byEndTime/{year}/{month}/{day}/{hour}/{minute}")
	public List<Event> byEndTime(@PathVariable("year")Integer year, @PathVariable("month")Integer month, @PathVariable("day")Integer day, @PathVariable("hour")Integer hour, @PathVariable("minute")Integer minute) {
		return access.getByEndTime(year, month, day, hour, minute);
	}

	/**
	 * @param value The event's place
	 * @return All events with the given place
	 */
	@RequestMapping("/byPlace/{value}")
	public List<Event> byPlace(@PathVariable("value")String value) {
		return access.getByPlace(value);
	}

	/**
	 * @param value The event's title
	 * @return All events with the given title
	 */
	@RequestMapping("/byTitle/{value}")
	public List<Event> byTitle(@PathVariable("value")String value) {
		return access.getByTitle(value);
	}

	/**
	 * @param value The event's description
	 * @return All events with the given description
	 */
	@RequestMapping("/byDescription/{value]")
	public List<Event> byDescription(@PathVariable("value")String value) {
		return access.getByDescription(value);
	}

	/**
	 * @param value The event's category
	 * @return All events with the given category
	 */
	@RequestMapping("/byCategory/{value}")
	public List<Event> byCategory(@PathVariable("value")Integer value) {
		if(value >= EventCategory.values().length) //sanitizing user input
			return null;
		else return access.getByCategory(EventCategory.values()[value]);
	}
}
