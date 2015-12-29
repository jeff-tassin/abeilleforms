/*
 * Copyright (C) 2005 Jeff Tassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeta.swingbuilder.store;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Externalizable;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.jeta.forms.store.memento.IconMemento;

/**
 * Represents an imported java bean used in the builder.
 * 
 * @author Jeff Tassin
 */
public class ImportedBeanInfo implements Externalizable, RegisteredBean, Comparable, Cloneable {
	static final long serialVersionUID = -6517746440901771760L;

	public static final int VERSION = 2;

	/**
	 * The class name of the imported java bean
	 */
	private String m_bean_name;

	/**
	 * Used to store the icon for this bean
	 */
	private IconMemento m_icon_memento;

	/**
	 * Flag that indicates if this bean is scrollable
	 */
	private boolean m_scrollable;

	/**
	 * This is a scaled version of the m_icon_memento icon. It is scaled to
	 * 16x16 pixels if the icon_memento is not exactly 16x16. We do this so it
	 * will fit on the toolbar.
	 */
	private transient ImageIcon m_scaled_icon;

	/**
	 * ctor - for serialization
	 */
	public ImportedBeanInfo() {

	}

	/**
	 * ctor
	 */
	public ImportedBeanInfo(String beanName, boolean scrollable) {
		m_bean_name = beanName;
		m_scrollable = scrollable;
	}

	/**
	 * @return the icon
	 */
	public Icon getIcon() {
		Icon result = null;
		if (m_scaled_icon == null) {
			if (m_icon_memento != null) {
				ImageIcon ii = m_icon_memento.getImageIcon();
				if (ii != null) {
					if ((ii.getIconWidth() == 16) && (ii.getIconHeight() == 16)) {
						m_scaled_icon = ii;
					}
					else {
						// we need to scale the image to fit 16x16 pixels if it
						// is too big or too small
						BufferedImage bimage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
						Graphics2D bg = bimage.createGraphics();
						bg.drawImage(ii.getImage(), 0, 0, 16, 16, null);
						bg.dispose();
						m_scaled_icon = new ImageIcon(bimage);
					}
				}
			}
		}
		return m_scaled_icon;
	}

	public String getDescription() {
		return getBeanName();
	}

	public String getClassName() {
		return getBeanName();
	}

	/**
	 * @return the class name of the imported java bean
	 */
	public String getBeanName() {
		return m_bean_name;
	}

	/**
	 * @return true if this component is scrollable
	 */
	public boolean isScrollable() {
		return m_scrollable;
	}

	/**
	 * Sets the class name of the imported java bean
	 */
	public void setBeanName(String name) {
		m_bean_name = name;
	}

	/**
	 * Sets the icon memento for this object
	 */
	public void setIconMemento(IconMemento im) {
		m_icon_memento = im;
		m_scaled_icon = null;
	}

	/**
	 */
	public void setScrollable(boolean scrollable) {
		m_scrollable = scrollable;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (null == obj)
			return false;

		if (!(obj instanceof ImportedBeanInfo))
			return false;

		ImportedBeanInfo mObj = (ImportedBeanInfo) obj;

		if ((m_scrollable == mObj.m_scrollable)
				&& ((null == m_bean_name && null == mObj.m_bean_name) || (m_bean_name != null && m_bean_name.equals(mObj.m_bean_name)) || (mObj.m_bean_name != null && mObj.m_bean_name
						.equals(m_bean_name)))
				&& ((null == m_icon_memento && null == mObj.m_icon_memento) || (m_icon_memento != null && m_icon_memento.equals(mObj.m_icon_memento)) || (mObj.m_icon_memento != null && mObj.m_icon_memento
						.equals(m_icon_memento)))) {
			return true;
		}
		else {
			return false;
		}
	}

	public int compareTo(Object o) throws ClassCastException {
		if (!(o instanceof ImportedBeanInfo))
			throw new ClassCastException("An ImportedBeanInfo object was expected.");
		ImportedBeanInfo beanInfo = (ImportedBeanInfo) o;
		return this.m_bean_name.compareTo(beanInfo.getBeanName());
	}

	public Object clone() {
		ImportedBeanInfo other = null;
		try {
			other = (ImportedBeanInfo) super.clone();
			//
			other.m_bean_name = m_bean_name;
			other.m_scrollable = m_scrollable;
			//
			m_scaled_icon = null;
			getIcon();
			other.m_scaled_icon = m_scaled_icon;
			m_scaled_icon = null;
			getIcon();
			//
			other.m_icon_memento = (m_icon_memento != null ? (IconMemento) m_icon_memento.clone() : (IconMemento) null);
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
		//
		return other;
	}

	/**
	 * Externalizable Implementation
	 */
	public void readExternal(java.io.ObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readInt();
		m_bean_name = (String) in.readObject();
		m_icon_memento = (IconMemento) in.readObject();
		if (version >= 2)
			m_scrollable = in.readBoolean();
	}

	/**
	 * Externalizable Implementation
	 */
	public void writeExternal(java.io.ObjectOutput out) throws IOException {
		out.writeInt(VERSION);
		out.writeObject(m_bean_name);
		out.writeObject(m_icon_memento);
		out.writeBoolean(m_scrollable);
	}
}
