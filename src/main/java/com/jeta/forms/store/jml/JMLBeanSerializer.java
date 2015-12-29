package com.jeta.forms.store.jml;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import com.jeta.forms.store.jml.dom.JMLDocument;
import com.jeta.forms.store.jml.dom.JMLNode;

public class JMLBeanSerializer implements JMLSerializer {

	/**
	 * XMLDeserializer implementation <object classname="classname">
	 * <properties> <property name="propname"> </property> </properties>
	 * </object>
	 */
	public JMLNode serialize(JMLDocument document, Object obj) throws JMLException {

		try {
			// XMLNode objnode = XMLUtils.createObjectNode2(document,
			// obj.getClass().getName() );
			JMLNode objnode = JMLUtils.createObjectNode(document, obj.getClass().getName());
			if (obj != null) {
				JMLNode propsnode = JMLUtils.createPropertiesNode(document);
				objnode.appendChild(propsnode);

				BeanInfo info = Introspector.getBeanInfo(obj.getClass());
				PropertyDescriptor[] props = info.getPropertyDescriptors();
				Object[] params = new Object[0];
				for (int index = 0; index < props.length; index++) {
					String pname = "";
					try {
						PropertyDescriptor prop = props[index];
						pname = prop.getName();
						Method m = prop.getReadMethod();
						Object pvalue = m.invoke(obj, params);
						System.out.println("XMLBeanSerializer:  pname: " + pname + "   pvalue: " + pvalue);
						JMLNode pnode = JMLUtils.createPropertyNode(document, pname, pvalue);
						if (pnode != null)
							propsnode.appendChild(pnode);
					} catch (Exception e) {
						System.out.println(e.getMessage() + ".  Unable to get property: " + pname + " for bean: " + obj.getClass());
					}
				}
			}
			return objnode;
		} catch (Exception e) {
			throw new JMLException(e.getMessage());
		}
	}

}
