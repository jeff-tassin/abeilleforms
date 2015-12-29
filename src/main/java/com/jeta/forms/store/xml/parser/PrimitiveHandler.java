package com.jeta.forms.store.xml.parser;

import com.jeta.forms.store.jml.dom.JMLAttributes;

public class PrimitiveHandler extends ObjectHandler {

	protected Object instantiateObject(JMLAttributes attribs) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String className = attribs.getValue("classname");
		String value = attribs.getValue("value");

		if ("Boolean".equalsIgnoreCase(className) || ("java.lang.Boolean").equalsIgnoreCase(className)) {
			return Boolean.valueOf(value);
		}
		else if ("Byte".equalsIgnoreCase(className) || ("java.lang.Byte").equalsIgnoreCase(className)) {
			return Byte.valueOf(value);
		}
		else if ("Character".equalsIgnoreCase(className) || ("java.lang.Character").equalsIgnoreCase(className)) {
			return new Character(value.charAt(0));
		}
		else if ("Short".equalsIgnoreCase(className) || ("java.lang.Short").equalsIgnoreCase(className)) {
			return Short.valueOf(value);
		}
		else if ("Integer".equalsIgnoreCase(className) || ("java.lang.Integer").equalsIgnoreCase(className)) {
			return Integer.valueOf(value);
		}
		else if ("Long".equalsIgnoreCase(className) || ("java.lang.Long").equalsIgnoreCase(className)) {
			return Long.valueOf(value);
		}
		else if ("Float".equalsIgnoreCase(className) || ("java.lang.Float").equalsIgnoreCase(className)) {
			return Float.valueOf(value);
		}
		else if ("Double".equalsIgnoreCase(className) || ("java.lang.Double").equalsIgnoreCase(className)) {
			return Double.valueOf(value);
		}
		else {
			throw new InstantiationException("PrimitiveHandler found invalid classname: " + className);
		}
	}

}
