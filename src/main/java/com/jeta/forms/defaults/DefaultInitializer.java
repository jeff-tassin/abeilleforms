/*
 * Copyright (c) 2004 JETA Software, Inc.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JETA Software nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeta.forms.defaults;

import java.util.logging.Logger;

import com.jeta.forms.colormgr.ColorManager;
import com.jeta.forms.colormgr.DefaultColorManager;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.DefaultFormComponentFactory;
import com.jeta.forms.gui.form.FormComponentFactory;
import com.jeta.forms.logger.ConsoleHandler;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.project.RuntimeProjectManager;
import com.jeta.forms.store.bean.BeanSerializerFactory;
import com.jeta.forms.store.bean.DefaultBeanSerializerFactory;
import com.jeta.open.registry.JETARegistry;

/**
 * This class loads the JETARegistry with default objects required by the Forms
 * runtime system.
 * 
 * @author Jeff Tassin
 */
public class DefaultInitializer {
	/**
	 * Flag that is stored in the JETARegistry to indicate that we've performed
	 * initialization. This is needed in case initialize() is called multiple
	 * times.
	 */
	private static final String INIT_FLAG = "forms.initialized";

	/**
	 * Initializes the components needed by the Forms system.
	 */
	public static void initialize() {
		synchronized (DefaultInitializer.class) {
			Boolean binit = (Boolean) JETARegistry.lookup(INIT_FLAG);
			if (!Boolean.TRUE.equals(binit)) {
				/** required for the forms framework */
				com.jeta.open.defaults.DefaultInitializer.initialize();
				JETARegistry.rebind(ColorManager.COMPONENT_ID, new DefaultColorManager());
				JETARegistry.rebind(RuntimeProjectManager.COMPONENT_ID, new RuntimeProjectManager());
				JETARegistry.rebind(BeanSerializerFactory.COMPONENT_ID, new DefaultBeanSerializerFactory());
				JETARegistry.rebind(FormComponentFactory.COMPONENT_ID, new DefaultFormComponentFactory());
				JETARegistry.rebind(INIT_FLAG, Boolean.TRUE);

				try {
					/**
					 * Initialize the logging system only if debugging.
					 */
					if (FormUtils.isDebug()) {
						Logger logger = Logger.getLogger(FormsLogger.LOGGER_NAME);
						if (logger != null) {
							logger.setLevel(java.util.logging.Level.FINEST);
							ConsoleHandler handler = new ConsoleHandler();
							logger.addHandler(handler);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}
}
