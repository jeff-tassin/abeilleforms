package com.jeta.forms.store.jml.dom;

import java.util.HashMap;

public class DefaultAttributes implements JMLAttributes {

	private HashMap m_attribs;

	/**
	 * Return the number of attributes in the list.
	 */
	public int getLength() {
		return m_attribs == null ? 0 : m_attribs.size();
	}

	/**
	 * Look up an attribute's value by XML 1.0 qualified name.
	 */
	public String getValue(String qName) {
		return m_attribs == null ? null : (String) m_attribs.get(qName);
	}

	public void setAttribute(String qname, String value) {
		if (m_attribs == null)
			m_attribs = new HashMap();
		m_attribs.put(qname, value);
	}

}
