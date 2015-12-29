package com.jeta.forms.store.jml;

import com.jeta.forms.store.jml.dom.JMLDocument;
import com.jeta.forms.store.jml.dom.JMLNode;

/**
 * This class is used to serializer Java primitive object types to JML node.
 * 
 * @author Jeff Tassin
 */
public class PrimitiveSerializer implements JMLSerializer {

	public JMLNode serialize(JMLDocument document, Object obj) throws JMLException {

		if (obj instanceof PrimitiveHolder)
			obj = ((PrimitiveHolder) obj).getPrimitive();

		if (obj instanceof Boolean || obj instanceof Byte || obj instanceof Character || obj instanceof Short || obj instanceof Integer || obj instanceof Long
				|| obj instanceof Float || obj instanceof Double || obj == null) {

			String className = obj.getClass().getName();
			className = className.substring("java.lang.".length());
			JMLNode objnode = JMLUtils.createObjectNode2(document, className);
			objnode.setAttribute("value", obj == null ? "" : obj.toString());
			return objnode;
		}
		throw new JMLException("PrimitiveSerializer.serialize non primitive object encountered: " + obj.getClass());
	}

}
