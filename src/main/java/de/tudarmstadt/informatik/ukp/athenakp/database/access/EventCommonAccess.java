package de.tudarmstadt.informatik.ukp.athenakp.database.access;

import java.util.List;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.Event;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.EventCategory;

/**
 * @author Daniel Lehmann
 */
public interface EventCommonAccess extends CommonAccess<Event> {
	/**
	 * Get all events with specified event id.
	 * @param id The event's id.
	 * @return A List of all events with the specified event id.
	 */
	public List<Event> getByEventId(Long id);

	/**
	 * Get all events with specified conference.
	 * @param conferenceName The event's conference's name.
	 * @return A List of all events with the specified conference.
	 */
	public List<Event> getByConferenceName(String conferenceName);

	/**
	 * Get all events with specified start time.
	 * @param year The event's start year.
	 * @param month The event's start month.
	 * @param day The event's start day.
	 * @param hour The event's start hour.
	 * @param minute The event's start minute.
	 * @return A List of all events with the specified end time.
	 */
	public List<Event> getByStartTime(Integer year, Integer month, Integer day, Integer hour, Integer minute);

	/**
	 * Get all events with specified end time.
	 * @param year The event's start year.
	 * @param month The event's start month.
	 * @param day The event's start day.
	 * @param hour The event's end hour.
	 * @param minute The event's end minute.
	 * @return A List of all events with the specified end time.
	 */
	public List<Event> getByEndTime(Integer year, Integer month, Integer day, Integer hour, Integer minute);

	/**
	 * Get all events with specified place.
	 * @param place The event's place.
	 * @return A List of all events with the specified place.
	 */
	public List<Event> getByPlace(String place);

	/**
	 * Get all events with specified title.
	 * @param title The event's title.
	 * @return A List of all events with the specified title.
	 */
	public List<Event> getByTitle(String title);

	/**
	 * Get all events with specified description.
	 * @param description The event's description.
	 * @return A List of all events with the specified description.
	 */
	public List<Event> getByDescription(String description);

	/**
	 * Get all events with specified category.
	 * @param category The event's category.
	 * @return A List of all events with the specified category.
	 */
	public List<Event> getByCategory(EventCategory category);
}
