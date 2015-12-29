package com.jeta.swingbuilder.gui.utils;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.formmgr.FormManagerUtils;
import com.jeta.forms.store.jml.JMLException;
import com.jeta.forms.store.jml.JMLUtils;
import com.jeta.forms.store.jml.dom.JMLNode;
import com.jeta.forms.store.memento.FormPackage;
import com.jeta.forms.store.memento.StateRequest;
import com.jeta.forms.store.xml.writer.XMLWriter;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.common.ComponentNames;
import com.jeta.swingbuilder.gui.project.UserPreferencesNames;
import com.jeta.swingbuilder.main.AbeilleForms;

/**
 * This is a utility class for converting forms to/from XML/binary format. You
 * must provide a valid Abeille project file or linked forms (nested forms that
 * refer to other forms on disk) will not be resolved.
 * 
 * Note that this class is in the com.jeta.swingbuilder package. This means that
 * you will need to include designer.jar as well as formsrt.jar in your
 * classpath if you decide to use this class to convert forms.
 * 
 * @author Jeff Tassin
 */
public class FormConverter {

	/**
	 * ctor creates a FormConverter object associated with the specified
	 * project.
	 * 
	 * @param projectFile
	 *            the full path and name of an Abeille project file. This is
	 *            needed to resolve linked forms. For example:
	 *            /home/jeff/abeille/myproject.jfpr
	 */
	public FormConverter(String projectFile) {
		JETARegistry.rebind(UserPreferencesNames.ID_LAST_PROJECT, projectFile);
	}

	/**
	 * Basic initialization needed by the designer when storing files.
	 */
	private static void initialize() {
		if (JETARegistry.lookup(ComponentNames.APPLICATION_STATE_STORE) == null) {
			System.setProperty("jeta1.debug", "true");
			new AbeilleForms().launch(new String[0], false);
		}
	}

	/**
	 * Converts a binary .jfrm to an XML file.
	 * 
	 * @param xmlDestFile
	 *            the path and name of an XML file to create
	 * @param binarySrcFile
	 *            the path an name of an existing binary .jfrm to read and
	 *            convert.
	 * @throws FormException
	 * @throws JMLException
	 */
	public void convertToXML(String xmlDestFile, String binarySrcFile) throws IOException, FormException, JMLException {
		convertToXML(new FileOutputStream(xmlDestFile), new FileInputStream(binarySrcFile));
	}

	/**
	 * Converts a binary .jfrm to an XML file.
	 * 
	 * @throws FormException
	 * @throws JMLException
	 * @throws IOException
	 */
	public void convertToXML(OutputStream xmlOutputStream, InputStream binaryInputStream) throws FormException, JMLException, IOException {

		initialize();
		FormUtils.setDesignMode(true);
		FormComponent fc = FormManagerUtils.openForm(binaryInputStream);
		FormPackage fpackage = new FormPackage(fc.getExternalState(StateRequest.SHALLOW_COPY));
		JMLNode node = JMLUtils.writeObject(fpackage);
		FormUtils.setDesignMode(false);

		Writer writer = new BufferedWriter(new OutputStreamWriter(xmlOutputStream));
		new XMLWriter().write(writer, node);
		writer.write('\n');
		writer.flush();
		writer.close();
	}

	/**
	 * Converts an XML form to a binary .jfrm
	 * 
	 * @param binaryDestFile
	 * @param xmlSrcFile
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws JMLException
	 * @throws FormException
	 */
	public void convertToBinary(String binaryDestFile, String xmlSrcFile) throws FileNotFoundException, ClassNotFoundException, IOException, JMLException,
			FormException {
		convertToBinary(new FileOutputStream(binaryDestFile), new FileInputStream(xmlSrcFile));
	}

	/**
	 * Converts an XML form to a binary .jfrm
	 * 
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws JMLException
	 * @throws FormException
	 */
	public void convertToBinary(OutputStream binaryOutputStream, InputStream xmlInputStream) throws ClassNotFoundException, IOException, JMLException,
			FormException {
		initialize();
		FormUtils.setDesignMode(true);
		FormComponent fc = FormManagerUtils.openForm(xmlInputStream);
		FormPackage fpackage = new FormPackage(fc.getExternalState(StateRequest.SHALLOW_COPY));
		ObjectOutputStream oos = null;
		if (binaryOutputStream instanceof ObjectOutputStream)
			oos = (ObjectOutputStream) binaryOutputStream;
		else
			oos = new ObjectOutputStream(binaryOutputStream);

		oos.writeObject(fpackage);
		oos.flush();
		oos.close();
		FormUtils.setDesignMode(false);

	}

}
