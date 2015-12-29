package com.jeta.forms.store.jml.dom;

public interface JMLAttributes {

	/**
	 * Return the number of attributes in the list.
	 */
	public int getLength();

	/**
	 * Look up an attribute's value by XML 1.0 qualified name.
	 */
	public String getValue(String qName);
}
