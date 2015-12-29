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

package com.jeta.swingbuilder.gui.beanmgr;

import java.awt.Component;

import com.jeta.forms.gui.common.FormException;
import com.jeta.open.i18n.I18N;
import com.jeta.open.rules.JETARule;
import com.jeta.open.rules.RuleResult;
import com.jeta.swingbuilder.gui.components.TSErrorDialog;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * Validator for the look and feel info view
 * 
 * @author Jeff Tassin
 */
public class BeanDefinitionValidator implements JETARule {
	/**
	 * Validates the look and feel view.
	 * 
	 * @param params
	 *            a 2 element array that contains a BeanDefinitionView and a
	 *            BeanLoader
	 */
	public RuleResult check(Object[] params) {
		BeanDefinitionView view = (BeanDefinitionView) params[0];
		String classname = FormDesignerUtils.fastTrim(view.getText(BeanDefinitionNames.ID_BEAN_CLASS));

		if (classname.length() == 0)
			return new RuleResult(I18N.getLocalizedMessage("Invalid class name"));

		BeanLoader loader = (BeanLoader) params[1];
		try {
			Component comp = loader.createBean(classname);
		} catch (FormException fe) {
			Exception e = fe;
			if (fe.getSourceException() != null)
				e = fe.getSourceException();

			if (e instanceof ClassNotFoundException)
				handleException(view, (ClassNotFoundException) e);
			else if (e instanceof IllegalAccessException)
				handleException(view, (IllegalAccessException) e);
			else if (e instanceof ClassCastException)
				handleException(view, (ClassCastException) e);
			else if (e instanceof InstantiationException)
				handleException(view, (InstantiationException) e);
			else
				handleException(view, e);

			return new RuleResult(RuleResult.FAIL_NO_MESSAGE_ID);
		} catch (Exception e) {
			handleException(view, e);
			return new RuleResult(RuleResult.FAIL_NO_MESSAGE_ID);
		}
		return RuleResult.SUCCESS;
	}

	private void handleException(Component view, ClassNotFoundException e) {
		String caption = I18N.getLocalizedMessage("Unable to instantiate bean");
		String msg = "The bean class was not found in the classpath.  Make sure you have included all JAR files and paths required by this bean in the classpaths list.";
		TSErrorDialog dlg = TSErrorDialog.createDialog(view, caption, msg, e);
		dlg.showCenter();
	}

	private void handleException(Component view, IllegalAccessException e) {
		String caption = I18N.getLocalizedMessage("Unable to instantiate bean");
		String msg = "An illegal access exception occurred.  Please verify that the bean is declared in a public class and that the bean has a public no-arg constructor.";
		TSErrorDialog dlg = TSErrorDialog.createDialog(view, caption, msg, e);
		dlg.showCenter();
	}

	private void handleException(Component view, ClassCastException e) {
		String caption = I18N.getLocalizedMessage("Unable to instantiate bean");
		String msg = "The given bean class does not appear to be a valid Swing component.  Please verify that the bean has javax.swing.JComponent has a superclass";
		TSErrorDialog dlg = TSErrorDialog.createDialog(view, caption, msg, e);
		dlg.showCenter();
	}

	private void handleException(Component view, InstantiationException e) {
		String caption = I18N.getLocalizedMessage("Unable to instantiate bean");
		String msg = "Please verify that the bean has a public no-arg constructor.";
		TSErrorDialog dlg = TSErrorDialog.createDialog(view, caption, msg, e);
		dlg.showCenter();
	}

	private void handleException(Component view, Exception e) {
		String caption = I18N.getLocalizedMessage("Unable to instantiate bean");
		String msg = "An unknown exception occurred.  The constructor or a member variable in the bean class might be throwing an unchecked exception.";
		TSErrorDialog dlg = TSErrorDialog.createDialog(view, caption, msg, e);
		dlg.showCenter();
	}

}
