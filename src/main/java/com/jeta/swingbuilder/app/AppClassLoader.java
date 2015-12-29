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

package com.jeta.swingbuilder.app;

import java.io.InputStream;
import java.net.URL;

import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.interfaces.resources.ResourceLoader;

public class AppClassLoader extends ClassLoader {
	String m_altPath;

	public AppClassLoader() {

	}

	public AppClassLoader(String path) {
		m_altPath = path;
	}

	public URL getResource(String name) {
		if (m_altPath == null) {
			return super.getResource(name);
		}
		else {
			String urlstring = "file://localhost/" + m_altPath + "/" + name;
			try {
				return new URL(urlstring);
			} catch (Exception e) {
				return super.getResource(name);
			}
		}
	}

	public InputStream getResourceAsStream(String name) {
		try {
			if (m_altPath == null) {
				ResourceLoader loader = (ResourceLoader) JETARegistry.lookup(ResourceLoader.COMPONENT_ID);
				return loader.getInputStream(name);
			}
			else {
				URL url = getResource(name);
				return url.openStream();
			}

		} catch (Exception e) {
			return super.getResourceAsStream(name);
		}
	}
}
