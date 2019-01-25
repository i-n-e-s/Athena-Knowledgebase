package de.tudarmstadt.informatik.ukp.athenakp.api;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.PaperCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.jpa.PaperJPAAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Author;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;

/**
 * Serves as a REST API definition for accessing papers. Any mapping defined by a
 * method in this class has to be prepended with "/papers" since this class is annotated
 * with a RequestMapping.
 * @author Daniel Lehmann
 */
@RestController
@RequestMapping("/papers")
public class PaperController {
	private final PaperCommonAccess access = new PaperJPAAccess();

	/**
	 * @return All papers in the database
	 */
	@RequestMapping("") //default
	public List<Paper> getAllPapers() {
		return access.get();
	}

	/**
	 * @param value The paper ID
	 * @return The paper with the specified id, if existing
	 */
	@RequestMapping("/byPaperID/{value}")
	public List<Paper> byPaperID(@PathVariable("value")Long value) {
		return access.getByPaperID(value);
	}

	/**
	 * @param value The paper ID
	 * @return All author that took part in writing the paper with the given ID
	 */
	@RequestMapping("/byPaperID/{value}/getAuthors")
	public Set<Author> getAuthors(@PathVariable("value")Long value) {
		List<Paper> papers = byPaperID(value);

		if(papers.size() > 0)
			return papers.get(0).getAuthors();
		else return null;
	}

	/**
	 * @param value The paper ID
	 * @return The Link to the pdf of this paper
	 */
	@RequestMapping("/byPaperID/{value}/href")
	public String getHref(@PathVariable("value")Long value) {
		List<Paper> papers = byPaperID(value);

		if(papers.size() > 0)
			return papers.get(0).getHref();
		else return null;
	}

	/**
	 * @param year The year in which the paper was released
	 * @param month The month in which the paper was released
	 * @param day The day on which the paper was released
	 * @return The papers with the specified release date, if existing
	 */
	@RequestMapping("/byReleaseDate/{year}/{month}/{day}")
	public List<Paper> byReleaseDate(@PathVariable("year")Integer year, @PathVariable("month")Integer month, @PathVariable("day")Integer day) {
		return access.getByReleaseDate(year, month, day);
	}

	/**
	 * @param year1 The start year to search for released papers
	 * @param month1 The start month to search for released papers
	 * @param year2 The end year to search for released papers
	 * @param month2 The end month to search for released papers
	 * @return The papers with the specified release date, if existing
	 */
	@RequestMapping("/byReleaseRange/{year1}/{month1}/{year2}/{month2}")
	public List<Paper> byReleaseRange(@PathVariable("year1")Integer year1, @PathVariable("month1")Integer month1, @PathVariable("year2")Integer year2, @PathVariable("month2")Integer month2) {
		return access.getByReleaseRange(year1, month1, year2, month2);
	}

	/**
	 * @param value The paper's topic
	 * @return All papers with the given topic
	 */
	@RequestMapping("/byTopic/{value}")
	public List<Paper> byTopic(@PathVariable("value")String value) {
		return access.getByTopic(value);
	}

	/**
	 * @param value The paper's title
	 * @return All papers with the given title
	 */
	@RequestMapping("/byTitle/{value}")
	public List<Paper> byTitle(@PathVariable("value")String value) {
		return access.getByTitle(value);
	}

	/**
	 *
	 * @param value the corresponding anthology
	 * @return All papers of the given anthology (currently only one) TODO: add wildcard search e.g. "CR-18"
	 * TODO: fix: still broken "-" does not play well - probably only with the browser. If so, this is probably fine.
	 */
	@RequestMapping("/byAnthology/{value]")
	public List<Paper> byAnthology(@PathVariable("value")String value) {
		return access.getByAnthology(value);
	}

	/**
	 * @param value The paper's direct download link
	 * @return All paper's with the given download link
	 */
	@RequestMapping("/byHref/{value}") //TODO: is this necessary?
	public List<Paper> byHref(@PathVariable("value")String value) {
		return access.getByHref(value);
	}

	/**
	 * @param value The file size of the papers .pdf file
	 * @return All papers with the given pdf file size
	 */
	@RequestMapping("/byPdfFileSize/{value}")
	public List<Paper> byPdfFileSize(@PathVariable("value")Integer value) {
		return access.getByPdfFileSize(value);
	}
	
	/**
	 * 
	 * @param value The author from which the paper are requested
	 * @return All papers, that the given author has published
	 */
	@RequestMapping("/byAuthor/{value}")
	public List<Paper> byAuthor(@PathVariable("value")String value){
		return access.getByAuthor(value);
	}
}
