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

package com.jeta.forms.gui.beans.factories;

import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.Introspector;

import com.jeta.forms.gui.beans.BeanProperties;
import com.jeta.forms.gui.beans.DynamicBeanInfo;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.properties.CompoundBorderProperty;
import com.jeta.forms.store.properties.DefaultBorderProperty;
import com.jeta.forms.store.properties.ScrollBarsProperty;

/**
 * Base class for Swing component bean factories. This factory includes support
 * for adding a scrollable property to any Swing component. This means that
 * before the component is added to a form, it is first added to a JScrollPane
 * and the scroll pane is added instead. This occurs only if the scroll flag is
 * set to true. Many swing component factoriesn don't require this
 * functionality, so they would set the flag to false. Other component factories
 * such as JTree, JList, JTextArea would set this flag to true.
 * 
 * Since the architecture only supports lightweight components, all factories
 * will probably extend from this class.
 * 
 * @author Jeff Tassin
 */
public class JComponentBeanFactory implements BeanFactory {
	/**
	 * The component class for the Swing component that we will instantiate
	 */
	private Class m_comp_class;

	/**
	 * The name of the bean class without any package qualifiers
	 */
	private String m_short_name;

	/**
	 * Flag that indicates if the component should be contained in a JScrollPane
	 * when created on the form.
	 */
	private boolean m_scrollable = false;

	/**
	 * Creates a <code>JComponentBeanFactory</code> instance with the
	 * specified Java Bean class object.
	 * 
	 * @param compClass
	 *            the class object of the Java bean to associate with this
	 *            factory.
	 */
	public JComponentBeanFactory(Class compClass) {
		setBeanClass(compClass);
	}

	/**
	 * BeanFactory implementation. Instantiates a JETABean and assigns any
	 * custom properties needed by the application.
	 * 
	 * @param compName
	 *            the name to assign to this component by calling
	 *            Component.setName
	 * @param instantiateBean
	 *            set to true if the underlying Java Bean should be instantiated
	 *            as well. During deserialization we don't want to do this
	 *            because the BeanDeserializer will create the JavaBean for us.
	 * @param setDefaults
	 *            sets default properties for the bean. If false, no properties
	 *            will be set (e.g. the text for a JButton)
	 * @return the newly instantiated JETABean
	 */
	public JETABean createBean(String compName, boolean instantiateBean, boolean setDefaults) throws FormException {
		Component comp = null;
		if (instantiateBean) {
			comp = (Component) instantiateBean();
			comp.setName(compName);
		}

		DynamicBeanInfo beaninfo = createBeanInfo(m_comp_class);
		BeanProperties default_props = new BeanProperties(beaninfo);
		defineProperties(default_props);
		return new JETABean(comp, default_props);
	}

	/**
	 * Creates a dynamic bean info object for the given class. A dynamic bean
	 * info object contains both standard and custom property descriptors for a
	 * Java bean.
	 * 
	 * @param compClass
	 *            the class object for the component whose bean information to
	 *            return.
	 * @return the dynamic bean information for the given bean class object.
	 */
	public static DynamicBeanInfo createBeanInfo(Class compClass) throws FormException {
		try {
			BeanInfo info = Introspector.getBeanInfo(compClass);
			return new DynamicBeanInfo(info);
		} catch (Exception e) {
			FormsLogger.severe(e);
			if (e instanceof FormException)
				throw (FormException) e;
			else
				throw new FormException(e);
		}
	}

	/**
	 * Returns the class for the bean associated with this factory.
	 * 
	 * @return the class for the bean associated with this factory.
	 */
	public Class getBeanClass() {
		return m_comp_class;
	}

	/**
	 * Defines the custom properties and default values for those properties for
	 * the Swing component associated with this factory. Specialized factories
	 * should override this method to provide any additional custom properties
	 * for their components. If you override this method, make sure to call
	 * super.defineProperties in the specialized class.
	 * 
	 * @param props
	 *            used to register any custom properties.
	 */
	public void defineProperties(BeanProperties props) {
		CompoundBorderProperty prop = new CompoundBorderProperty();
		prop.addBorder(new DefaultBorderProperty());
		props.register(prop);

		if (isScrollable())
			props.register(new ScrollBarsProperty());

	}

	/**
	 * Returns the bean class name without any package qualifiers.
	 * 
	 * @return the bean class name.
	 */
	public String getShortBeanClassName() {
		return m_short_name;
	}

	/**
	 * Creates a instance of the bean associated with this factory.
	 * 
	 * @return the new Java bean instance.
	 */
	public Component instantiateBean() throws FormException {
		try {
			return (Component) m_comp_class.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new FormException(e);
		}
	}

	/**
	 * Returns the flag that determines if the Java bean should be contained in
	 * a JScrollPane when added to the form.
	 * 
	 * @return tha flag that controls whether the ScrollBarsProperty will be
	 *         added to the component.
	 */
	public boolean isScrollable() {
		return m_scrollable;
	}

	/**
	 * Sets the class object for the Java bean associated with this factory
	 * 
	 * @param compClass
	 *            the class object for the Java Bean associated with this
	 *            factory.
	 */
	public void setBeanClass(Class compClass) {
		m_comp_class = compClass;
		try {
			String name = compClass.getName();
			int pos = name.lastIndexOf(".");
			if (pos > 0)
				name = name.substring(pos + 1, name.length());

			m_short_name = name;
		} catch (Exception e) {
			m_short_name = compClass.getName();
		}
	}

	/**
	 * Sets that flag that controls whether the ScrollBarsProperty will be added
	 * to the component.
	 * 
	 * @param scrollable
	 *            set to true if the Java bean should be contained within a
	 *            JScrollPane on the form.
	 */
	public void setScrollable(boolean scrollable) {
		m_scrollable = scrollable;
	}
}
