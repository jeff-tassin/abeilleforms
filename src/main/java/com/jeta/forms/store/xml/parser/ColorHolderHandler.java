package com.jeta.forms.store.xml.parser;

import java.awt.Color;

import com.jeta.forms.store.jml.dom.JMLAttributes;
import com.jeta.forms.store.properties.ColorHolder;

public class ColorHolderHandler extends InlineObjectHandler {

	/**
	 * Create object from String value. e.g. <at name="foreground"
	 * object="color">0,255,255</at>
	 */
	protected Object instantiateObject(JMLAttributes attribs, String value) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (value != null) {
			String[] tokens = value.split(",");
			return new ColorHolder(new Color(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])));
		}
		else {
			return null;
		}
	}
}
