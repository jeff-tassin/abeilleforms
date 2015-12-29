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
//import com.jgoodies.looks.plastic.PlasticLookAndFeel;
//import com.jgoodies.looks.plastic.PlasticTheme;

/**
 * A look and feel loader that knows how to work with the JGoodies Look and Feel
 * 
 * @author Jeff Tassin
 */
class JGoodiesLoader implements LookAndFeelLoader {
	public LookAndFeel createLookAndFeel(LookAndFeelInfo lfinfo) {
		try {
			Class c = Class.forName(lfinfo.getLookAndFeelClassName());
			return (LookAndFeel) c.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			FormsLogger.debug(e);
		}
		return null;
	}

	public void setLookAndFeel(LookAndFeelInfo lfinfo) {
		try {
			/*
			if (lfinfo.getLookAndFeel() != null) {
				String theme = lfinfo.getTheme();
				if (theme != null && theme.length() > 0) {
					Class c = Class.forName(theme);
					PlasticTheme ptheme = (PlasticTheme) c.newInstance();
					PlasticLookAndFeel.setMyCurrentTheme(ptheme);
				}
				UIManager.setLookAndFeel(lfinfo.getLookAndFeel());
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();
			FormsLogger.debug(e);
		}
	}
}
