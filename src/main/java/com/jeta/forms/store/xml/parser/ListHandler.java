package com.jeta.forms.store.xml.parser;

import java.util.List;

import org.xml.sax.SAXException;

import com.jeta.forms.store.jml.dom.JMLAttributes;

public class ListHandler extends ObjectHandler {

	/**
	 * XMLDeserializer implementation <object classname="java.util.LinkedList or
	 * java.util.ArrayList"> <item> <property name="value"><object>,,,</object></property>
	 * </item> ... <item> </item> </object>
	 */
	public void setProperty(Object keyName, Object value, JMLAttributes attribs) throws SAXException {
		if ("value".equalsIgnoreCase(keyName.toString())) {
			((List) getObject()).add(value);
		}
	}

	public void startElement(XMLNodeContext ctx) throws SAXException {
		if ("item".equalsIgnoreCase(ctx.getQualifiedName())) {
			// ignore
		}
		else {
			super.startElement(ctx);
		}
	}

	public void endElement(XMLNodeContext ctx) throws SAXException {
		if ("item".equalsIgnoreCase(ctx.getQualifiedName())) {
			// ignore
		}
		else {
			super.endElement(ctx);
		}
	}

}
