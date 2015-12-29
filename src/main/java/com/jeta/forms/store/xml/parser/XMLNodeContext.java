package com.jeta.forms.store.xml.parser;

import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class XMLNodeContext {
	private LinkedList m_stack = new LinkedList();

	private String m_uri;
	private String m_qName;
	private Attributes m_attribs;

	public Attributes getAttributes() {
		return m_attribs;
	}

	public XMLHandler getCurrentHandler() {
		if (m_stack.size() > 0)
			return (XMLHandler) m_stack.getFirst();
		else
			return null;
	}

	public void push(XMLHandler handler) throws SAXException {
		if (m_stack.contains(handler)) {
			throw new SAXException("XMLNodeContext invalid state.  Handler already on call stack: " + handler);
		}
		m_stack.addFirst(handler);
	}

	public void pop(XMLHandler handler) throws SAXException {
		if (m_stack.size() == 0) {
			throw new SAXException("XMLNodeContext invalid state.  Tried to pop from empty stack.");
		}
		Object obj = m_stack.removeFirst();
		if (handler != null && obj != handler) {
			throw new SAXException("XMLNodeContext invalid state.  Expected to find the specified handler on the call stack: " + handler
					+ "\n.But found instead: " + obj);
		}
	}

	public String getQualifiedName() {
		return m_qName;
	}

	public void set(String uri, String qName, Attributes attribs) {
		m_uri = uri;
		m_qName = qName;
		m_attribs = attribs;
	}

}
