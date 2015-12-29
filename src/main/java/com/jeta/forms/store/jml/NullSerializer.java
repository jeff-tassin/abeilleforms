package com.jeta.forms.store.jml;

import com.jeta.forms.store.jml.dom.JMLDocument;
import com.jeta.forms.store.jml.dom.JMLNode;

public class NullSerializer implements JMLSerializer {

	public JMLNode serialize(JMLDocument document, Object obj) throws JMLException {
		return JMLUtils.createObjectNode(document, null);
	}

}
