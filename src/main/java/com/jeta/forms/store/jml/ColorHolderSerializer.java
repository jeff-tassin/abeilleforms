package com.jeta.forms.store.jml;

import com.jeta.forms.store.jml.dom.JMLDocument;
import com.jeta.forms.store.jml.dom.JMLNode;
import com.jeta.forms.store.properties.ColorHolder;

public class ColorHolderSerializer implements InlineJMLSerializer {

	public JMLNode serialize(JMLDocument document, Object obj) throws JMLException {
		JMLUtils.verifyObjectType(obj, ColorHolder.class);
		ColorHolder color = (ColorHolder) obj;
		if (color != null) {
			StringBuffer sbuff = new StringBuffer();
			sbuff.append(String.valueOf(color.getRed()));
			sbuff.append(',');
			sbuff.append(String.valueOf(color.getGreen()));
			sbuff.append(',');
			sbuff.append(String.valueOf(color.getBlue()));
			return document.createTextNode(sbuff.toString());
		}
		else {
			return document.createTextNode("");
		}
	}

	public String getObjectName() {
		return "color";
	}

}
