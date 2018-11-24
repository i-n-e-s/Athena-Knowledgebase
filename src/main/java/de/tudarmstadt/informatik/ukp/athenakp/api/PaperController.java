package de.tudarmstadt.informatik.ukp.athenakp.api;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.hibernate.PaperHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Author;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;

/**
 * @author Daniel Lehmann
 */
@RestController
@RequestMapping("/papers")
public class PaperController {
	private final PaperHibernateAccess access = new PaperHibernateAccess();

	@RequestMapping("") //default
	public List<Paper> getAllPapers() {
		return access.get();
	}

	@RequestMapping("/byPaperID/{value}")
	public List<Paper> byPaperID(@PathVariable("value")Long value) {
		return access.getByPaperID(value);
	}

	@RequestMapping("/byPaperID/{value}/getAuthors")
	public Set<Author> getAuthors(@PathVariable("value")Long value) {
		List<Paper> papers = byPaperID(value);

		if(papers.size() > 0)
			return papers.get(0).getAuthors();
		else return null;
	}

	@RequestMapping("/byAuthor/{value}")
	public List<Paper> byAuthor(@PathVariable("value")String value) { //TODO implement this
		return null;
	}

	@RequestMapping("/byReleaseDate/{year}/{month}/{day}")
	public List<Paper> byReleaseDate(@PathVariable("year")Integer year, @PathVariable("month")Integer month, @PathVariable("day")Integer day) {
		return access.getByReleaseDate(year, month, day);
	}

	@RequestMapping("/byTopic/{value}")
	public List<Paper> byTopic(@PathVariable("value")String value) {
		return access.getByTopic(value);
	}

	@RequestMapping("/byTitle/{value}")
	public List<Paper> byTitle(@PathVariable("value")String value) {
		return access.getByTitle(value);
	}

	@RequestMapping("/byHref/{value}")
	public List<Paper> byHref(@PathVariable("value")String value) {
		return access.getByHref(value);
	}

	@RequestMapping("/byPdfFileSize/{value}")
	public List<Paper> byPdfFileSize(@PathVariable("value")Integer value) {
		return access.getByPdfFileSize(value);
	}
}
