package com.jeta.forms.store.jml.dom;

import java.util.Collection;

import com.jeta.open.support.EmptyCollection;

public class TextJMLNode extends AbstractJMLNode {

	private String m_textValue;

	public TextJMLNode(String textValue) {
		m_textValue = textValue;
	}

	public void appendChild(JMLNode childNode) {
		// ignore
		assert (false);
	}

	public String getTextValue() {
		return m_textValue;
	}

	public String getNodeName() {
		return "#text";
	}

	public Collection getChildren() {
		return EmptyCollection.getInstance();
	}

	public int getChildCount() {
		return 0;
	}

	public JMLNode getNode(int index) {
		throw new IndexOutOfBoundsException();
	}

}
