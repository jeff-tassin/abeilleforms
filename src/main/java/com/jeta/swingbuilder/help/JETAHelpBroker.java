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

package com.jeta.swingbuilder.help;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Locale;

import javax.help.BadIDException;
import javax.help.DefaultHelpBroker;
import javax.help.HelpSet;
import javax.help.InvalidHelpSetContextException;
import javax.help.JHelp;
import javax.help.Presentation;
import javax.help.WindowPresentation;
import javax.help.Map.ID;
import javax.swing.JFrame;

public class JETAHelpBroker extends DefaultHelpBroker {
	private WindowPresentation m_mw = null;

	/**
	 * Constructor
	 */
	public JETAHelpBroker(HelpSet hs) {
		super(null);
		m_mw = new MyWindowPresentation(hs);
	}

	public ID getCurrentID() {
		return m_mw.getCurrentID();
	}

	public URL getCurrentURL() {
		return m_mw.getCurrentURL();
	}

	public HelpSet getHelpSet() {
		return m_mw.getHelpSet();
	}

	public Locale getLocale() {
		return m_mw.getLocale();
	}

	public Font getFont() {
		return m_mw.getFont();
	}

	public String getCurrentView() {
		return m_mw.getCurrentView();
	}

	public void initPresentation() {
		m_mw.createHelpWindow();
	}

	public boolean isDisplayed() {
		return m_mw.isDisplayed();
	}

	public Point getLocation() {
		return m_mw.getLocation();
	}

	public Dimension getSize() throws javax.help.UnsupportedOperationException {
		return m_mw.getSize();
	}

	public boolean isViewDisplayed() {
		return m_mw.isViewDisplayed();
	}

	private Presentation getPresentation(String presentation, String presentationName) {
		Presentation pres;

		HelpSet hs = m_mw.getHelpSet();
		if (hs == null) {
			return null;
		}

		ClassLoader loader;
		Class klass;
		Class types[] = { HelpSet.class, String.class };
		Object params[] = { hs, presentationName };
		try {
			loader = hs.getLoader();
			if (loader == null) {
				klass = Class.forName(presentation);
			}
			else {
				klass = loader.loadClass(presentation);
			}
			Method m = klass.getMethod("getPresentation", types);
			pres = (Presentation) m.invoke(null, params);
		} catch (ClassNotFoundException cnfex) {
			throw new IllegalArgumentException(presentation + "error");
		} catch (Exception ex) {
			throw new RuntimeException("unable to invoke presentation");
		}

		if (pres == null || pres instanceof javax.help.Popup) {
			return null;
		}
		return pres;
	}

	public void setCurrentID(String id) throws BadIDException {
		m_mw.setCurrentID(id);
	}

	public void setCurrentID(ID id) throws InvalidHelpSetContextException {
		m_mw.setCurrentID(id);
	}

	public void setCurrentURL(URL url) {
		m_mw.setCurrentURL(url);
	}

	public void setActivationObject(Object comp) {
		m_mw.setActivationObject(comp);
	}

	public void setActivationWindow(Window window) {
		m_mw.setActivationWindow(window);
	}

	public void setHelpSet(HelpSet hs) {
		m_mw.setHelpSet(hs);
	}

	public void setHelpSetPresentation(HelpSet.Presentation hsPres) {
		m_mw.setHelpSetPresentation(hsPres);
	}

	public void setViewDisplayed(boolean displayed) {
		m_mw.setViewDisplayed(displayed);
	}

	public void setLocation(Point p) {
		m_mw.setLocation(p);
	}

	public void setDisplayed(boolean b) {
		m_mw.setDisplayed(b);
	}

	public void setFont(Font f) {
		m_mw.setFont(f);
	}

	public void setLocale(Locale l) {
		m_mw.setLocale(l);
	}

	public void setCurrentView(String name) {
		m_mw.setCurrentView(name);
	}

	public void setSize(Dimension d) {
		m_mw.setSize(d);
	}

	public class MyWindowPresentation extends WindowPresentation {
		private Dimension m_last_size;

		public MyWindowPresentation(HelpSet hs) {
			super(hs);
		}

		public Dimension getSize() {
			Dimension d = null;

			try {
				d = super.getSize();
				Frame[] frames = JFrame.getFrames();
				for (int k = 0; k < frames.length; k++) {
					if (!(frames[k] instanceof JFrame))
						continue;
					JFrame jf = (JFrame) frames[k];
					if (jf.getContentPane().getComponentCount() == 0)
						continue;
					Component c = jf.getContentPane().getComponent(0);
					if (c == null || !(c instanceof JHelp))
						continue;

					if (jf.isVisible()) {
						d = jf.getSize();
					}
				}
			} catch (Exception e) {
				/**
				 * this is a fix to get around a problem with JavaHelp. If you
				 * show help using a dialod and then show help using a frame, an
				 * exception is thrown
				 */
				d = m_last_size;
			}

			if (d == null)
				d = new Dimension(600, 400);

			m_last_size = d;
			return d;
		}
	}

}
