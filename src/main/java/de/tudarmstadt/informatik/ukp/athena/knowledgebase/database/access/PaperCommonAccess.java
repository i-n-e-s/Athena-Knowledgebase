package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access;

import java.util.List;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;

/**
 * @author Daniel Lehmann
 */
public interface PaperCommonAccess extends CommonAccess<Paper> {
	/**
	 * Get all papers with specified paper id.
	 * @param paperID The paper's paper id.
	 * @return A List of all papers with the specified paper id.
	 */
	public List<Paper> getByPaperID(Long paperID);

	/**
	 * Get all papers with specified author.
	 * @param author The paper's author.
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
	 * Get all papers released in the specified release range.
	 * @param year1 The start year to search for released papers
	 * @param month1 The start month to search for released papers
	 * @param year2 The end year to search for released papers
	 * @param month2 The end month to search for released papers
	 * @return A List of all papers released in the specified release range.
	 */
	public List<Paper> getByReleaseRange(Integer year1, Integer month1, Integer year2, Integer month2);

	/**
	 * Get all papers with specified topic.
	 * @param topic The paper's topic.
	 * @return A List of all papers with the specified topic.
	 */
	public List<Paper> getByTopic(String topic);

	/**
	 * Get all papers with specified title.
	 * @param title The paper's title.
	 * @return A List of all papers with the specified title.
	 */
	public List<Paper> getByTitle(String title);

	/**
	 * Get all papers with specified href.
	 * @param href The paper's href.
	 * @return A List of all papers with the specified href.
	 */
	public List<Paper> getByHref(String href);

	/**
	 * Get all papers with specified pdf's file size.
	 * @param pdfFileSize The paper's pdf's file size.
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
