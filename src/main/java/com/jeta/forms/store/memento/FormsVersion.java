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

import java.io.Externalizable;
import java.io.IOException;

/**
 * This class is used to provide a version stamp to a forms file. This is not
 * the version of the forms designer product. Rather, this allows the runtime to
 * check the form file version. If this is an older runtime that is trying to
 * load a file stored with a more recent, incompatible runtime, an error message
 * can be displayed to the user.
 * 
 * Incompatible changes should be marked by an increase in the major version
 * number. Changes to the minor and sub versions should be reserved for bug
 * fixes or changes that can be loaded and safely ignored by older runtimes.
 * 
 * @deprecated. Use FormsVersion2
 * @author Jeff Tassin
 */
public class FormsVersion implements Externalizable, Comparable {
	/**
	 * The version of this class.
	 */
	public static final int VERSION = 1;

	/**
	 * The version of the forms runtime file. Update these values when the forms
	 * runtime is changed.
	 */
	private int m_major = 1;
	private int m_minor = 3;
	private int m_sub = 0;

	/**
	 * Comparable implementation
	 */
	public int compareTo(Object o) {
		if (o instanceof FormsVersion) {
			FormsVersion version = (FormsVersion) o;
			if (m_major > version.m_major)
				return 1;
			else if (m_major < version.m_major)
				return -1;
			else {
				/** major versions are the same, check minor */
				if (m_minor > version.m_minor)
					return 1;
				else if (m_minor < version.m_minor)
					return -1;
				else {
					/** major and minor versions are the same, check sub */
					if (m_sub > version.m_sub)
						return 1;
					else if (m_sub < version.m_sub)
						return -1;
					else
						return 0; // versions are equal
				}
			}
		}
		else {
			return -1;
		}

	}

	/**
	 * Externalizable Implementation
	 */
	public void readExternal(java.io.ObjectInput in) throws ClassNotFoundException, IOException {
		int classversion = in.readInt();
		m_major = in.readInt();
		m_minor = in.readInt();
		m_sub = in.readInt();
	}

	/**
	 * Externalizable Implementation
	 */
	public void writeExternal(java.io.ObjectOutput out) throws IOException {
		out.writeInt(VERSION);
		out.writeInt(m_major);
		out.writeInt(m_minor);
		out.writeInt(m_sub);
	}

	public String toString() {
		return m_major + "." + m_minor + "." + m_sub;
	}
}