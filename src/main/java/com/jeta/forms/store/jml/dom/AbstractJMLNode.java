package com.jeta.forms.store.jml.dom;

import java.util.Collection;
import java.util.LinkedHashMap;

import com.jeta.open.support.EmptyCollection;

public abstract class AbstractJMLNode implements JMLNode {

	private LinkedHashMap m_attribs;

	public void setAttribute(String attribName, String attribValue) {
		if (m_attribs == null)
			m_attribs = new LinkedHashMap();

		m_attribs.put(attribName, attribValue);
	}

	public Collection getAttributeNames() {
		if (m_attribs == null)
			return EmptyCollection.getInstance();

		return m_attribs.keySet();
	}

	public String getAttribute(String attribName) {
		return (String) m_attribs.get(attribName);
	}

}
