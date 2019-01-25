package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.persistence.Column;

public abstract class Model {

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
					}else if(!(field.get(this).equals(field.get(model)))) {
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
	public boolean  equalsNullAsWildcard(Object model) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
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

}