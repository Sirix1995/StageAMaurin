package de.grogra.animation.timeline;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;

public class TimelineControls {

	static class AnimationPlayButton extends JToggleButton {
		private static final long serialVersionUID = -6875025791177021030L;
		static String PLAY_STRING = "Play Animation";
		static String STOP_STRING = "Stop Animation";
		public void setSelected(boolean b) {
			super.setSelected(b);
			if (b)
				this.setText(STOP_STRING);
			else
				this.setText(PLAY_STRING);
		}
	}
	
	private JButton gotoStartButton;
	private JButton previousFrameButton;
	private JToggleButton playAnimationButton;
	private JButton nextFrameButton;
	private JButton gotoEndButton;
	private JSpinner frameField;
	private JButton preferencesButton;
	private JToggleButton autoKeyButton;
	private JButton clearAnimationButton;
	private SpinnerNumberModel model;
	
	public TimelineControls() {
		gotoStartButton = new JButton("Goto Start");
		previousFrameButton = new JButton("Previous Frame");
		playAnimationButton = new AnimationPlayButton();
		playAnimationButton.setSelected(false);
		nextFrameButton = new JButton("Next Frame");
		gotoEndButton = new JButton("Goto End");
		model = new SpinnerNumberModel();
		frameField = new JSpinner(model);
		Dimension d = new Dimension(70, 20);
		frameField.setPreferredSize(d);
		frameField.setMinimumSize(d);
		preferencesButton = new JButton("Animation Preferences");
		clearAnimationButton = new JButton("Clear Animation");
		autoKeyButton = new JToggleButton("Auto Key");
	}

	public JButton getGotoStartButton() {
		return gotoStartButton;
	}

	public JButton getPreviousFrameButton() {
		return previousFrameButton;
	}

	public JToggleButton getPlayAnimationButton() {
		return playAnimationButton;
	}

	public JButton getNextFrameButton() {
		return nextFrameButton;
	}

	public JButton getGotoEndButton() {
		return gotoEndButton;
	}

	public JSpinner getFrameField() {
		return frameField;
	}

	public JButton getPreferencesButton() {
		return preferencesButton;
	}
	
	public JToggleButton getAutoKeyButton() {
		return autoKeyButton;
	}

	public JButton getClearAnimationButton() {
		return clearAnimationButton;
	}

	public void updateTimeContext(TimeContext timeContext) {
		model.setMinimum(timeContext.getStart());
		model.setMaximum(timeContext.getEnd());
	}
	
	public void setControlsState(boolean state) {
		gotoStartButton.setEnabled(state);
		previousFrameButton.setEnabled(state);
		nextFrameButton.setEnabled(state);
		gotoEndButton.setEnabled(state);
		frameField.setEnabled(state);
		preferencesButton.setEnabled(state);
		autoKeyButton.setEnabled(state);
		clearAnimationButton.setEnabled(state);
	}

}
