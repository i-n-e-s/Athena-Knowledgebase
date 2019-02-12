package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.persistence.Column;

//will be populated once testbench branch is merged, for now this class serves as a simple common super class so that the api can return a list of models
public class Model {

    /**
     *
     * This method compares all fields of the given models with this model, except all fields containing ID and all fields referencing other models. Null is seen
     * as value. It is reflective, symmetric and transitive
     *
     * @param model A model, preferably of the same class of the object from which called
     * @return true if all fields, except the one referencing other objects, are equal false if the given object is null, not the same class ore a field is different
     */
    public boolean  equalsWithoutID(Object model){
        if (model == null) return false;

        if (!this.getClass().equals(model.getClass())) return false;

        Field fields[] = getAllFields(this);

        for (Field field : fields) {
            boolean wasAccessible= field.isAccessible();
            if(!wasAccessible) field.setAccessible(true);
            if(!field.getName().contains("ID") && field.getAnnotation(Column.class)!=null) {//Field is not ID and Information is Stored in Object, because it's a Column
                //Start checking equality
                try {
                    if(field.get(this)==null) {//Both are null?
                        if(!(field.get(model) == null)) {
                            if(!wasAccessible) field.setAccessible(false);
                            return false;}
                    } else if(!(field.get(this).equals(field.get(model)))) {
                        if(!wasAccessible) field.setAccessible(false);
                        return false;
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(!wasAccessible) field.setAccessible(false);
        }
        return true;
    }

    /**
     *
     * This method compares all fields of the given models with this model, except all fields containing ID and all fields referencing other models. If a field is set to null it is seen as a wildcard
     *
     * @param model A model, preferably of the same class of the object from which called
     * @return true if all fields, except the one referencing other objects, are equal false if the given object is null, not the same class ore a field is different
     * @throws IllegalArgumentException Should not be thrown, if it works correctly
     * @throws IllegalAccessException Should not be thrown, if it works correctly
     * @throws NoSuchFieldException Should not be thrown, if it works correctly
     * @throws SecurityException Should not be thrown, if it works correctly
     */
    public boolean equalsNullAsWildcard(Object model) {
        if (model == null) return false;

        if (!this.getClass().equals(model.getClass())) return false;

        Field fields[] = getAllFields(this);
        Field modelFields[] = getAllFields((Model)model);

        for (Field field : fields) {
            boolean wasAccessible= field.isAccessible();
            if(!wasAccessible) field.setAccessible(true);
            if(!field.getName().contains("ID") && field.getAnnotation(Column.class)!=null) {//Field is not ID and Information is Stored in Object, because it's a Column
                //Start checking equality
                try {
                    if(field.get(this) != null && field.get(model) != null) {
                        if(!(field.get(this).equals(field.get(model)))) {
                            if(!wasAccessible) field.setAccessible(false);
                            return false;
                        }
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(!wasAccessible) field.setAccessible(false);
        }
        return true;
    }

    /**
     *
     * A method to find all fields in the hierarchy in
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
     * Complements a Model Object with the information of another
     * Other Object must be equal with Null as wildcard
     * Fields containing ID are ignored
     *
     * @param model the model Object to copy the attributes from
     * @return true if changes occurred
     *
    public boolean complementBy( Model model ) {
    if (! this.equalsNullAsWildcard(model) ) { return false; }
    //if (! (this instanceof model.getClass() )) { throw IllegalArgumentException(); }

    for (Field field : this.getClass().getDeclaredFields()) {
    boolean wasAccessible = field.isAccessible();

    if (!wasAccessible) field.setAccessible(true);

    if (field.getName().contains("ID")) { continue;	}

    try {
    if (Collection.class.isAssignableFrom(field.getType())) { //If field contains collection
    Collection<Model> thisColl = (Collection<Model>) field.get(this);
    Collection<Model> modelColl = (Collection) model.getClass().getDeclaredField(field.getName()).get(model);
    for (Model o : modelColl) {	//Add All elements from one List to the other
    if (thisColl.contains(o)) { continue; }		//TODO not add but complement
    thisColl.add(o);
    }
    }
    else {
    if (field.get(this) != null) { continue; }	//Don't Overwrite primary attributes
    field.set(this, model.getClass().getDeclaredField(field.getName()).get(model));
    }
    } catch (NullPointerException e) {//Throws NullPointerException, when a field in the containing class contains null
    //Both values are null we can go on
    } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) { //Should never be thrown
    e.printStackTrace();
    }

    if (!wasAccessible) field.setAccessible(false);
    }
    return true;
    }*/

    /**
     * Compares two Objects using .equals method, allowing an Object to be null TODO in utils class
     * @param a First object to compare
     * @param b Second object to compare
     * @return false if any object is null or equals() returns false
     */
    public static boolean equalsNotNull( Object a, Object b ) {
        if ( a == null || b == null ) { return false; }
        return a.equals(b);
    }

    /**
     * Creates the two way relation between Author and Paper Object
     * @return false if already connected
     */
    public static boolean connectAuthorPaper(Person author, Paper paper) {
        boolean changed = false;
        if ( !author.getPapers().contains(paper) ) { author.addPaper(paper); changed = true; }
        if ( !paper.getAuthors().contains(author) ) { paper.addAuthor(author); changed = true; }
        return changed;
    }

	/*

	@Override
	public String toString() {
		String ret = "{";

		for (Field field : this.getClass().getDeclaredFields()) {
			boolean wasAccessible = field.isAccessible();
			if (!wasAccessible) field.setAccessible(true);

			try {
				if ( Collection.class.isAssignableFrom(field.getType()) ) {
					for ( Object o : (Collection) field.get(this) ) {
						o.toString();
					}
				}
				ret = ret + field.getName() + ": " + String.valueOf( field.get(this) ) + ",\n";

			} catch (NullPointerException e) {//Throws NullPointerException, when a field in the containing class contains null
				//Both values are null we can go on
			} catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) { //Should never be thrown
				e.printStackTrace();
			}

			if (!wasAccessible) field.setAccessible(false);
		}
		return true;


	}*/
}