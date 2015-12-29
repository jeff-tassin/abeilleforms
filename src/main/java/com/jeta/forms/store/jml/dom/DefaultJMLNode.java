package com.jeta.forms.store.jml.dom;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultJMLNode extends AbstractJMLNode {

	private String m_nodeName;

	private ArrayList m_children;

	public DefaultJMLNode(String nodeName) {
		m_nodeName = nodeName;
	}

	public Collection getChildren() {
		return m_children;
	}

	public void appendChild(JMLNode childNode) {
		if (m_children == null)
			m_children = new ArrayList();
		m_children.add(childNode);
	}

	public String getNodeName() {
		return m_nodeName;
	}

	public int getChildCount() {
		if (m_children == null)
			return 0;
		return m_children.size();
	}

	public JMLNode getNode(int index) {
		if (m_children == null)
			throw new IndexOutOfBoundsException();

		return (JMLNode) m_children.get(index);
	}

}
