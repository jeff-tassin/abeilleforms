package com.jeta.forms.store.jml;

import com.jeta.forms.store.jml.dom.JMLDocument;
import com.jeta.forms.store.jml.dom.JMLNode;

/**
 * Serializer for Object[] types.
 */
public class ObjectArraySerializer implements JMLSerializer {

	/**
	 * XMLSeserializer implementation <object classname="[Ljava.lang.Object;">
	 * <at name="item"> <object>,,,</object> </at> <at name="item"> <object>,,,</object>
	 * </at> </object>
	 */
	public JMLNode serialize(JMLDocument document, Object obj) throws JMLException {

		JMLUtils.verifyObjectType(obj, Object[].class);

		JMLNode node = JMLUtils.createObjectNode(document, obj);
		Object[] oa = (Object[]) obj;

		if (oa != null) {
			for (int index = 0; index < oa.length; index++) {
				Object item = oa[index];
				if (item != null) {
					JMLNode itemNode = JMLUtils.createPropertyNode(document, "item", JMLUtils.getPrimitiveHolder(item));
					itemNode.setAttribute("index", String.valueOf(index));
					node.appendChild(itemNode);
				}
			}
			node.setAttribute("size", String.valueOf(oa.length));
		}
		return node;
	}

}
