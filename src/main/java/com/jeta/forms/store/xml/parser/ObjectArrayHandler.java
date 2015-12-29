package com.jeta.forms.store.xml.parser;

import java.util.ArrayList;

import org.xml.sax.SAXException;

import com.jeta.forms.store.jml.dom.JMLAttributes;

public class ObjectArrayHandler extends ObjectHandler {

	private ArrayList m_items = new ArrayList();

	protected Object instantiateObject(JMLAttributes attribs) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String ssize = attribs.getValue("size");
		if (ssize != null) {
			int size = Integer.parseInt(ssize);
			for (int index = 0; index < size; index++) {
				m_items.add(null);
			}
		}
		return m_items;
	}

	protected void setProperty(Object key, Object value, JMLAttributes attribs) throws SAXException {
		if ("item".equalsIgnoreCase(key.toString())) {
			String index = attribs.getValue("index");
			if (index != null) {
				m_items.set(Integer.parseInt(index), value);
			}
			else {
				m_items.add(value);
			}
		}
	}

	public Object getObject() {
		return m_items.toArray();
	}

}
