package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import java.lang.reflect.Field;

public abstract class Model {

	/**
	 * 
	 * This method compares all fields of the given models with this model, except all fields containing ID and all fields referencing other models. Null is seen
	 * as value.
	 * 
	 * @param model A model, preferably of the same class of the object from which called
	 * @return true if all fields, except the one referencing other objects, are equal false if the given object is null, not the same class ore a field is different
	 * @throws IllegalArgumentException Should not be thrown, if it works correctly
	 * @throws IllegalAccessException Should not be thrown, if it works correctly
	 * @throws NoSuchFieldException Should not be thrown, if it works correctly
	 * @throws SecurityException Should not be thrown, if it works correctly
	 */
	public boolean  equalsWithoutID(Object model) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		if (model == null) return false;

		if (!this.getClass().equals(model.getClass())) return false;

		for (Field field : this.getClass().getDeclaredFields()) {
			boolean wasAccessible= field.isAccessible();
			if(!wasAccessible) field.setAccessible(true);
			if(!field.getName().contains("ID") && !field.getType().getName().contains("Set")) {
				try {// The value of the field may be null
					if(!(model.getClass().getDeclaredField(field.getName()).get(model) == null));
					else if(!field.get(this).equals(model.getClass().getDeclaredField(field.getName()).get(model))) {
						if(!wasAccessible) field.setAccessible(false);
						return false;
					};
				}catch(NullPointerException e) {//Throws NullPointerException, when a field in the containing class contains null
					if(model.getClass().getDeclaredField(field.getName()).get(model) != null) return false;
					//Both values are null we can go on
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

		for (Field field : this.getClass().getDeclaredFields()) {
			boolean wasAccessible= field.isAccessible();
			if(!wasAccessible) field.setAccessible(true);
			if(!field.getName().contains("ID") && !field.getType().getName().contains("Set")) {
				try {// The value of the field may be null
					if(!field.get(this).equals(model.getClass().getDeclaredField(field.getName()).get(model))) {
						if(! (model.getClass().getDeclaredField(field.getName()).get(model) == null)){
						if(!wasAccessible) field.setAccessible(false);
						return false;
						}
					};
				}catch(NullPointerException e) {//Throws NullPointerException, when a field in the containing class contains null
					//Both values are null we can go on
				}
			}
			if(!wasAccessible) field.setAccessible(false);


		}
		return true;
	}

}