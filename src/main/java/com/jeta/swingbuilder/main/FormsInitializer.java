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

package com.jeta.swingbuilder.main;

import java.io.File;
import java.util.Locale;

import com.jeta.forms.logger.FormsLogger;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.app.AppResourceLoader;
import com.jeta.swingbuilder.app.ApplicationStateStore;
import com.jeta.swingbuilder.common.ComponentNames;
import com.jeta.swingbuilder.debug.DebugLogger;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.interfaces.resources.ResourceLoader;

/**
 * This class is responsible for initializing all components needed for the
 * Abeille Forms application.
 * 
 * @author Jeff Tassin
 */
public class FormsInitializer {
	private DebugLogger m_debug_logger;

	/**
	 * Checks that the home directory is present. If not, tries to create it.
	 * 
	 * @param home
	 * @return
	 */
	private boolean checkHome(String home) {
		boolean bresult = false;
		try {
			File f = new File(home);
			f.mkdir();
			bresult = true;
		} catch (Exception e) {
		}
		return bresult;
	}

	/**
	 * Initializes the application
	 * 
	 * @param args
	 */
	public void initialize(String[] args) {
		// now try to load properties
		try {
			String tshome = null;
			String language = null;
			String country = null;
			if (args != null) {
				for (int x = 0; x < args.length; x++) {
					try {
						if (args[x].equals("-language"))
							language = args[x + 1];
						else if (args[x].equals("-country"))
							country = args[x + 1];
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (tshome == null) {
				tshome = System.getProperty("user.home");
				if (tshome != null && tshome.length() > 0) {
					char c = tshome.charAt(tshome.length() - 1);
					if (c != '\\' && c != '/')
						tshome = tshome + File.separatorChar + ".abeilleforms13";
					else
						tshome = tshome + ".abeilleforms13";
				}
			}

			if (checkHome(tshome)) {
				/** the jeta framework */
				AppResourceLoader loader = new AppResourceLoader(tshome, "");
				JETARegistry.rebind(ResourceLoader.COMPONENT_ID, loader);

				/**
				 * Initialize the components needed by the forms sub-system.
				 */
				com.jeta.forms.defaults.DefaultInitializer.initialize();

				initializeLocale(language, country);
				// load global application user settings
				ApplicationStateStore appstore = new ApplicationStateStore("application");
				JETARegistry.rebind(ComponentNames.APPLICATION_STATE_STORE, appstore);
				System.setProperty("abeilleforms.home", tshome);
				System.setProperty("abeilleforms.version", com.jeta.forms.support.AbeilleForms.getVersion());

				/**
				 * Initialize the components needed by the swing builder system.
				 */
				com.jeta.swingbuilder.defaults.DefaultInitializer.initialize();

				/**
				 * register the help manager
				 */
				//JETARegistry.rebind(ComponentNames.APPLICATION_HELP_FACTORY, new AbeilleHelpFactory());
			}
			else {
				System.out.println("Unable to establish home directory: " + tshome);
				System.exit(0);
			}

			FormDesignerUtils.getEnvVars(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		FormsLogger.debug("FormsInitializer.completed.... ");

	}

	/**
	 * Gets the language and country from the command line and sets as the
	 * default locale
	 * 
	 * @param language
	 * @param country
	 */
	void initializeLocale(String language, String country) {
		Locale locale = null;
		if (language != null && country != null)
			locale = new Locale(language, country);
		else
			locale = Locale.getDefault();

		com.jeta.open.i18n.I18NHelper oi18n = com.jeta.open.i18n.I18NHelper.getInstance();
		oi18n.setLocale(locale);
		oi18n.loadBundle("com.jeta.swingbuilder.resources.MessagesBundle");

	}

	private void startDebugConsole() {
		try {
			m_debug_logger = new DebugLogger("localhost");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * LoggerHandler for writing log message to console.
	 * 
	 * @temp
	 */
	/*
	 * public class DebugHandler extends Handler { public DebugHandler() {
	 * setFormatter( new java.util.logging.SimpleFormatter() ); }
	 * 
	 * public boolean isLoggable(LogRecord record) { return true; }
	 * 
	 * public void close() { }
	 * 
	 * public void flush() { }
	 * 
	 * public void publish( LogRecord record ) { if ( m_debug_logger != null )
	 * m_debug_logger.sendRequest( record.getMessage() ); } }
	 */

}
