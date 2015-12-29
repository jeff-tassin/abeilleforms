package com.jeta.forms.store.xml.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MainHandler extends DefaultHandler {

	private XMLNodeContext m_ctx = new XMLNodeContext();

	private XMLHandler m_tophandler;

	public void characters(char[] ch, int start, int length) throws SAXException {
		// System.out.println( "MainHandler.characters: " + new String( ch,
		// start, length ) );
		m_ctx.getCurrentHandler().characters(ch, start, length);
	}

	public void startElement(String uri, String localName, String qName, Attributes attribs) throws SAXException {
		try {
			m_ctx.set(uri, qName, attribs);
			if (m_tophandler == null) {
				if ("object".equals(m_ctx.getQualifiedName())) {
					m_tophandler = XMLHandlerFactory.getInstance().getHandler(attribs.getValue("classname"));
					assert (m_tophandler != null);
					m_tophandler.startElement(m_ctx);
				}
				else {
					throw new SAXException("Invalid tag.  Expecting <object classname=\"...\">.  Got instead: " + m_ctx.getQualifiedName());
				}
			}
			else {
				m_ctx.getCurrentHandler().startElement(m_ctx);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof SAXException) {
				throw (SAXException) e;
			}
			else {
				throw new SAXException(e.getMessage(), e);
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		m_ctx.set(uri, qName, null);
		m_ctx.getCurrentHandler().endElement(m_ctx);
		if (m_ctx.getCurrentHandler() == null) {
			// we are done
			if ("object".equalsIgnoreCase(qName)) {

			}
			else {
				throw new SAXException("Invalid tag.  Expecting <object classname=\"...\">.  Got instead: " + qName);
			}
		}
	}

	public Object getObject() {
		return ((ObjectHandler) m_tophandler).getObject();
	}
}
