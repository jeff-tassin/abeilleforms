package com.jeta.forms.store.xml.parser;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.xml.sax.SAXException;

public class CustomSAXException extends SAXException {

	private String m_stackTrace;

	public CustomSAXException(String msg, String stackTrace) {
		// TODO Auto-generated constructor stub
		super(msg);
		m_stackTrace = stackTrace;
	}

	public static CustomSAXException create(String msg, String stackTrace) {
		return new CustomSAXException(msg, stackTrace);
	}

	public void printStackTrace(PrintStream s) {
		if (m_stackTrace == null)
			super.printStackTrace(s);
		else
			s.print(m_stackTrace);
	}

	public void printStackTrace(PrintWriter s) {
		if (m_stackTrace == null)
			super.printStackTrace(s);
		else
			s.print(m_stackTrace);

	}

}
