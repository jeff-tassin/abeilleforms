package com.jeta.forms.store.xml.parser;

public class PrimitiveHolderHandler extends ObjectHandler {

	public Object getObject() {
		String classname = (String) getProperty("primitive");
		String value = (String) getProperty("value");
		if ("java.lang.Byte".equals(classname)) {
			return value;
		}
		else if ("java.lang.Boolean".equals(classname)) {
			return value;
		}
		else if ("java.lang.Character".equals(classname)) {
			if (value == null || value.length() == 0)
				return '\0';
			else
				return value.charAt(0);
		}
		else if ("java.lang.Short".equals(classname)) {
			return value;
		}
		else if ("java.lang.Integer".equals(classname)) {
			return value;
		}
		else if ("java.lang.Long".equals(classname)) {
			return value;
		}
		else if ("java.lang.Float".equals(classname)) {
			return value;
		}
		else if ("java.lang.Double".equals(classname)) {
			return value;
		}
		else {
			assert (false);
			return null;
		}
	}
}
