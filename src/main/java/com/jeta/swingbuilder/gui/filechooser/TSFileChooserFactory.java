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

package com.jeta.swingbuilder.gui.filechooser;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.interfaces.userprops.TSUserProperties;

/**
 * This class is used to invoke a file chooser dialog. The type of dialog
 * depends on the user preferences. It can either be a JFileChooser or a
 * TSFileChooser. The TSFileChooser is a more advanced dialog that follows the
 * Linux and Emacs type file selection.
 * 
 * @author Jeff Tassin
 */
public class TSFileChooserFactory {

	public static final String LAST_DIRECTORY = "jeta.filechooser.lastdirectory";

	/**
	 * Invokes a file open dialog. The type of dialog depends on the user
	 * preferences. It will be either a JFileChooser or a TSFileChooser.
	 * 
	 * @return the file selected by the user, or null if the operation was
	 *         canceled
	 */
	public static File showOpenDialog() {
		return showOpenDialog(JFileChooser.FILES_ONLY);
	}

	/**
	 * Invokes a file open dialog. Allows the caller to specify a type of file
	 * used.
	 */
	public static File showOpenDialog(String type) {
		return showOpenDialog(type, (TSFileFilter) null);
	}

	/**
	 * Invokes a file open dialog. Allows the caller to specify a type of file
	 * used.
	 */
	public static File showOpenDialog(String type, TSFileFilter filter) {
		return showOpenDialog(new FileChooserConfig(type, JFileChooser.FILES_ONLY, filter));
	}

	/**
	 * Invokes a file open dialog. Allows the caller to specify a type of file
	 * used.
	 */
	public static File showOpenDialog(String type, TSFileFilter[] filters) {
		return showOpenDialog(new FileChooserConfig(type, JFileChooser.FILES_ONLY, filters));
	}

	/**
	 * Invokes a file open dialog. The type of dialog depends on the user
	 * preferences. It will be either a JFileChooser or a TSFileChooser.
	 * 
	 * @return the file selected by the user, or null if the operation was
	 *         canceled
	 */
	public static File showOpenDialog(int mode) {
		TSUserProperties userprops = (TSUserProperties) JETARegistry.lookup(TSUserProperties.COMPONENT_ID);
		return showOpenDialog(new FileChooserConfig(userprops.getProperty(LAST_DIRECTORY, null), null, mode, (TSFileFilter[]) null));
	}

	/**
	 * Invokes a file open dialog. The type of dialog depends on the user
	 * preferences. It will be either a JFileChooser or a TSFileChooser.
	 * 
	 * @return the file selected by the user, or null if the operation was
	 *         canceled
	 */
	public static File showOpenDialog(int mode, TSFileFilter filter) {
		TSUserProperties userprops = (TSUserProperties) JETARegistry.lookup(TSUserProperties.COMPONENT_ID);
		return showOpenDialog(new FileChooserConfig(userprops.getProperty(LAST_DIRECTORY, null), null, mode, filter));

		// return showOpenDialog( mode, userprops.getProperty( LAST_DIRECTORY,
		// null ), new TSFileFilter[] { filter} );
	}

	/**
	 * Invokes a file open dialog. The type of dialog depends on the user
	 * preferences. It will be either a JFileChooser or a TSFileChooser.
	 * 
	 * @return the file selected by the user, or null if the operation was
	 *         canceled
	 */
	public static File showOpenDialog(int mode, String last_dir, TSFileFilter[] filters) {
		return showOpenDialog(new FileChooserConfig(last_dir, null, mode, filters));
	}

	/**
	 * Invokes a file open dialog. The type of dialog depends on the user
	 * preferences. It will be either a JFileChooser or a TSFileChooser.
	 * 
	 * @return the file selected by the user, or null if the operation was
	 *         canceled
	 */
	public static File showOpenDialog(FileChooserConfig fcc) {
		TSUserProperties userprops = (TSUserProperties) JETARegistry.lookup(TSUserProperties.COMPONENT_ID);
		String last_dir = fcc.getInitialDirectory();

		if (last_dir != null) {
			File f = new File(last_dir);
			if (!f.exists()) {
				last_dir = f.getParent();
				if (last_dir != null) {
					f = new File(last_dir);
					if (!f.exists()) {
						last_dir = userprops.getProperty(LAST_DIRECTORY, null);
					}
				}
			}
		}

		String ftype = fcc.getFileType();
		if (last_dir == null) {
			String default_dir = userprops.getProperty(LAST_DIRECTORY, null);
			if (ftype == null)
				ftype = "";

			String last_dir_key = LAST_DIRECTORY + ftype;
			last_dir = userprops.getProperty(last_dir_key, default_dir);
			if (last_dir != null) {
				File f = new File(last_dir);
				if (!f.exists()) {
					last_dir = f.getParent();
					if (last_dir != null) {
						f = new File(last_dir);
						if (!f.exists()) {
							last_dir = userprops.getProperty(LAST_DIRECTORY, null);
						}
					}
				}
			}
		}

		JFileChooser chooser = new JFileChooser(last_dir);
		TSFileFilter[] filters = fcc.getFileFilters();
		if (filters != null) {
			for (int index = 0; index < filters.length; index++) {
				chooser.addChoosableFileFilter(filters[index]);
			}
		}
		chooser.setFileSelectionMode(fcc.getMode());
		int returnVal = chooser.showOpenDialog(fcc.getParentComponent());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			String dir = "";
			if (f.isDirectory())
				dir = f.getPath();
			else {
				dir = f.getParent();
			}

			if (ftype != null) {
				String last_dir_key = LAST_DIRECTORY + ftype;
				userprops.setProperty(last_dir_key, dir);
			}
			userprops.setProperty(LAST_DIRECTORY, dir);

			return f;
		}
		else {
			return null;
		}
	}

	public static File showSaveDialog(String type) {
		return showSaveDialog(type, (TSFileFilter[]) null);
	}

	public static File showSaveDialog(String type, TSFileFilter filter) {
		return showSaveDialog(new FileChooserConfig(type, JFileChooser.FILES_ONLY, filter));
	}

	public static File showSaveDialog(String type, TSFileFilter[] filter) {
		return showSaveDialog(new FileChooserConfig(type, JFileChooser.FILES_ONLY, filter));
	}

	public static File showSaveDialog() {
		TSUserProperties userprops = (TSUserProperties) JETARegistry.lookup(TSUserProperties.COMPONENT_ID);
		return showSaveDialog(new FileChooserConfig(userprops.getProperty(LAST_DIRECTORY, null), null, JFileChooser.FILES_ONLY, (TSFileFilter[]) null));
	}

	public static File showSaveDialog(FileChooserConfig fcc) {
		TSUserProperties userprops = (TSUserProperties) JETARegistry.lookup(TSUserProperties.COMPONENT_ID);
		String last_dir = fcc.getInitialDirectory();
		File selected_file = null;
		if (last_dir != null) {
			File f = new File(last_dir);
			if (!f.exists()) {
				last_dir = f.getParent();
				if (last_dir != null) {
					f = new File(last_dir);
					if (!f.exists()) {
						last_dir = userprops.getProperty(LAST_DIRECTORY, null);
					}
				}
			}
			else if (f.isFile()) {
				selected_file = f;
			}
		}

		String ftype = fcc.getFileType();
		if (last_dir == null) {
			String default_dir = userprops.getProperty(LAST_DIRECTORY, null);
			if (ftype == null)
				ftype = "";

			String last_dir_key = LAST_DIRECTORY + ftype;
			last_dir = userprops.getProperty(last_dir_key, default_dir);
			if (last_dir != null) {
				File f = new File(last_dir);
				if (!f.exists()) {
					last_dir = f.getParent();
					if (last_dir != null) {
						f = new File(last_dir);
						if (!f.exists()) {
							last_dir = userprops.getProperty(LAST_DIRECTORY, null);
						}
					}
				}
			}
		}

		JFileChooser chooser = new JFileChooser(last_dir) {
			public void approveSelection() {
				// check if file exists
				File f = getSelectedFile();
				if (f.exists()) {
					String title = I18N.getLocalizedMessage("Warning");
					String msg = I18N.getLocalizedMessage("File_exists_overwrite?");
					int result = JOptionPane.showConfirmDialog(this, msg, title, JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION)
						super.approveSelection();
				}
				else
					super.approveSelection();
			}
		};

		TSFileFilter[] filters = fcc.getFileFilters();
		if (filters != null) {
			for (int index = 0; index < filters.length; index++) {
				chooser.addChoosableFileFilter(filters[index]);
			}
		}

		if (selected_file != null)
			chooser.setSelectedFile(selected_file);

		int returnVal = chooser.showSaveDialog(fcc.getParentComponent());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			String dir = "";
			if (f.isDirectory())
				dir = f.getPath();
			else
				dir = f.getParent();

			if (ftype != null) {
				String last_dir_key = LAST_DIRECTORY + ftype;
				userprops.setProperty(last_dir_key, dir);
			}
			userprops.setProperty(LAST_DIRECTORY, dir);
			return f;
		}
		else
			return null;
	}
}
