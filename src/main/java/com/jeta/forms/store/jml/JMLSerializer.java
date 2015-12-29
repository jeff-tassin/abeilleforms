package com.jeta.forms.store.jml;

import com.jeta.forms.store.jml.dom.JMLDocument;
import com.jeta.forms.store.jml.dom.JMLNode;

public interface JMLSerializer {
	public JMLNode serialize(JMLDocument document, Object obj) throws JMLException;
}
