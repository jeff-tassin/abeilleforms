package com.jeta.forms.store.jml;

import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.jeta.forms.store.JETAPersistable;
import com.jeta.forms.store.properties.ColorHolder;
import com.jeta.forms.store.support.PropertyMap;

public class JMLSerializerFactory {

	public static final String COMPONENT_ID = null;

	private HashMap m_serializers = new HashMap();

	private JMLSerializer m_persistable_serializer = new JMLObjectOutput.XMLObjectOutputSerializer();
	private JMLSerializer m_null_serializer = new NullSerializer();

	private JMLSerializerFactory() {
		initialize();
	}

	public static JMLSerializerFactory getInstance() {
		return new JMLSerializerFactory();
	}

	private void registerSerializer(Class c, JMLSerializer serializer) {
		m_serializers.put(c, serializer);
	}

	private void initialize() {
		registerSerializer(PropertyMap.class, new PropertyMapSerializer());
		registerSerializer(HashMap.class, new HashMapSerializer());
		registerSerializer(LinkedList.class, new ListSerializer(LinkedList.class));
		registerSerializer(ArrayList.class, new ListSerializer(ArrayList.class));
		registerSerializer(Boolean.class, new PrimitiveSerializer());
		registerSerializer(Byte.class, new PrimitiveSerializer());
		registerSerializer(Character.class, new PrimitiveSerializer());
		registerSerializer(Short.class, new PrimitiveSerializer());
		registerSerializer(Integer.class, new PrimitiveSerializer());
		registerSerializer(Long.class, new PrimitiveSerializer());
		registerSerializer(Float.class, new PrimitiveSerializer());
		registerSerializer(Double.class, new PrimitiveSerializer());
		registerSerializer(String.class, new StringSerializer());
		registerSerializer(Insets.class, new InsetsSerializer());
		registerSerializer(ColorHolder.class, new ColorHolderSerializer());
		registerSerializer(Object[].class, new ObjectArraySerializer());
		registerSerializer(PrimitiveHolder.class, new PrimitiveSerializer());
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public JMLSerializer createSerializer(Object obj) {
		JMLSerializer result = null;
		if (obj == null) {
			result = m_null_serializer;
		}
		else {
			result = (JMLSerializer) m_serializers.get(obj.getClass());
			if (result == null && obj instanceof JETAPersistable) {
				result = m_persistable_serializer;
			}
		}

		if (result == null) {
			System.out.println("JMLSerializerFactory.createSerializer failed: " + obj.getClass());
			assert (false);
		}

		return result;
	}

}
