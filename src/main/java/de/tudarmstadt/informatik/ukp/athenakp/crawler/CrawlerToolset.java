package de.tudarmstadt.informatik.ukp.athenakp.crawler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

import static java.lang.Integer.parseInt;

/**
 * A class with methods that might be useful across a variety of conferences / scraping tasks
 * currently mostly focused on converting Strings to LocalDates and LocalTimes
 */
class CrawlerToolset {

	/**Converts a time in String format to a LocalDate instance
	 * @param timeString time in String format (e.g. 9:00 or 09:00)
	 * @return corresponding LocalTime instance
	 * @throws IndexOutOfBoundsException if the String was not in the expected format
	 */
	 LocalTime acl2018ConvertStringToTime(String timeString) throws IndexOutOfBoundsException{
		String[] hoursAndMinutes = timeString.split(":", 2);
		int startHours = parseInt(hoursAndMinutes[0]);
		int startMinutes = parseInt(hoursAndMinutes[1]);
		return LocalTime.of(startHours, startMinutes);
	}

	/**
	 * A method which constructs an Array holding the beginning and end of a conference or an event
	 * @param dateString Date (day - day + months + year) in String format e.g. "15-20 July 2018")
	 * @return an Array of LocalDates with two entries, the beginning and end of the date range
	 * @throws IndexOutOfBoundsException if the String was not in the proper format
	 */
	LocalDate[] acl2018ConvertStringToDateRange(String dateString) throws IndexOutOfBoundsException{
	 	String[] daysMonthsYearAndLocation = dateString.split(" ");
	 	String daysRange = daysMonthsYearAndLocation[0];
	 	String[] startAndEndDay = daysRange.split("-", 2);
		LocalDate[] dateRange = new LocalDate[2];
		dateRange[0] = stringToLocalDate(startAndEndDay[0], daysMonthsYearAndLocation[1], daysMonthsYearAndLocation[2]);
		dateRange[1] = stringToLocalDate(startAndEndDay[1], daysMonthsYearAndLocation[1], daysMonthsYearAndLocation[2]);
	 	return dateRange;
	}

	/**
	 * A method which converts day,month and year strings into a LocalDate
	 * @param dayDescription day as String "11"
	 * @param monthDescription month as String "July"
	 * @param yearDescription year as String "2018"
	 * @return LocalDate corresponding to the parameters
	 */
	LocalDate stringToLocalDate(String dayDescription, String monthDescription, String yearDescription){
		int day = parseInt(dayDescription);
		int year = parseInt(yearDescription);
		Month monthObject = Month.valueOf(monthDescription.toUpperCase());
		return LocalDate.of(year, monthObject,day);
	}
}
