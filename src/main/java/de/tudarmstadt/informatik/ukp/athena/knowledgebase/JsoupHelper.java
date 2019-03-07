package de.tudarmstadt.informatik.ukp.athena.knowledgebase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupHelper {
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.82 Safari/537.36 Viv/2.3.1440.41";

	/**
	 * Connects to the given URL with the default user agent (JsoupHelper.USER_AGENT). If the connection fails, it tries again 4 more times (5 tries total)
	 * If no tries are left, the method will exit and the program execution can continue without interruption.
	 * The printed error message will be "Exceeded <code>tries</code> tries when trying to connect to <code>url</code>"
	 * @param url The URL to connect to
	 */
	public static Document connect(String url) {
		return connect(url, "Exceeded %s tries when trying to connect to " + url);
	}

	/**
	 * Connects to the given URL with the default user agent (JsoupHelper.USER_AGENT). If the connection fails, it tries again 4 more times (5 tries total)
	 * If no tries are left, the method will exit and the program execution can continue without interruption
	 * @param url The URL to connect to
	 * @param errorMessage The error message that should show when no tries are left
	 */
	public static Document connect(String url, String errorMessage) {
		return connect(url, USER_AGENT, errorMessage);
	}

	/**
	 * Connects to the given URL with the given user agent. If the connection fails, it tries again 4 more times (5 tries total)
	 * If no tries are left, the method will exit and the program execution can continue without interruption
	 * @param url The URL to connect to
	 * @param userAgent The user agent to connect with
	 * @param errorMessage The error message that should show when no tries are left
	 * @return The {@link org.jsoup.nodes.Document Document} of the given url, null if the connection was unsuccessful and no tries are left
	 */
	public static Document connect(String url, String userAgent, String errorMessage) {
		return connect(url, userAgent, errorMessage, 5);
	}

	/**
	 * Connects to the given URL with the given user agent. If the connection fails, it tries again.
	 * If no tries are left, the method will exit and the program execution can continue without interruption
	 * @param url The URL to connect to
	 * @param userAgent The user agent to connect with
	 * @param errorMessage The error message that should show when no tries are left
	 * @param tries The amount of tries until an error
	 * @return The {@link org.jsoup.nodes.Document Document} of the given url, null if the connection was unsuccessful and no tries are left
	 */
	public static Document connect(String url, String userAgent, String errorMessage, int tries) {
		for(int i = 0; i < tries; i++) {
			Document doc;

			try {
				doc = Jsoup.connect(url).userAgent(userAgent).get();
				return doc; //the above line did not error, so nothing was catched -> connection successfully established
			}
			catch(Exception e) {
				System.out.println("\"" + url + "\": Tries left: " + (tries - i - 1));
			}
		}

		System.out.printf(errorMessage + "\n", tries);
		return null;
	}
}
