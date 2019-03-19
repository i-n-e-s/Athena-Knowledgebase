package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Model {
	private static Logger logger = LogManager.getLogger(Model.class);

	/**
	 * This method compares all fields of the given models with this model, except all fields containing ID and all fields referencing other models. Null is seen
	 * as value. It is reflective, symmetric and transitive
	 *
	 * @param model A model, preferably of the same class of the object from which called
	 * @return true if all fields, except the ones referencing other objects, are equal. false if the given object is null, not the same class or a field is different
	 */
	public boolean  equalsWithoutID(Object model){
		if (model == null) return false;

		if (!this.getClass().equals(model.getClass())) return false; //Different Classes can't be equal

		Field fields[] = getAllFields(this);

		for (Field field : fields) {
			boolean wasAccessible= field.isAccessible();
			if(!wasAccessible) field.setAccessible(true);
			if(!field.getName().contains("ID") && field.getAnnotation(Column.class)!=null) {//Field is not ID and Information is Stored in Object, because it's a Column
				//Start checking equality
				try {
					if(field.get(this)==null) {
						if(!(field.get(model) == null)) { //Both are null? => equal
							if(!wasAccessible) field.setAccessible(false);
							return false;//One of the fields is not null
						}
					}else if(!(field.get(this).equals(field.get(model)))) {//
						if(!wasAccessible) field.setAccessible(false);
						return false;//fields are not equal values
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					logger.fatal("This should never be reached...", e);
				}
			}
			if(!wasAccessible) field.setAccessible(false);
		}
		return true;
	}

	/**
	 * This method compares all fields of the given models with this model, except all fields containing ID and all fields referencing other models. If a field is set to null it is seen as a wildcard
	 *
	 * @param model A model, preferably of the same class of the object from which called
	 * @return true if all fields, except the ones referencing other objects, are equal. false if the given object is null, not the same class or a field is different
	 */
	public boolean  equalsNullAsWildcard(Object model) {
		if (model == null) return false;

		if (!this.getClass().equals(model.getClass())) return false; //Different Classes can't be equal

		Field fields[] = getAllFields(this);

		for (Field field : fields) {
			boolean wasAccessible= field.isAccessible();
			if(!wasAccessible) field.setAccessible(true);
			if(!field.getName().contains("ID") && field.getAnnotation(Column.class)!=null) {//Field is not ID and Information is Stored in Object, because it's a Column
				//Start checking equality
				try {
					if(field.get(this) != null && field.get(model) != null) {// is one value null
						if(!(field.get(this).equals(field.get(model)))) {
							if(!wasAccessible) field.setAccessible(false);
							return false;// values are not equal
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					logger.fatal("This should never be reached...", e);
				}
			}
			if(!wasAccessible) field.setAccessible(false);
		}
		return true;
	}

	/**
	 * Returns all fields of this class and its superclasses, up to the class "Model"
	 *
	 * @param model A model or an Class which extends Model
	 * @return All fields declared in the class hierarchy between the given Object and Model
	 */
	private Field[] getAllFields(Model model) {
		ArrayList<Field> returnValue = new ArrayList<Field>();
		Class<? extends Model> currentClass = model.getClass();
		while (!currentClass.getName().endsWith(".Model")) {
			Field[] fields = currentClass.getDeclaredFields();
			for (Field field : fields) {
				returnValue.add(field);
			}
			currentClass = (Class<? extends Model>) currentClass.getSuperclass();
		}
		Field[] returnArray = new Field[returnValue.size()];
		returnValue.toArray(returnArray);
		return returnArray;
	}

	/**
	 * Return the Value of the Field which is annotated as ID in this model
	 *
	 * @return the Id of this object
	 */
	public Object getID() {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			boolean wasAccessible= field.isAccessible();
			if(!wasAccessible) field.setAccessible(true);
			if(field.getAnnotation(Id.class) != null) {
				Object returnValue = null;
				try {
					returnValue = field.get(this);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					logger.fatal("getId should check this, contact developer", e);
				}
				if(!wasAccessible) field.setAccessible(false);
				return returnValue;
			}
			if(!wasAccessible) field.setAccessible(false);
		}
		return null;
	}
	
	/**
     * Creates the two way relation between Author and Paper Object
     * @return false if already connected
     */
    public static boolean connectAuthorPaper(Person author, Paper paper) {
        boolean changed = false;

        //Search if connection already exists
        boolean found = false;
        for ( Paper authorsPaper : author.getPapers() ) {
            if ( authorsPaper.equalsWithoutID(paper) ) { return false; }
        }

        if ( !author.getPapers().contains(paper) ) { author.addPaper(paper); changed = true; }
        if ( !paper.getAuthors().contains(author) ) { paper.addAuthor(author); changed = true; }
        return changed;
    }

}