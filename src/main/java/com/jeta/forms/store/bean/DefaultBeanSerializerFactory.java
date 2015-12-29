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

package com.jeta.forms.store.bean;

import java.io.InputStream;
import java.io.ObjectInputStream;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.memento.PropertiesMemento;

/**
 * A default implementation of a factory for creating BeanSerializer and
 * BeanDeserializer instances. These classes are resonsible for
 * serializing/deserializing the properties of a JETABean.
 * 
 * @author Jeff Tassin
 */
public class DefaultBeanSerializerFactory implements BeanSerializerFactory {
	/**
	 * Creates an instance of a BeanSerializer which can be used to serialize
	 * the properties of a JETABean.
	 * 
	 * @return a bean serializer instance.
	 */
	public BeanSerializer createSerializer() {
		return new DefaultBeanSerializer();
	}

	/**
	 * Creates an instance of a BeanSerializer which can be used to deserialize
	 * the properties of a JETABean.
	 * 
	 * @param istream
	 *            an input stream that contains a PropertiesMemento object. The
	 *            deserializer uses the PropertiesMemento as a basis for
	 *            creating and initializing JETABeans.
	 * @return a bean deserializer instance.
	 */
	public BeanDeserializer createDeserializer(InputStream istream) throws FormException {
		try {
			ObjectInputStream ois = new ObjectInputStream(istream);
			PropertiesMemento memento = (PropertiesMemento) ois.readObject();
			return new DefaultBeanDeserializer(memento);
		} catch (Exception e) {
			FormsLogger.severe(e);
			if (e instanceof FormException)
				throw (FormException) e;

			throw new FormException(e);
		}
	}

	/**
	 * Creates an instance of a BeanDeserializer which can be used to
	 * deserialize the properties of a JETABean.
	 * 
	 * @param pm
	 *            an object that contains the serialized properties of a
	 *            JETABean. The deserializer uses this as a basis for creating
	 *            and initializing JETABeans.
	 * @return a bean deserializer instance.
	 */
	public BeanDeserializer createDeserializer(PropertiesMemento pm) throws FormException {
		return new DefaultBeanDeserializer(pm);
	}

}
