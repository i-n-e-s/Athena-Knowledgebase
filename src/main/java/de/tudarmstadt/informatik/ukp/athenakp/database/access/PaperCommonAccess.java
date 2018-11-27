package de.tudarmstadt.informatik.ukp.athenakp.database.access;

import java.util.List;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;

/**
 * @author Daniel Lehmann
 */
public interface PaperCommonAccess extends CommonAccess<Paper>
{
	/**
	 * Get all papers with specified paper id.
	 * @param id The paper's paper id.
	 * @return A List of all papers with the specified paper id.
	 */
	public List<Paper> getByPaperID(Long paperID);

	/**
	 * Get all papers with specified author.
	 * @param prefix The paper's author.
	 * @return A List of all papers with the specified author.
	 */
	public List<Paper> getByAuthor(String author);

	/**
	 * Get all papers with specified last release date.
	 * @param year The paper's release year.
	 * @param month The paper's release month.
	 * @param day The paper's release day.
	 * @return A List of all papers with the specified release date.
	 */
	public List<Paper> getByReleaseDate(Integer year, Integer month, Integer day);

	/**
	 * Get all papers with specified topic.
	 * @param middleName The paper's topic.
	 * @return A List of all papers with the specified topic.
	 */
	public List<Paper> getByTopic(String topic);

	/**
	 * Get all papers with specified title.
	 * @param lastName The paper's title.
	 * @return A List of all papers with the specified title.
	 */
	public List<Paper> getByTitle(String title);

	/**
	 * Get all papers with specified href.
	 * @param birthdate The paper's href.
	 * @return A List of all papers with the specified href.
	 */
	public List<Paper> getByHref(String href);

	/**
	 * Get all papers with specified pdf's file size.
	 * @param obit The paper's pdf's file size.
	 * @return A List of all papers with the specified pdf's file size.
	 */
	public List<Paper> getByPdfFileSize(Integer pdfFileSize);

	/**
	 * Get all papers of an anthology
	 * @param anthology the anthology the paper appeared in
	 * @return A list of all papers that appeared in the anthology
	 */
	public List<Paper> getByAnthology(String anthology);
}
