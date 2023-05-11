package test.jeta.forms.gui.form;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.jeta.forms.components.panel.FormPanel;


public class TestFormPanel {

	public static void main(String[] args ) {
		System.out.println( "starting test...");
		JFrame frame = new JFrame();
		FormPanel panel = new FormPanel("com/jeta/swingbuilder/gui/main/columnSpec.frm");
		frame.getContentPane().add(panel);

		// set the size and location of this frame
		frame.setSize(600, 500);
		frame.setLocation(200, 100);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		});
	}

}
