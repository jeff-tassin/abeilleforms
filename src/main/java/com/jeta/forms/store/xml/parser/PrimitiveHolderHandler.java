package com.jeta.forms.store.xml.parser;

public class PrimitiveHolderHandler extends ObjectHandler {

	public Object getObject() {
		String classname = (String) getProperty("primitive");
		String value = (String) getProperty("value");
		if ("java.lang.Byte".equals(classname)) {
			return new Byte(value);
		}
		else if ("java.lang.Boolean".equals(classname)) {
			return new Boolean(value);
		}
		else if ("java.lang.Character".equals(classname)) {
			if (value == null || value.length() == 0)
				return new Character('\0');
			else
				return new Character(value.charAt(0));
		}
		else if ("java.lang.Short".equals(classname)) {
			return new Short(value);
		}
		else if ("java.lang.Integer".equals(classname)) {
			return new Integer(value);
		}
		else if ("java.lang.Long".equals(classname)) {
			return new Long(value);
		}
		else if ("java.lang.Float".equals(classname)) {
			return new Float(value);
		}
		else if ("java.lang.Double".equals(classname)) {
			return new Double(value);
		}
		else {
			assert (false);
			return null;
		}
	}
}
