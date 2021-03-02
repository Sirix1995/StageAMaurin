package de.grogra.animation.timeline;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.grogra.animation.AnimCore;
import de.grogra.animation.Init;
import de.grogra.animation.TimeChangeListener;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.swing.PanelSupport;
import de.grogra.pf.ui.swing.SwingPanel;
import de.grogra.pf.ui.swing.WindowSupport;
import de.grogra.util.Map;

public class TimelineManager implements ChangeListener, ActionListener {

	final private static String ANIMTIMELINEPANEL = "AnimTimelinePanel";
	final private static String ANIMTIMELINEPANELPARAMS = "AnimTimelinePanelParams";
	
	final private Workbench wb;
	final private Timeline timeline;
	
	final private ArrayList<TimeChangeListener> timeChangeListenerList;
	final private Set<Integer> keys;
	final private TimeContext timeContext;

	private AnimPlayer animPlayer;
	
	public TimelineManager(Workbench wb) {
		this.wb = wb;
		
		timeChangeListenerList = new ArrayList<TimeChangeListener>();
		keys = new TreeSet<Integer>();
		timeContext = new TimeContext(0, 100, 25, 10, true);
		timeline = new Timeline(timeContext);
		timeline.getTimelineSlider().addChangeListener(this);
		timeline.getTimelineControls().getGotoStartButton().addActionListener(this);
		timeline.getTimelineControls().getPreviousFrameButton().addActionListener(this);
		timeline.getTimelineControls().getPlayAnimationButton().addActionListener(this);
		timeline.getTimelineControls().getNextFrameButton().addActionListener(this);
		timeline.getTimelineControls().getGotoEndButton().addActionListener(this);
		timeline.getTimelineControls().getFrameField().addChangeListener(this);
		timeline.getTimelineControls().getPreferencesButton().addActionListener(this);
		
		Panel panel = (Panel) wb.getProperty(ANIMTIMELINEPANEL);
		if (panel != null) {
			fillPanel(wb, this, panel, (Map) wb.getProperty(ANIMTIMELINEPANELPARAMS));
		}
	}
	
	public static void fillPanel(Workbench wb, TimelineManager timelineManager, Panel panel, Map params) {
		Timeline timeline = timelineManager.getTimeline();
		JPanel controlsPanel = timelineManager.getControlsPanel();
		ArrayList<TimeChangeListener> timeChangeListenerList = timelineManager.getTimeChangeListenerList();
		TimeContext timeContext = timelineManager.getTimeContext();
		
		// create view
		Container c = ((SwingPanel) panel.getComponent()).getContentPane();
		((PanelSupport)panel).initialize((WindowSupport) wb.getWindow(), params);

		c.removeAll();
		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
		
		// set sliders
		c.add(timeline.getKeyframeSlider());
		c.add(timeline.getTimelineSlider());
		// necessary to avoid an error when painted
		c.addComponentListener(timeline.getKeyframeSlider());
		c.addContainerListener(timeline.getKeyframeSlider());
		
		// set buttons
		c.add(controlsPanel);
		
		// TODO: replace with time read from loaded project
		for (TimeChangeListener timeChangeListener : timeChangeListenerList) {
			timeChangeListener.timeChanged(timeContext.getCurrentTime(), false);
		}
	}

	public static Panel createTimeline (Context ctx, Map params) {
		Workbench wb = Workbench.current();

		UIToolkit ui = UIToolkit.get(wb);
		Panel panel = ui.createPanel(wb, null, params);
		
		wb.setProperty(ANIMTIMELINEPANEL, panel);
		wb.setProperty(ANIMTIMELINEPANELPARAMS, params);
		
		AnimCore animCore = (AnimCore) wb.getProperty(Init.ANIMCORE);
		fillPanel(wb, animCore.getAnimManager().getTimelineManager(), panel, params);
		
		return panel;
	}
	
	public Timeline getTimeline() {
		return timeline;
	}
	
	public int getCurrentTime() {
		return timeContext.getCurrentTime();
	}
	
	public boolean setCurrentTime(int time) {
		if ((time < timeContext.getStart()) || (time > timeContext.getEnd()))
			return false;
		timeContext.setCurrentTime(time);
		timeline.setCurrentTime(time);
		return true;
	}
	
	public boolean increaseCurrentTime(int difference) {
		return setCurrentTime(getCurrentTime() + difference);
	}
	
	/**
	 * Increases current time by time steps value.
	 * @return
	 */
	public void increaseCurrentTime() {
		int timesteps = timeContext.getTimeSteps();
		if (timesteps > 0) {
			setCurrentTime(getCurrentTime() + timesteps);
		}
	}
	
	public void setTimelineRange(int start, int end) {
		timeContext.setStart(start);
		timeContext.setEnd(end);
		timeline.updateTimeContext(timeContext);
		updateKeys();
	}
	
	public void addKey(int key) {
		keys.add(key);
	}
	
	public void removeKey(int key) {
		keys.remove(key);
	}
	
	public void setKeys(Set<Integer> keys) {
		deleteKeys();
		this.keys.addAll(keys);
	}
	
	public void updateKeys() {
		timeline.deleteKeyframes();
		for (Integer key : keys) {
			if ((key >= timeContext.getStart()) && (key <= timeContext.getEnd())) {
				timeline.addKeyframe(key);
			}
		}
	}
	
	public void deleteKeys() {
		keys.clear();
		timeline.deleteKeyframes();
	}
	
	public void addTimeChangeListener(TimeChangeListener timeChangeListener) {
		timeChangeListenerList.add(timeChangeListener);
	}
	
	public void removeTimeChangeListener(TimeChangeListener timeChangeListener) {
		timeChangeListenerList.remove(timeChangeListener);
	}
	
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		int newTime = getCurrentTime();
		int oldTime = newTime;
		if (source.equals(timeline.getTimelineSlider())) {
			// state of timelineSlider has changed
			newTime = timeline.getTimelineSlider().getValue();
		}
		else if (source.equals(timeline.getTimelineControls().getFrameField())) {
			// state of frameField has changed
			newTime = (Integer) timeline.getTimelineControls().getFrameField().getValue();
		}
		if (oldTime != newTime) {
			for (TimeChangeListener timeChangeListener : timeChangeListenerList) {
				timeChangeListener.timeChanged(newTime, false);
			}		
		}
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		int oldTime = getCurrentTime();
		int newTime = oldTime;
		
		if (source.equals(timeline.getTimelineControls().getGotoStartButton())) {
			newTime = timeContext.getStart();
		}
		else if (source.equals(timeline.getTimelineControls().getPreviousFrameButton())) {
			increaseCurrentTime(-1);
			newTime = getCurrentTime();
		}
		else if (source.equals(timeline.getTimelineControls().getPlayAnimationButton())) {
			JToggleButton playButton = timeline.getTimelineControls().getPlayAnimationButton();
			if (playButton.isSelected()) {
				// deactive all time controls except playAnimationButton
				timeline.getTimelineControls().setControlsState(false);
				timeline.getTimelineControls().getPlayAnimationButton().setSelected(true);
				animPlayer = new AnimPlayer(wb, timeline, timeContext, timeChangeListenerList);
				animPlayer.start();
			}
			else {
				animPlayer.interrupt();
			}
		}
		else if (source.equals(timeline.getTimelineControls().getNextFrameButton())) {
			increaseCurrentTime(1);
			newTime = getCurrentTime();
		}
		else if (source.equals(timeline.getTimelineControls().getGotoEndButton())) {
			newTime = timeContext.getEnd();
		}
		else if (source.equals(timeline.getTimelineControls().getPreferencesButton())) {
			// open extra window with preferences
			PreferencesDialog preferencesDialg = new PreferencesDialog(timeContext);
			preferencesDialg.setVisible(true);
			if (preferencesDialg.hasPressedOk()) {
				timeline.updateTimeContext(timeContext);
				updateKeys();
			}
		}
		if (newTime != oldTime) {
			for (TimeChangeListener timeChangeListener : timeChangeListenerList) {
				timeChangeListener.timeChanged(newTime, false);
			}	
		}
	}
	
	public TimeContext getTimeContext() {
		return timeContext;
	}

	public JPanel getControlsPanel() {
		return timeline.getControlsPanel();
	}

	public ArrayList<TimeChangeListener> getTimeChangeListenerList() {
		return timeChangeListenerList;
	}
	
}
