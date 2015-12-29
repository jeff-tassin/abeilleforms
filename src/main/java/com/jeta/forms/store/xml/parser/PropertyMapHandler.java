package com.jeta.forms.store.xml.parser;

import org.xml.sax.SAXException;

import com.jeta.forms.store.jml.dom.JMLAttributes;
import com.jeta.forms.store.support.PropertyMap;

public class PropertyMapHandler extends ObjectHandler {

	/**
	 * XMLDeserializer implementation <object> <at name="keyname1">value1</at>
	 * <at name="keyname2">value2</at> <at name="keyname3">value3</at>
	 * </object>
	 */
	public void setProperty(Object keyName, Object value, JMLAttributes attribs) throws SAXException {
		((PropertyMap) getObject()).put(keyName, value);
	}

}
