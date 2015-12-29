package com.jeta.forms.store.jml.dom;

import java.util.Collection;

public interface JMLNode {

	public void setAttribute(String attribName, String attribValue);

	public void appendChild(JMLNode childNode);

	public Collection getChildren();

	public Collection getAttributeNames();

	public String getAttribute(String attribName);

	public String getNodeName();

	public int getChildCount();

	public JMLNode getNode(int index);
}
