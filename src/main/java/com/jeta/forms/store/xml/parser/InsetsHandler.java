package com.jeta.forms.store.xml.parser;

import java.awt.Insets;

import org.xml.sax.SAXException;

import com.jeta.forms.store.jml.dom.JMLAttributes;

public class InsetsHandler extends InlineObjectHandler {

	protected void setProperty(Object key, Object value, JMLAttributes attribs) throws SAXException {
		Insets insets = (Insets) getObject();
		if ("top".equalsIgnoreCase(key.toString())) {
			insets.top = Integer.parseInt(value.toString());
		}
		else if ("left".equalsIgnoreCase(key.toString())) {
			insets.left = Integer.parseInt(value.toString());
		}
		else if ("bottom".equalsIgnoreCase(key.toString())) {
			insets.bottom = Integer.parseInt(value.toString());
		}
		else if ("right".equalsIgnoreCase(key.toString())) {
			insets.right = Integer.parseInt(value.toString());
		}
	}

	/**
	 * 
	 */
	protected Object instantiateObject(JMLAttributes attribs, String value) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (value != null) {
			String[] tokens = value.split(",");
			return new Insets(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
		}
		else {
			return new Insets(0, 0, 0, 0);
		}
	}
}
