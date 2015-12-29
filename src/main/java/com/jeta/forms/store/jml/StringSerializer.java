package com.jeta.forms.store.jml;

import com.jeta.forms.store.jml.dom.JMLDocument;
import com.jeta.forms.store.jml.dom.JMLNode;

public class StringSerializer implements JMLSerializer {

	public JMLNode serialize(JMLDocument document, Object obj) throws JMLException {
		if (obj == null)
			return document.createTextNode("");
		else
			return document.createTextNode(obj.toString());
	}

}
