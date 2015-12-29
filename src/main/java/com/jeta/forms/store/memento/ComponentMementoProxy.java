package com.jeta.forms.store.memento;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class is mainly needed when copying a form to the clipboard. The Swing
 * copy/paste implementation serializes the data during the paste operation
 * rather than the copy. If the user switches editors, this can cause problems
 * with the form containment hierarhcy and it won't correctly save its state.
 * So, we use this class to force the form to save to a byte array when the copy
 * command is invoked. Since the form must have focus at that point, the state
 * is correctly saved.
 * 
 * @author Jeff Tassin
 */
public class ComponentMementoProxy implements Externalizable {
	public static final int VERSION = 1;

	static final long serialVersionUID = -6311643735573896684L;

	/**
	 * We must store the component memento as a byte array. If we stored as a
	 * ComponentMemento, the paste operation would attempt to re-serialize the
	 * memento. This causes problems if pasting from one form editor to a
	 * different form editor since the copied forms don't have valid gridview
	 * parents once they are deactivated.
	 */
	private byte[] m_component_memento;

	/**
	 * Cached value of ComponentMemento once we deserialize from the byte array.
	 */
	private ComponentMemento m_cm;

	/**
	 * Default ctor
	 */
	public ComponentMementoProxy() {

	}

	public ComponentMementoProxy(ComponentMemento cm) {
		setComponentMemento(cm);
	}

	public ComponentMemento getComponentMemento() {
		try {
			if (m_cm == null) {
				ByteArrayInputStream bis = new ByteArrayInputStream(m_component_memento);
				ObjectInputStream ois = new ObjectInputStream(bis);
				m_cm = (ComponentMemento) ois.readObject();
				System.out.println("componentransferable  getCOmponentMemento... ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m_cm;
	}

	void setComponentMemento(ComponentMemento cm) {
		try {
			m_cm = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(cm);
			oos.close();
			m_component_memento = bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			m_cm = cm;
		}
	}

	/**
	 * Externalizable Implementation
	 */
	public void readExternal(java.io.ObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readInt();
		m_component_memento = (byte[]) in.readObject();
		m_cm = (ComponentMemento) in.readObject();
	}

	/**
	 * Externalizable Implementation
	 */
	public void writeExternal(java.io.ObjectOutput out) throws IOException {
		out.writeInt(VERSION);
		out.writeObject(m_component_memento);
		out.writeObject(m_cm);
	}

}
