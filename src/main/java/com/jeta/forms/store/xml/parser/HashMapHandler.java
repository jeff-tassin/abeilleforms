package com.jeta.forms.store.xml.parser;

import java.util.HashMap;

import org.xml.sax.SAXException;

import com.jeta.forms.store.jml.dom.JMLAttributes;

public class HashMapHandler extends ObjectHandler {

	private Object m_current_key;
	private Object m_current_value;

	public void setProperty(Object keyName, Object value, JMLAttributes attribs) throws SAXException {
		if ("key".equalsIgnoreCase(keyName.toString())) {
			m_current_key = value;
		}
		else if ("value".equalsIgnoreCase(keyName.toString())) {
			m_current_value = value;
		}
	}

	/**
	 * XMLDeserializer implementation <object classname="java.util.HashMap">
	 * <item> <property name="key"><object>,,,</object></property> <property
	 * name="value"><object>,,,</object></property> </item> ... <item>
	 * </item> </object>
	 */
	public void startElement(XMLNodeContext ctx) throws SAXException {
		if ("item".equalsIgnoreCase(ctx.getQualifiedName())) {
			m_current_key = null;
			m_current_value = null;
		}
		else {
			super.startElement(ctx);
		}
	}

	public void endElement(XMLNodeContext ctx) throws SAXException {
		if ("item".equalsIgnoreCase(ctx.getQualifiedName())) {
			((HashMap) getObject()).put(m_current_key, m_current_value);
		}
		else {
			super.endElement(ctx);
		}
	}

}
