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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Helper class for handling application Help support
 * 
 * @author Jeff Tassin
 */
public class HelpUtils {

	/**
	 * Adds an action listener to the button to invoke help when the button is
	 * pressed.
	 */
	public static void enableHelpOnButton(javax.swing.AbstractButton btn, String id) {
		try {
			/**
			 * from CSH source
			 * 
			 * @see CSH.setHelpIDString( btn, id ); We don't call CSH here
			 *      because we don't want to load the classes until the user
			 *      actually presses the help button
			 */

			setHelpIDString(btn, id);
			btn.addActionListener(new HelpListener());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setHelpIDString(javax.swing.AbstractButton btn, String id) {
		btn.putClientProperty("HelpID", id);
	}

	/**
	 * This is a lazy action listener for the help system. We don't want to load
	 * the helpset unless the user explicitly presses the help button. It is
	 * hoped that this would improve startup performance because it is one less
	 * thing that has to be loaded; however no tests were run to prove that this
	 * actually helps.
	 */
	static class HelpListener implements ActionListener {
		/** the delegate action handler that actually displays the help */
		private HelpDelegator m_delegate;

		public void actionPerformed(ActionEvent evt) {
			if (m_delegate == null) {
				m_delegate = new HelpDelegator();
			}
			m_delegate.actionPerformed(evt);
		}
	}
}
