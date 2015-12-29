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

package com.jeta.forms.store.memento;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.ImageIcon;

import com.jeta.forms.store.AbstractJETAPersistable;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * Represents a storable icon. This class takes the raw bytes from an image file
 * and puts them in a byte array. It then serializes the byte array. We don't
 * use ImageIcon because it does not guarantee compatibility with future JVMs.
 * 
 * @author Jeff Tassin
 */
public class IconMemento extends AbstractJETAPersistable implements Cloneable {
	static final long serialVersionUID = 7200914507339096976L;

	/**
	 * The version of this class
	 */
	public static final int VERSION = 1;

	/**
	 * The bytes that make up an image file (PNG,JPG,GIF).
	 */
	private byte[] m_image_bytes;

	/**
	 * the icon instance.
	 */
	private transient ImageIcon m_icon;

	/**
	 * Creates an unitialized <code>IconMemento</code> instance.
	 */
	public IconMemento() {
	}

	/**
	 * Initializes this memento from an image file.
	 */
	public IconMemento(File f) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			FileInputStream fis = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(fis);
			byte[] buff = new byte[1024];
			int read = bis.read(buff);
			while (read > 0) {
				bos.write(buff, 0, read);
				read = bis.read(buff);
			}

			if (bos.size() > 0) {
				m_image_bytes = bos.toByteArray();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns an image icon based on the image data in this memento.
	 * 
	 * @return the image icon based on the image of this memento
	 */
	public ImageIcon getImageIcon() {
		if (m_icon == null && m_image_bytes != null)
			m_icon = new ImageIcon(m_image_bytes);

		return m_icon;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (null == obj)
			return false;

		if (!(obj instanceof IconMemento))
			return false;

		IconMemento iconMemento = (IconMemento) obj;

		if ((null == m_image_bytes && null != iconMemento.m_image_bytes) || (null != m_image_bytes && null == iconMemento.m_image_bytes)
				|| (null != m_image_bytes && null != iconMemento.m_image_bytes && (iconMemento.m_image_bytes.length != m_image_bytes.length))) {
			return false;
		}
		else {
			for (int i = 0; i < m_image_bytes.length; i++) {
				if (m_image_bytes[i] != iconMemento.m_image_bytes[i])
					return false;
			}
		}
		return true;
	}

	public Object clone() {
		IconMemento other = null;
		try {
			other = (IconMemento) super.clone();
			//
			other.m_image_bytes = m_image_bytes;
			//
			m_icon = null;
			other.m_icon = getImageIcon();
			m_icon = null;
			getImageIcon();
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
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readVersion();
		m_image_bytes = (byte[]) in.readObject("imagebytes");
	}

	/**
	 * Externalizable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeObject("imagebytes", m_image_bytes);
	}

}
