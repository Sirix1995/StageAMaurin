package de.grogra.animation.timeline;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;


public class Timeline {

	final private TimelineControls timelineControls;
	final private JPanel controlsPanel;
	
	private TimelineSlider timelineSlider;
	private MThumbSlider keyframeSlider;
	
	private int minorTickSpacing = 1;
	private int majorTickSpacing = 10;
		
	public Timeline(TimeContext timeContext) {
		timelineControls = new TimelineControls();
		
		controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		controlsPanel.setMinimumSize(new Dimension(500, 60));
		controlsPanel.add(timelineControls.getGotoStartButton());
		controlsPanel.add(timelineControls.getPreviousFrameButton());
		controlsPanel.add(timelineControls.getPlayAnimationButton());
		controlsPanel.add(timelineControls.getNextFrameButton());
		controlsPanel.add(timelineControls.getGotoEndButton());
		controlsPanel.add(timelineControls.getFrameField());
		controlsPanel.add(timelineControls.getPreferencesButton());
		controlsPanel.add(timelineControls.getAutoKeyButton());
		controlsPanel.add(timelineControls.getClearAnimationButton());
		
		this.timelineSlider = new TimelineSlider();
		timelineSlider.setUI(new com.sun.java.swing.plaf.windows.WindowsSliderUI(timelineSlider));
		timelineSlider.setMinorTickSpacing(minorTickSpacing);
		timelineSlider.setMajorTickSpacing(majorTickSpacing);
		timelineSlider.setPaintTicks(true);
		timelineSlider.setPaintLabels(true);
		timelineSlider.setSnapToTicks(true);
		timelineSlider.setEnabled(true);
		timelineSlider.setValue(timeContext.getCurrentTime());

		this.keyframeSlider = new MThumbSlider();
		keyframeSlider.setUI(new WindowsMThumbSliderUI());
		keyframeSlider.setMinorTickSpacing(minorTickSpacing);
		keyframeSlider.setMajorTickSpacing(majorTickSpacing);
		keyframeSlider.setPaintTicks(true);
		keyframeSlider.setPaintLabels(true);
	    keyframeSlider.setSnapToTicks(true);
	    keyframeSlider.setEnabled(false);
	    
	    this.updateTimeContext(timeContext);
	}
	
	public void setCurrentTime(int time) {
		timelineSlider.setValue(time);
		timelineControls.getFrameField().setValue(time);
	}
	
	public void updateTimeContext(TimeContext timeContext) {
		timelineSlider.setMinimum(timeContext.getStart());
		keyframeSlider.setMinimum(timeContext.getStart());
		timelineSlider.setMaximum(timeContext.getEnd());
		keyframeSlider.setMaximum(timeContext.getEnd());
		timelineControls.updateTimeContext(timeContext);
	}
	
	public void addKeyframe(int time) {
		keyframeSlider.addThumb(time);
	}
	
	public void deleteKeyframes() {
		keyframeSlider.clearThumbs();
	}
	
	public TimelineSlider getTimelineSlider() {
		return timelineSlider;
	}
	
	public MThumbSlider getKeyframeSlider() {
		return keyframeSlider;
	}
	
	public TimelineControls getTimelineControls() {
		return timelineControls;
	}

	public JPanel getControlsPanel() {
		return controlsPanel;
	}
		
}
