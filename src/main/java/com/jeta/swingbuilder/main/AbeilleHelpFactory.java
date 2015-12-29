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

import javax.help.HelpBroker;

import com.jeta.swingbuilder.help.HelpFactory;

/**
 * Factory for creating HelpBrokers for the application
 * 
 * @author Jeff Tassin
 */
public class AbeilleHelpFactory extends HelpFactory {
	private static HelpBroker m_helpbroker;

	public AbeilleHelpFactory() {

	}

	/**
	 * Creates a HelpBroker instance for the application
	 */
	public HelpBroker createHelpBroker() {
		synchronized (AbeilleHelpFactory.class) {
			if (m_helpbroker == null) {
				HelpInitializer hi = new HelpInitializer();
				m_helpbroker = hi.initialize();
			}
		}
		return m_helpbroker;
	}
}
