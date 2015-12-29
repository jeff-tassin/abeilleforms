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

package com.jeta.swingbuilder.gui.lookandfeel;

import javax.swing.LookAndFeel;

import com.jeta.forms.logger.FormsLogger;

/**
 * 
 * @author Jeff Tassin
 */
public class LookAndFeelInfo {
	/**
	 * The class name of the imported look and feel
	 */
	private String m_lf_class_name;

	/**
	 * The name of the look and feel
	 */
	private String m_name;

	/**
	 * The name of the look and feel theme. Can be null for those LaFs that
	 * don't support themes.
	 */
	private String m_theme;

	/**
	 * A unique id for the look and feel. Set by the application.
	 */
	private String m_id;

	/**
	 * A cached reference to the look and feel associated with this class
	 */
	private LookAndFeel m_laf;

	/**
	 * The name of the class responsible for instantiating a specified look and
	 * feel along with the specified theme.
	 */
	private String m_loader_class;

	/**
	 * A cached reference to the look and feel loader
	 */
	private LookAndFeelLoader m_loader;

	/**
	 * ctor
	 * 
	 * @param loaderClass
	 *            the name of the class responsible for instantiating a
	 *            specified look and feel along with the specified theme.
	 * @param lfClass
	 *            the name of the look and feel class
	 * @param lfTheme
	 *            the name of a theme to apply to the look and feel. Can be
	 *            null.
	 * @param lfName
	 *            the name of the look and feel. This will be displayed in the
	 *            application L&F menu.
	 */
	public LookAndFeelInfo(String loaderClass, String lfClass, String lfTheme, String lfName, String id) {
		m_loader_class = loaderClass;
		m_lf_class_name = lfClass;
		m_theme = lfTheme;
		m_name = lfName;
		m_id = id;
	}

	public LookAndFeelLoader getLoaderInstance() {
		if (m_loader == null) {
			try {
				Class c = Class.forName(m_loader_class);
				m_loader = (LookAndFeelLoader) c.newInstance();
			} catch (Exception e) {
				FormsLogger.severe(e);
			}
		}
		return m_loader;
	}

	public LookAndFeel getLookAndFeel() {
		try {
			m_laf = getLoaderInstance().createLookAndFeel(this);
		} catch (Exception e) {
			FormsLogger.severe(e);
		}
		return m_laf;
	}

	/**
	 * @return the description for the look and feel
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * Returns the unique id for this look and feel.
	 */
	public String getId() {
		return m_id;
	}

	/**
	 * @return the class name of the imported look and feel
	 */
	public String getLookAndFeelClassName() {
		return m_lf_class_name;
	}

	public String getTheme() {
		return m_theme;
	}

	/**
	 * Sets the description for the look and feel
	 */
	public void setName(String desc) {
		m_name = desc;
	}

	/**
	 * Sets the class name of the imported look and feel
	 */
	public void setLookAndFeelClassName(String name) {
		m_lf_class_name = name;
	}

}
