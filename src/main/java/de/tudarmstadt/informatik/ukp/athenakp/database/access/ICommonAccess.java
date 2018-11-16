package de.tudarmstadt.informatik.ukp.athenakp.database.access;

import java.util.List;

/**
 * Common access interface hiding the database layer. Every Access-Class should implement this
 * as it demands implementation of basic database access functionality.
 *
 * @author Tristan Wettich
 * @param <T> The model-class and respective table to be accessed
 */
public interface ICommonAccess<T> {

	/**
	 * Add/save data in database.
	 * @param data Object of model T to be saved
	 */
	public void add(T data);

	/**
	 * Update data in database.
	 * @param data Object of model T to be updated
	 */
	public void update(T data);

	/**
	 * Delete data from database.
	 * @param data Object of model T to be removed
	 */
	public void delete(T data);

	/**
	 * Get a List of all entries of model/table T.
	 */
	public List<T> get();

}
