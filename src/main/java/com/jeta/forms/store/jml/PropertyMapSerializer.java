package com.jeta.forms.store.jml;

import java.util.Iterator;

import com.jeta.forms.store.jml.dom.JMLDocument;
import com.jeta.forms.store.jml.dom.JMLNode;
import com.jeta.forms.store.support.PropertyMap;

public class PropertyMapSerializer implements JMLSerializer {

	/**
	 * XMLDeserializer implementation <object> <at name="keyname1">value1</at>
	 * <at name="keyname2">value2</at> <at name="keyname3">value3</at>
	 * </object>
	 */
	public JMLNode serialize(JMLDocument document, Object obj) throws JMLException {
		JMLNode hashNode = JMLUtils.createObjectNode(document, obj);
		PropertyMap pmap = (PropertyMap) obj;

		if (pmap != null) {
			Iterator iter = pmap.keySet().iterator();
			while (iter.hasNext()) {
				Object key = iter.next();
				Object value = pmap.get(key);
				JMLNode propnode = null;
				if (JMLUtils.isPrimitive(value))
					propnode = JMLUtils.createPropertyNode(document, key.toString(), value.toString());
				else
					propnode = JMLUtils.createPropertyNode(document, key.toString(), value);

				hashNode.appendChild(propnode);
			}
		}
		return hashNode;
	}

}
