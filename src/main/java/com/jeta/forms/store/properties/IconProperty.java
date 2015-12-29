/*
 * Copyright (c) 2004 JETA Software, Inc.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JETA Software nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeta.forms.store.properties;

import java.awt.Component;
import java.awt.Graphics;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.project.ProjectManager;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.open.registry.JETARegistry;

/**
 * A class for handling handling icon properties in a Java bean. All icons are
 * loaded from the CLASSPATH. In the designer, the user specifies the package
 * and filename that contains the icon resource for a given Java bean icon
 * property. This object is responsible for serializing the path to the icon
 * resource as well as loading the icon from the CLASSPATH at runtime.
 * 
 * @author Jeff Tassin
 */
public class IconProperty extends JETAProperty implements Icon {

	static final long serialVersionUID = -7743120720855547949L;

	/**
	 * The version of this class.
	 */
	public static final int VERSION = 1;

	/**
	 * Flag that indicates if the image is stored in the form file or referenced
	 * from the CLASSPATH This is currently not used. All images must be
	 * referenced from the CLASSPATH.
	 */
	private boolean m_embedded = false;

	/**
	 * If the image is loaded from the CLASSPATH, this is the package/name where
	 * the image is stored relative to the CLASSPATH
	 */
	private String m_path;

	/**
	 * The actual image.
	 */
	private transient ImageIcon m_image;

	/**
	 * A description for the image. This is useful if the image is embedded.
	 */
	private String m_description;

	/**
	 * Currently not used.
	 */
	private int[] m_pixels;

	/**
	 * Creates an unitialized <code>IconProperty</code> instance.
	 */
	public IconProperty() {
	}

	/**
	 * @return the description for this property
	 */
	public String getDescription() {
		return m_description;
	}

	/**
	 * Returns the underlying image icon.
	 * 
	 * @return the underlying image icon;
	 */
	public ImageIcon imageIcon() {
		return m_image;
	}

	/**
	 * Returns the width of the icon in pixels.
	 * 
	 * @return the width of the image in pixels
	 */
	public int getIconWidth() {
		return m_image == null ? 0 : m_image.getIconWidth();
	}

	/**
	 * Returns the height of the icon in pixels.
	 * 
	 * @return the height of the image in pixels
	 */
	public int getIconHeight() {
		return m_image == null ? 0 : m_image.getIconHeight();
	}

	/**
	 * Returns the path relative to the current CLASSPATH that contains the
	 * icon.
	 * 
	 * @return the relative path
	 */
	public String getRelativePath() {
		return m_path;
	}

	/**
	 * Returns true if this image is embedded. Currently this should always
	 * return false.
	 * 
	 * @return true if this image is embedded
	 */
	public boolean isEmbedded() {
		return m_embedded;
	}

	/**
	 * Loads the image from the project manager and caches the resulting
	 * ImageIcon.
	 */
	public void loadImage() {
		try {
			ProjectManager pmgr = (ProjectManager) JETARegistry.lookup(ProjectManager.COMPONENT_ID);
			if (pmgr != null && m_path != null && m_path.length() > 0) {
				m_image = pmgr.loadImage(m_path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Icon implementation. Renders the icon specified by this property on the
	 * given graphics context.
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (m_image != null)
			m_image.paintIcon(c, g, x, y);
	}

	/**
	 * Prints this value to the console
	 */
	public void print() {
		System.out.println("IconProperty..................");
		System.out.println("  embedded: " + isEmbedded());
		System.out.println("  description: " + getDescription());
		System.out.println("  path: " + getRelativePath());
		System.out.println("  width: " + getIconWidth());
		System.out.println("  height: " + getIconHeight());
	}

	/**
	 * Sets the flag that indicates if this image is embedded or not Not
	 * currently used. Embedded icons are not currently supported.
	 */
	public void setEmbedded(boolean bembedded) {
		m_embedded = false;
	}

	/**
	 * Sets the description for this icon. This is only useful for embedded
	 * images.
	 * 
	 * @param desc
	 *            the description for this icon.
	 */
	public void setDescription(String desc) {
		m_description = desc;
	}

	/**
	 * Sets the relative path where the icon file is located. This path is
	 * relative to the classpath.
	 * 
	 * @param path
	 *            the relative path to the icon resource.
	 */
	public void setRelativePath(String path) {
		m_path = path;
		m_image = null;
		loadImage();

		if (path != null) {
			int pos = path.lastIndexOf('\\');
			pos = Math.max(pos, path.lastIndexOf('/'));
			if (pos > 0) {
				m_description = path.substring(pos + 1, path.length());
			}
			else {
				m_description = path;
			}
		}
	}

	/**
	 * Sets this property to that of another IconProperty.
	 */
	public void setValue(Object prop) {
		if (prop == this)
			return;

		if (prop instanceof IconProperty) {
			IconProperty iprop = (IconProperty) prop;
			m_embedded = false;
			m_path = iprop.m_path;
			m_image = iprop.m_image;
			m_description = iprop.m_description;

			if (!m_embedded && m_image == null) {
				loadImage();
			}
		}
		else if (prop == null) {
			m_path = "";
			m_image = null;
			m_description = "";
		}
		else {
			assert (false);
		}
	}

	/**
	 * Updates the bean. No op for this property.
	 */
	public void updateBean(JETABean jbean) {

	}

	/**
	 * Externalizable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		m_embedded = in.readBoolean("embedded");
		m_path = in.readString("path");
		if (FormUtils.isDesignMode()) {
			m_path = FormUtils.fixPath(m_path);
		}

		m_description = in.readString("description");
		int w = in.readInt("width");
		int h = in.readInt("height");

		m_embedded = false;
		loadImage();

	}

	/**
	 * Externalizable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(JETAProperty.class));
		out.writeVersion(VERSION);
		out.writeBoolean("embedded", m_embedded);
		out.writeObject("path", m_path);
		out.writeObject("description", m_description);

		int width = getIconWidth();
		int height = getIconHeight();

		out.writeInt("width", width);
		out.writeInt("height", height);

	}

}
