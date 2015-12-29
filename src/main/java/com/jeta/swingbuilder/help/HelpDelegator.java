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

import javax.help.CSH;
import javax.help.HelpBroker;

import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.common.ComponentNames;

class HelpDelegator implements ActionListener {
	/** the delegate action handler that actually displays the help */
	private CSH.DisplayHelpFromSource m_delegate;

	public HelpDelegator() {
		m_delegate = new CSH.DisplayHelpFromSource(HelpDelegator.getHelpBroker());
	}

	public void actionPerformed(ActionEvent evt) {
		m_delegate.actionPerformed(evt);
	}

	/**
	 * Creates the main help broker for the application and registers with the
	 * component manager
	 */
	public synchronized static HelpBroker getHelpBroker() {
		try {
			HelpFactory factory = (HelpFactory) JETARegistry.lookup(ComponentNames.APPLICATION_HELP_FACTORY);
			if (factory != null) {
				return factory.createHelpBroker();
			}
			else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
