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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import com.jeta.forms.gui.common.URLClassLoaderHelper;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.interfaces.userprops.TSUserPropertiesUtils;

/**
 * The look and feel manager for the application.
 * 
 * @author Jeff Tassin
 */
public class DefaultLookAndFeelManager {
	/**
	 * The class loader for all look and feels.
	 */
	private URLClassLoaderHelper m_loader;

	/**
	 * A list of default look and feels. (We use LookAndFeelInfo objects for
	 * convenience here)
	 */
	private LinkedHashMap m_lafs = new LinkedHashMap();

	// public static final String ALLOY = AlloyLoader.class.getName();
	public static final String JGOODIES = JGoodiesLoader.class.getName();

	public static final String DEFAULT = DefaultLoader.class.getName();

	public static final String COMPONENT_ID = "look.and.feel.manager";

	public static final String CURRENT_LOOK_AND_FEEL = "current.look.and.feel";

	/**
	 * ctor
	 */
	public DefaultLookAndFeelManager() {
		loadDefaults();
	}

	private void createDefaultLookAndFeel(String lfLoader, String lfClassName, String lfThemeName, String name) {
		LookAndFeelInfo lf = new LookAndFeelInfo(lfLoader, lfClassName, lfThemeName, name, lfClassName + "," + lfThemeName);
		m_lafs.put(lf.getId(), lf);
	}

	public LookAndFeelInfo findById(String id) {
		return (LookAndFeelInfo) m_lafs.get(id);
	}

	/**
	 * Locates a look and feel by the class name.
	 */
	public LookAndFeelInfo findByClass(String className, String theme) {
		Iterator iter = m_lafs.values().iterator();
		while (iter.hasNext()) {
			LookAndFeelInfo info = (LookAndFeelInfo) iter.next();
			if (theme == null || theme.length() == 0) {
				if (className.equals(info.getLookAndFeelClassName()))
					return info;
			}
			else {
				if (className.equals(info.getLookAndFeelClassName()) && theme.equals(info.getTheme()))
					return info;
			}
		}
		return null;
	}

	/**
	 * @return the default look and feel for the application. The following OSes
	 *         will have the given default look and feel: Linux Java Metal
	 *         Windows Windows Apple Apple Other Java Metal
	 */
	public LookAndFeelInfo getDefaultLookAndFeel() {
		String id = TSUserPropertiesUtils.getString(CURRENT_LOOK_AND_FEEL, null);
		if (id != null) {
			LookAndFeelInfo info = findById(id);
			if (info != null)
				return info;
		}

		if (JETAToolbox.isOSX()) {
			return findByClass(LookAndFeelNames.ID_AQUA_LF, null);
		}
		else {
			return findByClass("com.jgoodies.looks.plastic.PlasticXPLookAndFeel", "com.jgoodies.looks.plastic.theme.ExperienceBlue");
		}
	}

	public Collection getDefaultLookAndFeels() {
		return m_lafs.values();
	}

	/**
	 * @return the look and feel
	 */
	public LookAndFeel getLookAndFeel(String lfId) {
		try {
			LookAndFeelInfo info = (LookAndFeelInfo) m_lafs.get(lfId);
			if (info != null) {
				return info.getLookAndFeel();
			}
		} catch (Exception e) {
			e.printStackTrace();
			// ignore here. null is returned on error
		}
		return null;
	}

	private void loadDefaults() {
		try {
			/*
			 * implement later createDefaultLookAndFeel( ALLOY,
			 * "com.incors.plaf.alloy.AlloyLookAndFeel",
			 * "com.incors.plaf.alloy.DefaultAlloyTheme",
			 * I18N.getLocalizedMessage( "Alloy - Default" ) );
			 * createDefaultLookAndFeel( ALLOY,
			 * "com.incors.plaf.alloy.AlloyLookAndFeel",
			 * "com.incors.plaf.alloy.themes.glass.GlassTheme",
			 * I18N.getLocalizedMessage( "Alloy - Glass" ) );
			 * createDefaultLookAndFeel( ALLOY,
			 * "com.incors.plaf.alloy.AlloyLookAndFeel",
			 * "com.incors.plaf.alloy.themes.bedouin.BedouinTheme",
			 * I18N.getLocalizedMessage( "Alloy - Desert" ) );
			 * createDefaultLookAndFeel( ALLOY,
			 * "com.incors.plaf.alloy.AlloyLookAndFeel",
			 * "com.incors.plaf.alloy.themes.acid.AcidTheme",
			 * I18N.getLocalizedMessage( "Alloy - Acid" ) );
			 */

			/*
			createDefaultLookAndFeel(JGOODIES, "com.jgoodies.looks.plastic.PlasticXPLookAndFeel", "com.jgoodies.looks.plastic.theme.ExperienceBlue", I18N
					.getLocalizedMessage("PlasticXP - Experience Blue"));

			createDefaultLookAndFeel(JGOODIES, "com.jgoodies.looks.plastic.PlasticXPLookAndFeel", "com.jgoodies.looks.plastic.theme.ExperienceGreen", I18N
					.getLocalizedMessage("PlasticXP - Experience Green"));

			createDefaultLookAndFeel(JGOODIES, "com.jgoodies.looks.plastic.PlasticXPLookAndFeel", "com.jgoodies.looks.plastic.theme.SkyBlue", I18N
					.getLocalizedMessage("PlasticXP - Sky Blue"));
			*/
			
			createDefaultLookAndFeel(DEFAULT, "javax.swing.plaf.metal.MetalLookAndFeel", "", I18N.getLocalizedMessage("Metal"));

			/*
			createDefaultLookAndFeel(DEFAULT, "org.jvnet.substance.SubstanceLookAndFeel", "", I18N.getLocalizedMessage("Substance"));
			*/

			UIManager.LookAndFeelInfo[] lfinfo = UIManager.getInstalledLookAndFeels();
			if (lfinfo != null) {
				for (int index = 0; index < lfinfo.length; index++) {
					UIManager.LookAndFeelInfo lfi = lfinfo[index];
					createDefaultLookAndFeel(DEFAULT, lfi.getClassName(), "", lfi.getName());
				}
			}

		} catch (Exception e) {

		}
	}

	/** sets the look and feel for the given frame and look an feel class name */
	public static void setLookAndFeel(LookAndFeelInfo lfinfo) {
		try {
			if (lfinfo != null) {
				LookAndFeelLoader loader = lfinfo.getLoaderInstance();
				if (loader != null) {
					loader.setLookAndFeel(lfinfo);
				}
			}
			TSUserPropertiesUtils.setString(CURRENT_LOOK_AND_FEEL, lfinfo.getId());
		} catch (Exception e) {
			FormsLogger.debug(e);
		}
	}

}
