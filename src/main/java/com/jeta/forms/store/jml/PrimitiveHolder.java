package com.jeta.forms.store.jml;

public class PrimitiveHolder {
	private Object m_value;

	public PrimitiveHolder() {
	}

	public PrimitiveHolder(Object value) {
		m_value = value;
	}

	public Object getPrimitive() {
		return m_value;
	}

	public String getPrimitiveClassName() {
		return m_value == null ? "null" : m_value.getClass().getName();
	}

}
