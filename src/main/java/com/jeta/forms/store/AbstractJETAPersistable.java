package com.jeta.forms.store;

import java.io.IOException;

public abstract class AbstractJETAPersistable implements JETAPersistable {

	/**
	 * Externalizable Implementation
	 */
	public void readExternal(java.io.ObjectInput in) throws ClassNotFoundException, IOException {
		if (in instanceof JETAObjectInput)
			read((JETAObjectInput) in);
		else {
			read(new JavaExternalizableObjectInput(in));
		}
	}

	/**
	 * Externalizable Implementation
	 */
	public void writeExternal(java.io.ObjectOutput out) throws IOException {
		if (out instanceof JETAObjectOutput)
			write((JETAObjectOutput) out);
		else
			write(new JavaExternalizableObjectOutput(out));
	}

}
