package de.tudarmstadt.informatik.ukp.athenakp.crawler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.Event;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.EventCategory;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.ScheduleEntry;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Session;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Workshop;

/**
 * Parses the workshops of ACL2018 and their schedules if they are on the page that ACL links to
 */
public class ACL18WorkshopParser {
	private static final String workshopPage = "https://acl2018.org/workshops/";

	/**
	 * Parses ACL 2018's workshop schedule.
	 * Some of this is hardcoded because why not
	 * @param result The resulting arraylist with the complete workshop data
	 */
	public static ArrayList<ScheduleEntry> parseWorkshops() {
		ArrayList<ScheduleEntry> result = new ArrayList<>();

		try {
			Document doc = Jsoup.connect(workshopPage).get();
			Elements content = doc.select(".post-content");
			Elements days = content.select("ul");

			for(int i = 0; i < days.size(); i++) {
				Element day = days.get(i);
				Elements workshops = day.select("li");

				for(Element workshopEl : workshops) {
					Workshop workshop = new Workshop();
					String[] dayMonth = content.select("h4").get(i).text().split(" ", 2)[1].split(" ");
					String[] complTitleRoom = workshopEl.text().split(": Room");
					String wsLink = workshopEl.selectFirst("a").attr("href");
					String[] titleAbbr = complTitleRoom[0].split("\\(");

					workshop.setConference("ACL 2018");
					workshop.setDate(LocalDate.of(2018, CrawlerToolset.getMonthIndex(dayMonth[1]), Integer.parseInt(dayMonth[0])));
					workshop.setBegin(LocalTime.of(9, 0));
					workshop.setEnd(LocalTime.of(17, 0)); //assume 5pm, because the schedule table is not 100% proportional
					workshop.setTitle(titleAbbr[0].trim());
					workshop.setPlace("Room" + complTitleRoom[1]);
					workshop.setAbbreviation(titleAbbr[1].replace(")", "").trim());

					//not every workshop has a schedule and each one has a different layout - this switch is there to select them
					//the previous link to BioNLP is now linking to the 2019 edition of the workshop :(
					//the CALCS workshop has a schedule, but it's in pdf form. some hours went by trying to find a proper library
					//	for pdf reading, but to no avail
					switch(workshop.getAbbreviation()) {
						case "MSR": parseMSR(Jsoup.connect(wsLink).get(), workshop); break;
						case "MRQA": parseMRQA(Jsoup.connect(wsLink).get(), workshop); break;
						case "RELNLP": parseRELNLP(Jsoup.connect(wsLink).get(), workshop); break;
						case "ECONLP": parseECONLP(Jsoup.connect("https://julielab.de/econlp/2018/").get(), workshop); break; //direct link because wsLink is a redirect in this case
						case "MML_Challenge": parseMML(Jsoup.connect(wsLink).get(), workshop); break;
						case "SocialNLP": parseSocialNLP(Jsoup.connect(wsLink).get(), workshop); break;
						case "NLPOSS": parseNLPOSS(Jsoup.connect(wsLink).get(), workshop); break;
					}

					result.add(workshop);
				}
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Parses the MSR workshop's schedule
	 * @param doc The document containing the schedule
	 * @param workshop The workshop instance to add the schedule to
	 */
	//the method can't be written more concise because it parses one single workshop's schedule which can't be broken up in multiple parts
	private static void parseMSR(Document doc, Workshop workshop) {
		Elements els = doc.select("#program_table > tbody > tr");
		Event previousEvent = null;

		for(Element el : els) {
			Event event = new Event();
			Elements td = el.select("td");
			String[] timeSplit = td.get(0).text().split(":");
			LocalTime time;

			if(timeSplit[0].contains("h")) //there's a single goddamn h in the closing event
				timeSplit[0] = timeSplit[0].replace("h", "");

			time = LocalTime.of(Integer.parseInt(timeSplit[0]), Integer.parseInt(timeSplit[1]));
			event.setBegin(time);
			event.setDate(workshop.getDate());
			event.setConference(workshop.getConference());

			//i'm assuming that the closing event is the end of the workshop, thus it does not get added
			if(previousEvent != null) {
				previousEvent.setEnd(time);
				workshop.addEvent(previousEvent);
				workshop.setEnd(previousEvent.getEnd());
			}

			if(el.hasClass("program_break")) {
				event.setTitle(td.get(1).text().trim());
				event.setCategory(EventCategory.BREAK);
			}
			else {
				event.setTitle(td.get(1).select("b").text().trim());

				switch(event.getTitle().toLowerCase()) {
					case "oral presentation":
						event.setDescription(td.get(1).text().replace(event.getTitle(), ""));
						event.setCategory(EventCategory.PRESENTATION);
						break;
					case "oral presentations": case "poster session":
						String html = td.get(1).html().replace("<b> " + event.getTitle() +" </b>", "").trim();
						String[] sections = html.split("<i>");

						for(String section : sections) {
							Session session = new Session();
							String[] titleDesc = section.split("</i>");

							if(titleDesc.length < 2) //doesn't contain an entry
								continue;

							session.setPlace(workshop.getPlace());
							session.setTitle(titleDesc[0].trim());
							session.setDescription(titleDesc[1].split("<br>")[1].trim());
							event.addSession(session);

							switch(event.getTitle().toLowerCase()) {
								case "oral presentations": event.setCategory(EventCategory.PRESENTATION); break;
								case "poster session": event.setCategory(EventCategory.SESSION); break;
							}
						}

						break;
					case "panel/discussions": event.setCategory(EventCategory.TALK); break;
				}
			}

			if(event.getCategory() == null) {
				if(event.getTitle().contains("Opening"))
					event.setCategory(EventCategory.WELCOME);
				else if(event.getTitle().contains("Talk"))
					event.setCategory(EventCategory.TALK);
			}

			previousEvent = event;
		}
	}

	/**
	 * Parses the MRQA workshop's schedule
	 * @param doc The document containing the schedule
	 * @param workshop The workshop instance to add the schedule to
	 */
	//the method can't be written more concise because it parses one single workshop's schedule which can't be broken up in multiple parts
	private static void parseMRQA(Document doc, Workshop workshop) {
		Elements blog = doc.selectFirst(".blog-main").children();
		boolean programFound = false;

		for(Element el : blog) {
			if(!programFound && el.id().equals("program")) {
				programFound = true;
				continue;
			}
			else if(programFound && el.id().equals("important-dates")) //end of program
				break;
			else if(!programFound)
				continue;

			Event previousEvent = null;
			String[] br = el.html().split("<br>");

			for(String line : br) {
				if(line.contains("|")) {
					Event event = new Event();
					String[] split = line.split("\\|"); //splitting by | only basically gets the char array as a string array

					setEventBeginEnd(extractBeginEnd(split[0].trim().split("–")), event); //NOT A HYPHEN!!! IT'S AN 'EN DASH'
					event.setConference(workshop.getConference());
					event.setPlace(workshop.getPlace());
					event.setTitle(split[1].trim());

					if(event.getTitle().contains("<em>")) {
						String previousTitle = event.getTitle();
						event.setTitle(previousTitle.split(":")[0]);
						event.setDescription(previousTitle.split("<em>")[1].replace("</em>", ""));
					}

					previousEvent = event;
				}
				else if(previousEvent != null) {
					previousEvent.setDescription(previousEvent.getTitle());

					if(!previousEvent.getDescription().equals("Panel discussion")) //panel discussion doesn't have the same -
						previousEvent.setTitle(line.split("- ")[1].split("</b>")[0].trim());
				}

				if(previousEvent != null) {
					String title = previousEvent.getTitle();

					if(title.contains("Opening"))
						previousEvent.setCategory(EventCategory.WELCOME);
					else if(title.contains("coffee") || title.contains("Lunch"))
						previousEvent.setCategory(EventCategory.BREAK);
					else if(title.contains("Poster"))
						previousEvent.setCategory(EventCategory.SESSION);
					else
						previousEvent.setCategory(EventCategory.TALK);

					if(previousEvent.getDescription() != null && previousEvent.getDescription().contains("<a href="))
						previousEvent.setDescription(previousEvent.getDescription().split(">")[1].split("<")[0]);

					if(previousEvent.getDescription() != null && previousEvent.getDescription().equals("Panel discussion") && !line.contains("discussion")) { //get panel discussion description
						String[] split;

						line = line.replace("<b>", "").replace("</a>", "").replace("</b>", "");
						split = line.split("\">");
						previousEvent.setTitle(previousEvent.getDescription());
						previousEvent.setDescription(split[1].split("<")[0] + split[2]);
					}

					workshop.addEvent(previousEvent);
					workshop.setEnd(previousEvent.getEnd());
				}
			}
		}
	}

	/**
	 * Parses the RELNLP workshop's schedule
	 * @param doc The document containing the schedule
	 * @param workshop The workshop instance to add the schedule to
	 */
	//the method can't be written more concise because it parses one single workshop's schedule which can't be broken up in multiple parts
	private static void parseRELNLP(Document doc, Workshop workshop) {
		Elements els = doc.select(".tyJCtd").get(1).children();
		boolean programFound = false;
		Event previousEvent = null;

		for(Element el : els) {
			if(!programFound && el.id().equals("h.p__bAUwIOcmLuf")){
				programFound = true;
				continue;
			}
			else if(programFound && el.id().equals("h.p_LOk0X7QqxUPR")) //end of program
				break;
			else if(!programFound)
				continue;

			if(previousEvent != null) {
				boolean skip = false;

				if(!el.text().contains("--")) {
					skip = true;
					previousEvent.setDescription(el.text());
				}

				if(previousEvent.getTitle().contains("Opening"))
					previousEvent.setCategory(EventCategory.WELCOME);
				else if(previousEvent.getTitle().contains("Talk"))
					previousEvent.setCategory(EventCategory.TALK);
				else if(previousEvent.getTitle().contains("Break"))
					previousEvent.setCategory(EventCategory.BREAK);
				else if(previousEvent.getTitle().contains("session"))
					previousEvent.setCategory(EventCategory.SESSION);

				workshop.addEvent(previousEvent);
				workshop.setEnd(previousEvent.getEnd());

				if(skip)
					continue;
			}

			Event event = new Event();
			//this time extraction code is used often, but there is a lot of variation so no util method
			String info = el.html().split("/strong>")[1];

			setEventBeginEnd(extractBeginEnd(el.html().split("strong>")[1].split("<")[0].trim().split("--")), event);
			event.setConference(workshop.getConference());
			event.setPlace(workshop.getPlace());

			if(info.contains(":") && !info.contains("Invited")) {
				String[] infoSplit = info.split(":");

				event.setTitle(infoSplit[0]);
				event.setDescription(infoSplit[1].replace("<em>", "").replace("</em>", ""));
			}
			else
				event.setTitle(info);

			previousEvent = event;
		}
	}

	/**
	 * Parses the ECONLP workshop's schedule
	 * @param doc The document containing the schedule
	 * @param workshop The workshop instance to add the schedule to
	 */
	//the method can't be written more concise because it parses one single workshop's schedule which can't be broken up in multiple parts
	private static void parseECONLP(Document doc, Workshop workshop) {
		Elements els = doc.selectFirst(".wrapper").children().get(2).children();
		boolean programFound = false;
		Event event = null;

		for(Element el : els) {
			if(!programFound && el.id().equals("workshop-programme")){
				programFound = true;
				continue;
			}
			else if(programFound && el.id().equals("comitees")) //end of program
				break;
			else if(!programFound)
				continue;

			if(event == null) {
				event = new Event();
				setEventBeginEnd(extractBeginEnd(el.html().split("–")), event); //NOT A HYPHEN!!! IT'S AN 'EN DASH'
				event.setConference(workshop.getConference());
				event.setPlace(workshop.getPlace());
			}
			else {
				if(el.html().contains("<br>")) {
					String[] split = el.html().split("<br>");

					event.setTitle(split[0].replace("<strong>", "").replace("</strong>", ""));
					event.setDescription(split[1]);
					event.setCategory(EventCategory.PRESENTATION);
				}
				else {
					event.setTitle(el.text());

					if(event.getTitle().contains("Discussion"))
						event.setCategory(EventCategory.TALK);
					else if(event.getTitle().contains("Session")) {
						String[] titleSplit = event.getTitle().split("-");

						event.setDescription(titleSplit[1].trim());
						event.setTitle(titleSplit[0].trim());
						event.setCategory(EventCategory.SESSION);
					}
					else
						event.setCategory(EventCategory.BREAK);
				}

				workshop.addEvent(event);
				workshop.setEnd(event.getEnd());
				event = null;
			}
		}
	}

	/**
	 * Parses the MML workshop's schedule
	 * @param doc The document containing the schedule
	 * @param workshop The workshop instance to add the schedule to
	 */
	private static void parseMML(Document doc, Workshop workshop) {
		Elements els = doc.selectFirst("#one_col > div").children();
		boolean programFound = false;

		for(Element el : els) {
			if(!programFound && el.is("h2") && el.text().equals("Schedule")){
				programFound = true;
				continue;
			}
			else if(programFound && el.is("h2") && el.text().equals("Important Dates")) //end of program
				break;
			else if(!programFound)
				continue;

			String[] time = el.html().split(">")[1].split("<")[0].split("-");

			if(time.length < 2) //schedule ends
				break;

			Event event = new Event();

			setEventBeginEnd(extractBeginEnd(time), event);
			event.setConference(workshop.getConference());
			event.setPlace(workshop.getPlace());
			event.setTitle(el.html().split("</strong>")[1].replace("<em>", "").replace("</em>", "").replace("&nbsp;", " "));

			if(event.getTitle().toLowerCase().contains("opening"))
				event.setCategory(EventCategory.WELCOME);
			else if(event.getTitle().toLowerCase().contains("keynote"))
				event.setCategory(EventCategory.PRESENTATION);
			else if(event.getTitle().toLowerCase().contains("break"))
				event.setCategory(EventCategory.BREAK);
			else if(event.getTitle().toLowerCase().contains("results"))
				event.setCategory(EventCategory.CEREMONY);
			else
				event.setCategory(EventCategory.TALK);

			workshop.addEvent(event);
			workshop.setEnd(event.getEnd());
		}
	}

	/**
	 * Parses the SocialNLP workshop's schedule
	 * @param doc The document containing the schedule
	 * @param workshop The workshop instance to add the schedule to
	 */
	//the method can't be written more concise because it parses one single workshop's schedule which can't be broken up in multiple parts
	private static void parseSocialNLP(Document doc, Workshop workshop) {
		Elements els = doc.selectFirst("#sites-canvas-main-content > table > tbody > tr > td > div > div > table:nth-child(37) > tbody").children();

		for(int i = 0; i < els.size(); i++) {
			Element el = els.get(i);
			Elements td = el.select("td");

			if(td.size() == 0) //header
				continue;

			String[] titleDesc = td.get(1).html().split("<br>");
			Event event = new Event();

			event.setConference(workshop.getConference());
			event.setPlace(workshop.getPlace());

			if(td.get(0).hasText()) {
				String[] timeTitle = td.get(0).html().split("<br>");

				setEventBeginEnd(extractBeginEnd(timeTitle[0].split("-")), event);

				if(timeTitle.length > 1)
					event.setTitle(timeTitle[1]);
			}

			if(event.getTitle() != null) {
				Session session = new Session();

				titleDesc = td.get(1).html().split("<br>");
				session.setTitle(titleDesc[0].replace("<b>", "").replace("</b>", ""));
				session.setDescription(titleDesc[1]);
				session.setPlace(event.getPlace());
				event.addSession(session);
				td = els.get(++i).select("td");

				while(!td.get(0).hasText()) {
					session = new Session();

					titleDesc = td.get(1).html().split("<br>");
					session.setTitle(titleDesc[0].replace("<b>", "").replace("</b>", ""));
					session.setDescription(titleDesc[1]);
					event.addSession(session);
					td = els.get(++i).select("td");
				}

				i--; //the last while iteration has text again, so decremenent and let the for loop increment itself and work on that data
			}
			else if(titleDesc.length > 1) {
				event.setTitle(titleDesc[0].replace("<b>", "").replace("</b>", ""));
				event.setDescription(titleDesc[1]);
			}
			else
				event.setTitle(titleDesc[0]);

			if(event.getTitle().toLowerCase().contains("keynote"))
				event.setCategory(EventCategory.PRESENTATION);
			else if(event.getTitle().toLowerCase().contains("session"))
				event.setCategory(EventCategory.TALK);
			else if(event.getTitle().toLowerCase().contains("break") || event.getTitle().toLowerCase().contains("lunch"))
				event.setCategory(EventCategory.BREAK);

			workshop.addEvent(event);
			workshop.setEnd(event.getEnd());
		}
	}

	/**
	 * Parses the NLPOSS workshop's schedule
	 * @param doc The document containing the schedule
	 * @param workshop The workshop instance to add the schedule to
	 */
	//the method can't be written more concise because it parses one single workshop's schedule which can't be broken up in multiple parts
	private static void parseNLPOSS(Document doc, Workshop workshop) {
		Elements els = doc.selectFirst("#program").select("p");
		Event event = new Event();

		for(int i = 0; i < els.size(); i++) {
			Element el = els.get(i);
			String[] time = el.html().split("<strong>")[0].split(" - ");

			if(time.length < 2) //schedule ends
				break;

			time[0] = time[0].substring(0, 2) + ":" + time[0].substring(2);
			time[1] = time[1].substring(0, 2) + ":" + time[1].substring(2, 4); //cut off excess whitespace and &nbsp;s
			setEventBeginEnd(extractBeginEnd(time), event);
			event.setConference(workshop.getConference());
			event.setPlace(workshop.getPlace());
			event.setTitle(el.html().split("<strong>")[1].split("</strong>")[0]);

			if(els.get(i + 1).html().startsWith("<a href")) {
				el = els.get(++i);

				while(el.html().startsWith("<a href")) {
					Session session = new Session();

					session.setTitle(el.selectFirst("a").text());
					session.setDescription(el.html().split("<em>")[1].split("</em>")[0]);
					session.setPlace(event.getPlace());
					event.addSession(session);
					el = els.get(++i);
				}

				i--; //the last while iteration is a normal event and not part of this current one, so decremenent and let the for loop increment itself and work on that data
			}

			if(event.getTitle().toLowerCase().contains("opening"))
				event.setCategory(EventCategory.WELCOME);
			else if(event.getTitle().toLowerCase().contains("session"))
				event.setCategory(EventCategory.SESSION);
			else if(event.getTitle().toLowerCase().contains("talk"))
				event.setCategory(EventCategory.TALK);
			else if(event.getTitle().toLowerCase().contains("presentation"))
				event.setCategory(EventCategory.PRESENTATION);
			else if(event.getTitle().toLowerCase().contains("break") || event.getTitle().toLowerCase().contains("lunch"))
				event.setCategory(EventCategory.BREAK);

			workshop.addEvent(event);
			workshop.setEnd(event.getEnd());
			event = new Event();
		}
	}

	/**
	 * Extracts the {@link LocalTime} from Strings containing the begin (first index) and end (second index) in the format hh:mm
	 * @param beginEnd The array containing the begin (first index) and end (second index) time in the format hh:mm
	 * @return A {@link LocalTime} array containing the extracted begin (first index) and end (second index)
	 */
	public static final LocalTime[] extractBeginEnd(String[] beginEnd) {
		String[] begin = beginEnd[0].split(":");
		String[] end = beginEnd[1].split(":");

		return new LocalTime[] {
				LocalTime.of(Integer.parseInt(begin[0]), Integer.parseInt(begin[1])),
				LocalTime.of(Integer.parseInt(end[0]), Integer.parseInt(end[1]))
		};
	}

	/**
	 * Sets the given event's begin and end
	 * @param beginEnd An {@link LocalTime} array containing the begin (first index) and end (second index) times
	 * @param event The event to set the begin and end of
	 */
	public static final void setEventBeginEnd(LocalTime[] beginEnd, Event event) {
		event.setBegin(beginEnd[0]);
		event.setEnd(beginEnd[1]);
	}
}