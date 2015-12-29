package com.jeta.forms.store.jml;

import java.awt.Insets;

import com.jeta.forms.store.jml.dom.JMLDocument;
import com.jeta.forms.store.jml.dom.JMLNode;

public class InsetsSerializer implements InlineJMLSerializer {

	public JMLNode serialize(JMLDocument document, Object obj) throws JMLException {
		JMLUtils.verifyObjectType(obj, Insets.class);
		Insets insets = (Insets) obj;
		if (insets != null) {
			StringBuffer sbuff = new StringBuffer();
			sbuff.append(String.valueOf(insets.top));
			sbuff.append(',');
			sbuff.append(String.valueOf(insets.left));
			sbuff.append(',');
			sbuff.append(String.valueOf(insets.bottom));
			sbuff.append(',');
			sbuff.append(String.valueOf(insets.right));
			return document.createTextNode(sbuff.toString());
		}
		else {
			return document.createTextNode("");
		}
	}

	public String getObjectName() {
		return "insets";
	}

}
