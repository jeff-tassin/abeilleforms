package com.jeta.forms.store.jml.dom;

public class DefaultXMLDocument implements JMLDocument {

	public JMLNode createNode(String nodeName) {
		return new DefaultJMLNode(nodeName);
	}

	public JMLNode createTextNode(String nodeValue) {
		return new TextJMLNode(nodeValue);
	}

}
