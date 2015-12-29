package com.jeta.forms.store.jml;

import java.util.HashMap;
import java.util.Iterator;

import com.jeta.forms.store.jml.dom.JMLDocument;
import com.jeta.forms.store.jml.dom.JMLNode;

/**
 * This class is responsible for transforming java.util.HashMap objects to/from
 * XML form.
 * 
 * @author Jeff Tassin
 */
public class HashMapSerializer implements JMLSerializer {

	/**
	 * XMLDeserializer implementation <object classname="java.util.HashMap">
	 * <property name="keyname">value</property> <property name="keyname">value</property>
	 * </item> ... <item> </item> </object>
	 */
	public JMLNode serialize(JMLDocument document, Object obj) throws JMLException {

		JMLUtils.verifyObjectType(obj, HashMap.class);

		JMLNode hashNode = JMLUtils.createObjectNode(document, obj);
		HashMap hash = (HashMap) obj;

		if (hash != null) {
			Iterator iter = hash.keySet().iterator();
			while (iter.hasNext()) {
				Object key = iter.next();
				JMLNode itemnode = document.createNode("item");
				itemnode.appendChild(JMLUtils.createPropertyNode(document, "key", JMLUtils.getPrimitiveHolder(key)));
				itemnode.appendChild(JMLUtils.createPropertyNode(document, "value", JMLUtils.getPrimitiveHolder(hash.get(key))));
				hashNode.appendChild(itemnode);
			}
		}
		return hashNode;
	}

}
