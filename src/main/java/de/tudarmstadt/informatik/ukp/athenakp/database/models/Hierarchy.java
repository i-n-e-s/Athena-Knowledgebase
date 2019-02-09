package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes that the annotated field describes a hierarchical relationship between the containing class and the generic type of the collection
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Hierarchy {
	/**
	 * @return The entity name (as found in the database) that is stored here
	 */
	String entityName();
}
