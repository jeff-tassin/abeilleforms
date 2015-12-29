package com.jeta.forms.store.xml.parser;

import com.jeta.forms.store.jml.dom.JMLAttributes;

/**
 * Handler for objects that can store their representation as a single string.
 * These type of objects are stored as direct child nodes (text) of the
 * associated property.
 * 
 * For example, we can store color objects this way:
 * 
 * <at name="foreground" object="color">0,255,255</at>
 * 
 * @author Jeff Tassin
 */
public abstract class InlineObjectHandler extends ObjectHandler {

	/**
	 * ObjectHandler ipmlementation
	 */
	protected Object instantiateObject(JMLAttributes attribs) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		throw new InstantiationException("This method should not be called for InlineObjectHandlers.  Call instantiateObject(String prop) instead.");
	}

	protected abstract Object instantiateObject(JMLAttributes attribs, String value) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException;

}
