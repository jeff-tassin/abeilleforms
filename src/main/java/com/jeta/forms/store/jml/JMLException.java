package com.jeta.forms.store.jml;

public class JMLException extends Exception {

	public JMLException(Exception cause) {
		super(cause);
	}

	public JMLException(String msg, Exception cause) {
		super(msg, cause);
	}

	public JMLException(String msg) {
		super(msg);
	}
}
