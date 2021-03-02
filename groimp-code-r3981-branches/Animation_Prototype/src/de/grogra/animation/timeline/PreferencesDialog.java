package de.grogra.animation.timeline;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

public class PreferencesDialog extends JDialog {

	private static final long serialVersionUID = 5125393355411817367L;
	private static final int width = 300;
	private static final int height = 180;
	
	JFormattedTextField startField;
	JFormattedTextField endField;
	JFormattedTextField fpsField;
	JFormattedTextField stepsField;
	JCheckBox stepOnExecution;
	
	TimeContext timeCtx;
	
	boolean pressedOk = false;
	
	public PreferencesDialog(TimeContext timeContext) {
		this.setModal(true);
		this.timeCtx = timeContext;
		
		this.setLocationRelativeTo(null);
		this.setTitle("Animation Preferences");
		this.setLayout(new GridLayout(6, 2, 6, 3));
		
		DecimalFormat df = new DecimalFormat("#");		
		
		// start frame
		JLabel startFieldLabel = new JLabel("Start Frame:");
		this.add(startFieldLabel);
		startField = new JFormattedTextField(df);
		startField.setValue(timeContext.getStart());
		this.add(startField);
		// end frame
		JLabel endFieldLabel = new JLabel("Ende Frame:");
		this.add(endFieldLabel);
		endField = new JFormattedTextField(df);
		endField.setValue(timeContext.getEnd());
		this.add(endField);
		// fps
		this.add(new JLabel("FPS:"));
		fpsField = new JFormattedTextField(df);
		fpsField.setValue(timeContext.getFps());
		this.add(fpsField);
		// time steps
		this.add(new JLabel("Time Steps:"));
		stepsField = new JFormattedTextField(df);
		stepsField.setValue(timeContext.getTimeSteps());
		this.add(stepsField);
		// time step on xl execution
		this.add(new JLabel("Time step on XL execution"));
		stepOnExecution = new JCheckBox();
		stepOnExecution.setSelected(timeContext.isStepOnExecution());
		this.add(stepOnExecution);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timeCtx.setStart(Integer.parseInt(startField.getText()));
				timeCtx.setEnd(Integer.parseInt(endField.getText()));
				timeCtx.setTimeSteps(Integer.parseInt(stepsField.getText()));
				timeCtx.setFps(Integer.parseInt(fpsField.getText()));
				timeCtx.setStepOnExecution(stepOnExecution.isSelected());
				pressedOk = true;
				PreferencesDialog.this.setVisible(false);
			}
		});
		this.add(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PreferencesDialog.this.setVisible(false);
			}
		});
		this.add(cancelButton);
		
		this.setSize(width, height);
		Point p = this.getLocation();
		p.x -= width / 2;
		p.y -= height / 2;
		this.setLocation(p);
		this.setResizable(false);
	}
	
	public boolean hasPressedOk() {
		return pressedOk;
	}
	
}
