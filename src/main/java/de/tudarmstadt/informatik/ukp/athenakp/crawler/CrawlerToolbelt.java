package de.tudarmstadt.informatik.ukp.athenakp.crawler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

import static java.lang.Integer.parseInt;

class CrawlerToolbelt {

	 LocalTime acl2018ConvertStringToTime(String timeString) throws IndexOutOfBoundsException{
		String[] hoursAndMinutes = timeString.split(":", 2);
		int startHours = parseInt(hoursAndMinutes[0]);
		int startMinutes = parseInt(hoursAndMinutes[1]);
		return LocalTime.of(startHours, startMinutes);
	}

	LocalDate[] acl2018ConvertStringToDateRange(String dateString) throws IndexOutOfBoundsException{
	 	String[] daysMonthsYearAndLocation = dateString.split(" ");
	 	String daysRange = daysMonthsYearAndLocation[0];
	 	String[] startAndEndDay = daysRange.split("-", 2);
		LocalDate[] dateRange = new LocalDate[2];
		dateRange[0] = stringToLocalDate(startAndEndDay[0], daysMonthsYearAndLocation[1], daysMonthsYearAndLocation[2]);
		dateRange[1] = stringToLocalDate(startAndEndDay[1], daysMonthsYearAndLocation[1], daysMonthsYearAndLocation[2]);
	 	return dateRange;
	}

	LocalDate stringToLocalDate(String dayDescription, String monthDescription, String yearDescription){
		int day = parseInt(dayDescription);
		int year = parseInt(yearDescription);
		Month monthObject = Month.valueOf(monthDescription.toUpperCase());
		return LocalDate.of(year, monthObject,day);
	}
}
