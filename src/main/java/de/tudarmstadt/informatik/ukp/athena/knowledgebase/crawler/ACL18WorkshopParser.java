package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.ScheduleEntry;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Session;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.SessionCategory;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.SessionPart;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Workshop;

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
					LocalDate date = LocalDate.of(2018, CrawlerToolset.getMonthIndex(dayMonth[1]), Integer.parseInt(dayMonth[0]));

					workshop.setConferenceName("ACL 2018");
					workshop.setBegin(LocalDateTime.of(date, LocalTime.of(9, 0)));
					workshop.setEnd(LocalDateTime.of(date, LocalTime.of(17, 0))); //assume 5pm, because the schedule table is not 100% proportional
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
		Session previousSession = null;

		for(Element el : els) {
			Session session = new Session();
			Elements td = el.select("td");
			String[] timeSplit = td.get(0).text().split(":");
			LocalTime time;

			if(timeSplit[0].contains("h")) //there's a single goddamn h in the closing event
				timeSplit[0] = timeSplit[0].replace("h", "");

			time = LocalTime.of(Integer.parseInt(timeSplit[0]), Integer.parseInt(timeSplit[1]));
			session.setBegin(LocalDateTime.of(workshop.getBegin().toLocalDate(), time));

			//i'm assuming that the closing event is the end of the workshop, thus it does not get added
			if(previousSession != null) {
				previousSession.setEnd(LocalDateTime.of(workshop.getEnd().toLocalDate(), time));
				workshop.addSession(previousSession);
				workshop.setEnd(previousSession.getEnd());
			}

			if(el.hasClass("program_break")) {
				session.setTitle(td.get(1).text().trim());
				session.setCategory(SessionCategory.BREAK);
			}
			else {
				session.setTitle(td.get(1).select("b").text().trim());

				switch(session.getTitle().toLowerCase()) {
					case "oral presentation":
						session.setDescription(td.get(1).text().replace(session.getTitle(), ""));
						session.setCategory(SessionCategory.PRESENTATION);
						break;
					case "oral presentations": case "poster session":
						String html = td.get(1).html().replace("<b> " + session.getTitle() +" </b>", "").trim();
						String[] sections = html.split("<i>");

						for(String section : sections) {
							SessionPart sessionPart = new SessionPart();
							String[] titleDesc = section.split("</i>");

							if(titleDesc.length < 2) //doesn't contain an entry
								continue;

							sessionPart.setPlace(workshop.getPlace());
							sessionPart.setTitle(titleDesc[0].trim());
							sessionPart.setDescription(titleDesc[1].split("<br>")[1].trim());
							session.addSessionPart(sessionPart);

							switch(session.getTitle().toLowerCase()) {
								case "oral presentations": session.setCategory(SessionCategory.PRESENTATION); break;
								case "poster session": session.setCategory(SessionCategory.SESSION); break;
							}
						}

						break;
					case "panel/discussions": session.setCategory(SessionCategory.TALK); break;
				}
			}

			if(session.getCategory() == null) {
				if(session.getTitle().contains("Opening"))
					session.setCategory(SessionCategory.WELCOME);
				else if(session.getTitle().contains("Talk"))
					session.setCategory(SessionCategory.TALK);
			}

			previousSession = session;
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

			Session previousSession = null;
			String[] br = el.html().split("<br>");

			for(String line : br) {
				if(line.contains("|")) {
					Session session = new Session();
					String[] split = line.split("\\|"); //splitting by | only basically gets the char array as a string array

					setSessionBeginEnd(extractBeginEnd(split[0].trim().split("–")), workshop.getBegin().toLocalDate(), workshop.getEnd().toLocalDate(), session); //NOT A HYPHEN!!! IT'S AN 'EN DASH'
					session.setPlace(workshop.getPlace());
					session.setTitle(split[1].trim());

					if(session.getTitle().contains("<em>")) {
						String previousTitle = session.getTitle();
						session.setTitle(previousTitle.split(":")[0]);
						session.setDescription(previousTitle.split("<em>")[1].replace("</em>", ""));
					}

					previousSession = session;
				}
				else if(previousSession != null) {
					previousSession.setDescription(previousSession.getTitle());

					if(!previousSession.getDescription().equals("Panel discussion")) //panel discussion doesn't have the same -
						previousSession.setTitle(line.split("- ")[1].split("</b>")[0].trim());
				}

				if(previousSession != null) {
					String title = previousSession.getTitle();

					if(title.contains("Opening"))
						previousSession.setCategory(SessionCategory.WELCOME);
					else if(title.contains("coffee") || title.contains("Lunch"))
						previousSession.setCategory(SessionCategory.BREAK);
					else if(title.contains("Poster"))
						previousSession.setCategory(SessionCategory.SESSION);
					else
						previousSession.setCategory(SessionCategory.TALK);

					if(previousSession.getDescription() != null && previousSession.getDescription().contains("<a href="))
						previousSession.setDescription(previousSession.getDescription().split(">")[1].split("<")[0]);

					if(previousSession.getDescription() != null && previousSession.getDescription().equals("Panel discussion") && !line.contains("discussion")) { //get panel discussion description
						String[] split;

						line = line.replace("<b>", "").replace("</a>", "").replace("</b>", "");
						split = line.split("\">");
						previousSession.setTitle(previousSession.getDescription());
						previousSession.setDescription(split[1].split("<")[0] + split[2]);
					}

					workshop.addSession(previousSession);
					workshop.setEnd(previousSession.getEnd());
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
		Session previousSession = null;

		for(Element el : els) {
			if(!programFound && el.id().equals("h.p__bAUwIOcmLuf")){
				programFound = true;
				continue;
			}
			else if(programFound && el.id().equals("h.p_LOk0X7QqxUPR")) //end of program
				break;
			else if(!programFound)
				continue;

			if(previousSession != null) {
				boolean skip = false;

				if(!el.text().contains("--")) {
					skip = true;
					previousSession.setDescription(el.text());
				}

				if(previousSession.getTitle().contains("Opening"))
					previousSession.setCategory(SessionCategory.WELCOME);
				else if(previousSession.getTitle().contains("Talk"))
					previousSession.setCategory(SessionCategory.TALK);
				else if(previousSession.getTitle().contains("Break"))
					previousSession.setCategory(SessionCategory.BREAK);
				else if(previousSession.getTitle().contains("session"))
					previousSession.setCategory(SessionCategory.SESSION);

				workshop.addSession(previousSession);
				workshop.setEnd(previousSession.getEnd());

				if(skip)
					continue;
			}

			Session session = new Session();
			//this time extraction code is used often, but there is a lot of variation so no util method
			String info = el.html().split("/strong>")[1];

			setSessionBeginEnd(extractBeginEnd(el.html().split("strong>")[1].split("<")[0].trim().split("--")), workshop.getBegin().toLocalDate(), workshop.getEnd().toLocalDate(), session);
			session.setPlace(workshop.getPlace());

			if(info.contains(":") && !info.contains("Invited")) {
				String[] infoSplit = info.split(":");

				session.setTitle(infoSplit[0]);
				session.setDescription(infoSplit[1].replace("<em>", "").replace("</em>", ""));
			}
			else
				session.setTitle(info);

			previousSession = session;
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
		Session session = null;

		for(Element el : els) {
			if(!programFound && el.id().equals("workshop-programme")){
				programFound = true;
				continue;
			}
			else if(programFound && el.id().equals("comitees")) //end of program
				break;
			else if(!programFound)
				continue;

			if(session == null) {
				session = new Session();
				setSessionBeginEnd(extractBeginEnd(el.html().split("–")), workshop.getBegin().toLocalDate(), workshop.getEnd().toLocalDate(), session); //NOT A HYPHEN!!! IT'S AN 'EN DASH'
				session.setPlace(workshop.getPlace());
			}
			else {
				if(el.html().contains("<br>")) {
					String[] split = el.html().split("<br>");

					session.setTitle(split[0].replace("<strong>", "").replace("</strong>", ""));
					session.setDescription(split[1]);
					session.setCategory(SessionCategory.PRESENTATION);
				}
				else {
					session.setTitle(el.text());

					if(session.getTitle().contains("Discussion"))
						session.setCategory(SessionCategory.TALK);
					else if(session.getTitle().contains("Session")) {
						String[] titleSplit = session.getTitle().split("-");

						session.setDescription(titleSplit[1].trim());
						session.setTitle(titleSplit[0].trim());
						session.setCategory(SessionCategory.SESSION);
					}
					else
						session.setCategory(SessionCategory.BREAK);
				}

				workshop.addSession(session);
				workshop.setEnd(session.getEnd());
				session = null;
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

			Session session = new Session();

			setSessionBeginEnd(extractBeginEnd(time), workshop.getBegin().toLocalDate(), workshop.getEnd().toLocalDate(), session);
			session.setPlace(workshop.getPlace());
			session.setTitle(el.html().split("</strong>")[1].replace("<em>", "").replace("</em>", "").replace("&nbsp;", " "));

			if(session.getTitle().toLowerCase().contains("opening"))
				session.setCategory(SessionCategory.WELCOME);
			else if(session.getTitle().toLowerCase().contains("keynote"))
				session.setCategory(SessionCategory.PRESENTATION);
			else if(session.getTitle().toLowerCase().contains("break"))
				session.setCategory(SessionCategory.BREAK);
			else if(session.getTitle().toLowerCase().contains("results"))
				session.setCategory(SessionCategory.CEREMONY);
			else
				session.setCategory(SessionCategory.TALK);

			workshop.addSession(session);
			workshop.setEnd(session.getEnd());
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
			Session session = new Session();

			session.setPlace(workshop.getPlace());

			if(td.get(0).hasText()) {
				String[] timeTitle = td.get(0).html().split("<br>");

				setSessionBeginEnd(extractBeginEnd(timeTitle[0].split("-")), workshop.getBegin().toLocalDate(), workshop.getEnd().toLocalDate(), session);

				if(timeTitle.length > 1)
					session.setTitle(timeTitle[1]);
			}

			if(session.getTitle() != null) {
				SessionPart sessionPart = new SessionPart();

				titleDesc = td.get(1).html().split("<br>");
				sessionPart.setTitle(titleDesc[0].replace("<b>", "").replace("</b>", ""));
				sessionPart.setDescription(titleDesc[1]);
				sessionPart.setPlace(session.getPlace());
				session.addSessionPart(sessionPart);
				td = els.get(++i).select("td");

				while(!td.get(0).hasText()) {
					sessionPart = new SessionPart();

					titleDesc = td.get(1).html().split("<br>");
					sessionPart.setTitle(titleDesc[0].replace("<b>", "").replace("</b>", ""));
					sessionPart.setDescription(titleDesc[1]);
					session.addSessionPart(sessionPart);
					td = els.get(++i).select("td");
				}

				i--; //the last while iteration has text again, so decremenent and let the for loop increment itself and work on that data
			}
			else if(titleDesc.length > 1) {
				session.setTitle(titleDesc[0].replace("<b>", "").replace("</b>", ""));
				session.setDescription(titleDesc[1]);
			}
			else
				session.setTitle(titleDesc[0]);

			if(session.getTitle().toLowerCase().contains("keynote"))
				session.setCategory(SessionCategory.PRESENTATION);
			else if(session.getTitle().toLowerCase().contains("session"))
				session.setCategory(SessionCategory.TALK);
			else if(session.getTitle().toLowerCase().contains("break") || session.getTitle().toLowerCase().contains("lunch"))
				session.setCategory(SessionCategory.BREAK);

			workshop.addSession(session);
			workshop.setEnd(session.getEnd());
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
		Session session = new Session();

		for(int i = 0; i < els.size(); i++) {
			Element el = els.get(i);
			String[] time = el.html().split("<strong>")[0].split(" - ");

			if(time.length < 2) //schedule ends
				break;

			time[0] = time[0].substring(0, 2) + ":" + time[0].substring(2);
			time[1] = time[1].substring(0, 2) + ":" + time[1].substring(2, 4); //cut off excess whitespace and &nbsp;s
			setSessionBeginEnd(extractBeginEnd(time), workshop.getBegin().toLocalDate(), workshop.getEnd().toLocalDate(), session);
			session.setPlace(workshop.getPlace());
			session.setTitle(el.html().split("<strong>")[1].split("</strong>")[0]);

			if(els.get(i + 1).html().startsWith("<a href")) {
				el = els.get(++i);

				while(el.html().startsWith("<a href")) {
					SessionPart sessionPart = new SessionPart();

					sessionPart.setTitle(el.selectFirst("a").text());
					sessionPart.setDescription(el.html().split("<em>")[1].split("</em>")[0]);
					sessionPart.setPlace(session.getPlace());
					session.addSessionPart(sessionPart);
					el = els.get(++i);
				}

				i--; //the last while iteration is a normal event and not part of this current one, so decremenent and let the for loop increment itself and work on that data
			}

			if(session.getTitle().toLowerCase().contains("opening"))
				session.setCategory(SessionCategory.WELCOME);
			else if(session.getTitle().toLowerCase().contains("session"))
				session.setCategory(SessionCategory.SESSION);
			else if(session.getTitle().toLowerCase().contains("talk"))
				session.setCategory(SessionCategory.TALK);
			else if(session.getTitle().toLowerCase().contains("presentation"))
				session.setCategory(SessionCategory.PRESENTATION);
			else if(session.getTitle().toLowerCase().contains("break") || session.getTitle().toLowerCase().contains("lunch"))
				session.setCategory(SessionCategory.BREAK);

			workshop.addSession(session);
			workshop.setEnd(session.getEnd());
			session = new Session();
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
	 * Sets the given session's begin and end.
	 * @param beginEnd An {@link LocalTime} array containing the begin (first index) and end (second index) times
	 * @param beginDate The {@link LocalDate} to use as the begin date, usually the one of the workshop
	 * @param endDate The {@link LocalDate} to use as the end date, usually the one of the workshop
	 * @param session The session to set the begin and end of
	 */
	public static final void setSessionBeginEnd(LocalTime[] beginEnd, LocalDate beginDate, LocalDate endDate, Session session) {
		session.setBegin(LocalDateTime.of(beginDate, beginEnd[0]));
		session.setEnd(LocalDateTime.of(endDate, beginEnd[1]));
	}
}