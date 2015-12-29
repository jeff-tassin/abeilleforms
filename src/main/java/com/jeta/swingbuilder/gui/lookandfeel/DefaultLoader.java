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
import javax.swing.UIManager;

import com.jeta.forms.logger.FormsLogger;

/**
 * A look and feel loader that knows how to work with the Default Look and Feel
 * 
 * @author Jeff Tassin
 */
class DefaultLoader implements LookAndFeelLoader {
	public LookAndFeel createLookAndFeel(LookAndFeelInfo lfinfo) {
		try {
			if (lfinfo.getLookAndFeelClassName().indexOf("SubstanceLookAndFeel") >= 0) {
				String jc_version = System.getProperty("java.class.version");
				float fval = Float.valueOf(jc_version).floatValue();
				if (fval < 49.0f) {
					return null;
				}
			}
			Class c = Class.forName(lfinfo.getLookAndFeelClassName());
			LookAndFeel result = (LookAndFeel) c.newInstance();
			return result;
		} catch (Throwable e) {
			FormsLogger.debug(e);
		}
		return null;
	}

	public void setLookAndFeel(LookAndFeelInfo lfinfo) {
		try {
			if (lfinfo.getLookAndFeel() != null)
				UIManager.setLookAndFeel(lfinfo.getLookAndFeel());
		} catch (Exception e) {
			FormsLogger.debug(e);
		}
	}
}
