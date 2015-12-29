package com.jeta.forms.store.jml;

import com.jeta.forms.store.jml.dom.JMLDocument;
import com.jeta.forms.store.jml.dom.JMLNode;

/**
 * This class is used to store Java primitive types.
 * 
 * @deprecated Use PrimitiveSerializer instead.
 * @author Jeff Tassin
 */
public class PrimitiveHolderSerializer implements JMLSerializer {

	public JMLNode serialize(JMLDocument document, Object obj) throws JMLException {
		JMLUtils.verifyObjectType(obj, PrimitiveHolder.class);

		JMLNode objnode = JMLUtils.createObjectNode(document, obj);
		PrimitiveHolder prim = (PrimitiveHolder) obj;

		if (prim != null) {
			objnode.appendChild(JMLUtils.createPropertyNode(document, "primitive", prim.getPrimitiveClassName()));
			objnode.appendChild(JMLUtils.createPropertyNode(document, "value", String.valueOf(prim.getPrimitive())));
		}
		return objnode;
	}

}
