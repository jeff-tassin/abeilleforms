package com.jeta.forms.store.jml;

import java.util.Iterator;
import java.util.List;

import com.jeta.forms.store.jml.dom.JMLDocument;
import com.jeta.forms.store.jml.dom.JMLNode;

public class ListSerializer implements JMLSerializer {

	/**
	 * The list class we are serializaing (e.g. LinkedList or ArrayList )
	 */
	private Class m_listClass;

	/**
	 * ctor
	 * 
	 * @param listClass
	 */
	public ListSerializer(Class listClass) {
		m_listClass = listClass;
	}

	/**
	 * XMLDeserializer implementation <object classname="java.util.LinkedList or
	 * java.util.ArrayList"> <item> <property name="value"><object>,,,</object></property>
	 * </item> ... <item> </item> </object>
	 */
	public JMLNode serialize(JMLDocument document, Object obj) throws JMLException {

		JMLUtils.verifyObjectType(obj, m_listClass);

		JMLNode hashNode = JMLUtils.createObjectNode(document, obj);
		List list = (List) obj;

		if (list != null) {
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				Object item = iter.next();
				JMLNode itemnode = document.createNode("item");
				itemnode.appendChild(JMLUtils.createPropertyNode(document, "value", JMLUtils.getPrimitiveHolder(item)));
				hashNode.appendChild(itemnode);
			}
		}
		return hashNode;
	}
}
