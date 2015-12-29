package com.jeta.forms.store.xml.parser;

import org.xml.sax.SAXException;

import com.jeta.forms.store.JETAPersistable;
import com.jeta.forms.store.jml.JMLUtils;

public class JETAPersistableHandler extends ObjectHandler {

	public void endElement(XMLNodeContext ctx) throws SAXException {
		super.endElement(ctx);
		if ("object".equalsIgnoreCase(ctx.getQualifiedName())) {
			try {
				XMLObjectInput objinput = new XMLObjectInput(this);
				((JETAPersistable) getObject()).read(objinput);
			} catch (Exception e) {
				throw JMLUtils.createSAXException(e);
			}
		}
	}
}
