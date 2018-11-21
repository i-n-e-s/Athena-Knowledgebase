package de.tudarmstadt.informatik.ukp.athenakp.api;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.hibernate.PaperHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;

/**
 * @author Daniel Lehmann
 */
@RestController
@RequestMapping("/papers")
public class PaperController {
	private final PaperHibernateAccess access = new PaperHibernateAccess();

	@RequestMapping("/byPaperID/{value}")
	public List<Paper> byPaperID(@PathVariable("value")Long value) {
		return access.getByPaperID(value);
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
