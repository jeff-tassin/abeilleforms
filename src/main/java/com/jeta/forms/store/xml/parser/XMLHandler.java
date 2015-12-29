package com.jeta.forms.store.xml.parser;

import org.xml.sax.SAXException;

public interface XMLHandler {
	public void characters(char[] ch, int start, int length) throws SAXException;

	public void startElement(XMLNodeContext ctx) throws SAXException;

	public void endElement(XMLNodeContext ctx) throws SAXException;
}
