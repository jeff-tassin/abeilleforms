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

package com.jeta.forms.gui.formmgr;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import com.jeta.forms.beanmgr.BeanManager;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.project.ProjectManager;
import com.jeta.forms.store.jml.JMLException;
import com.jeta.forms.store.jml.JMLUtils;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.forms.store.memento.FormPackage;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.open.resources.ResourceLoader;

/**
 * Helper class for working with forms.
 * 
 * @author Jeff Tassin
 */
public class FormManagerUtils {
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 20;

	public static FormMemento loadForm(InputStream is) throws ClassNotFoundException, IOException, JMLException {
		/**
		 * If the input stream is an instanceof ObjectInputStream, we
		 * automatically assume it is binary.
		 */
		if (is instanceof ObjectInputStream) {
			FormMemento memento = null;
			Object obj = ((ObjectInputStream) is).readObject();
			if (obj instanceof FormPackage) {
				memento = ((FormPackage) obj).getMemento();
			}
			else {
				memento = (FormMemento) obj;
			}
			return memento;

		}
		else {

			/**
			 * If we are here, the file format is unknown. It could be binary or
			 * XML. We need to inspect the file contents and try to make a
			 * determination.
			 */
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] first_read = new byte[1024];
			byte[] data = new byte[DEFAULT_BUFFER_SIZE];
			int nread = is.read(first_read);

			if (nread > 0) {
				baos.write(first_read, 0, nread);
				nread = is.read(data);
			}

			while (nread > 0) {
				baos.write(data, 0, nread);
				nread = is.read(data);
			}

			/**
			 * Read the first 50 or so bytes in the file and try to determine if
			 * it is a binary or xml file.
			 */
			boolean found_binary = true;
			int BINARY_TAG_OFFSET = 4;
			String binary_tag = "sr\0(com.jeta.forms.store.memento.FormPackage";
			for (int index = 0; index < binary_tag.length(); index++) {
				if (first_read[index + BINARY_TAG_OFFSET] != (byte) binary_tag.charAt(index)) {
					found_binary = false;
					break;
				}
			}

			FormMemento memento = null;
			if (found_binary) {
				// assume the file is binary
				ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
				Object obj = ois.readObject();
				if (obj instanceof FormPackage) {
					memento = ((FormPackage) obj).getMemento();
				}
				else {
					memento = (FormMemento) obj;
				}
			}
			else {
				// assume the file is in XML format
				FormPackage fp = (FormPackage) JMLUtils.readObject(new ByteArrayInputStream(baos.toByteArray()));
				memento = fp.getMemento();
			}
			return memento;
		}
	}

	/**
	 * Opens a form from the given input stream. The input stream should refer
	 * to a valid form file.
	 * 
	 * @param istream
	 *            the input stream
	 * @return a FormComponent object that is initialized from the data in the
	 *         stream.
	 */
	public static FormComponent openForm(InputStream istream) throws FormException {
		try {
			FormMemento memento = loadForm(istream);
			FormComponent fc = FormComponent.create();
			fc.setState(memento);
			/**
			 * we don't need to set the form path here because we are in
			 * run-mode and linked vs. embedded has no meaning
			 */
			return fc;
		} catch (Exception e) {
			if (e instanceof FormException)
				throw (FormException) e;

			throw new FormException(e);
		}
	}

	/**
	 * Opens a linked form.
	 * 
	 * @param relativePath
	 *            the path of the linked form relative to the CLASSPATH when in
	 *            run mode or the source paths when in design mode.
	 * @return an initialized FormComponent object.
	 */
	public static FormComponent openPackagedForm(String relativePath) throws FormException {
		FormUtils.safeAssert(!FormUtils.isDesignMode());
		FormUtils.safeAssert(relativePath != null);
		FormUtils.safeAssert(relativePath.length() > 0);
		try {

			/**
			 * we need to do this when showing the form in the designer. Even if
			 * design mode is false, we still need to load the form using the
			 * project manager because the form might be in the cache and not
			 * yet stored back to disk. Design mode can be false in the design
			 * when doing a 'preview'
			 */
			ProjectManager pmgr = (ProjectManager) JETARegistry.lookup(ProjectManager.COMPONENT_ID);
			FormUtils.safeAssert(pmgr != null);
			if (pmgr != null) {
				String abspath = pmgr.getAbsolutePath(relativePath);
				if (abspath != null) {
					File f = new File(abspath);
					if (f.isFile()) {
						return openForm(new FileInputStream(f));
					}
				}
			}

			//FormsLogger.debug("FormManagerUtils.loadForm: " + relativePath);

			/**
			 * Replace the path separator in case the form was stored on a
			 * Windows system.
			 */
			relativePath = relativePath.replace('\\', '/');

			ResourceLoader loader = (ResourceLoader) JETARegistry.lookup(ResourceLoader.COMPONENT_ID);
			BufferedInputStream bis = new BufferedInputStream(loader.getResourceAsStream(relativePath), DEFAULT_BUFFER_SIZE);

			FormMemento memento = loadForm(bis);
			FormComponent fc = FormComponent.create();
			fc.setState(memento);
			bis.close();

			/**
			 * we don't need to set the form path here because we are in
			 * run-mode and linked vs. embedded has no meaning
			 */
			return fc;
		} catch (Exception e) {
			try {
				/**
				 * if we are here then it is probably a situation where the form
				 * is embedded in a custom bean
				 */
				BeanManager bm = (BeanManager) JETARegistry.lookup(BeanManager.COMPONENT_ID);
				if (bm != null) {
					ClassLoader loader = bm.getClassLoader();
					return openForm(loader.getResourceAsStream(relativePath));
				}
			} catch (Exception bme) {
				// ignore
			}

			System.out.println("FormManagerUtils.openPackgedForm failed: " + relativePath);
			e.printStackTrace();
			if (e instanceof FormException)
				throw (FormException) e;

			FormsLogger.severe(I18N.format("Error_loading_form_1", relativePath));
			throw new FormException(e);
		}
	}
}
