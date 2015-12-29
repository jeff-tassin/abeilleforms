package com.jeta.forms.store.support;

import java.io.Serializable;
import java.util.HashMap;

/**
 * This is just a tagging interface for the PropertiesMemento.
 * 
 * Do not add any methods to this class or it can break existing forms.
 * 
 * 
 * @author Jeff Tassin
 */
public class PropertyMap extends HashMap implements Serializable {

	private static final long serialVersionUID = 135817357234113L;

	public PropertyMap() {

	}

	public PropertyMap(HashMap src) {
		super(src);
	}

}
