package de.grogra.animation.timeline;

import java.util.List;
import de.grogra.animation.AnimCore;
import de.grogra.animation.AnimJob;
import de.grogra.animation.Init;
import de.grogra.animation.TimeChangeListener;
import de.grogra.animation.util.Debug;
import de.grogra.pf.ui.Context;

public class AnimPlayer extends Thread {
	
	final private Timeline timeline;
	final private TimeContext  timeContext;
	final private List<TimeChangeListener> timeChangeListenerList;
	final private Context ctx;
	
	public AnimPlayer(Context ctx, Timeline timeline,
			TimeContext timeContext, List<TimeChangeListener> timeChangeListenerList) {
		this.timeline = timeline;
		this.timeContext = timeContext;
		this.timeChangeListenerList = timeChangeListenerList;
		this.ctx = ctx;
	}
	
	public void run() {
		AnimCore animCore = (AnimCore) ctx.getWorkbench().getProperty(Init.ANIMCORE);
		
		int newTime = timeContext.getCurrentTime();
		
		long frameLength = (long) (1000 * (1.0 / timeContext.getFps()));
		long prevMillis = System.currentTimeMillis();
		long currMillis = 0;
		long diffMillis = 0;
		long restMillis = 0;
		
		while (!isInterrupted()) {
			
			AnimJob job = new AnimJob(newTime, ctx) {
				@Override
				protected void runImpl(Object arg, Context ctx) {
					for (TimeChangeListener timeChangeListener : timeChangeListenerList) {
						timeChangeListener.timeChanged((Integer) arg, true);
					}
				}
			};
			job.execute();
			// wait on finish execution
			synchronized (job) {
				try {
					while (!job.isDone()) {
						job.wait();
					}
				} catch (InterruptedException e) {interrupt();}
			}
		
			currMillis = System.currentTimeMillis();
			diffMillis = currMillis - prevMillis;
			prevMillis = currMillis;
			
			restMillis = frameLength - diffMillis;
			Debug.println("restMillis: " + restMillis);
			if (restMillis > 0) {
				// update to fast, wait a little more time
				try {
					Thread.sleep(restMillis);
				} catch (InterruptedException e) {interrupt();}					
			}
			else {
				// update to slow, skip frames
				long framesToSkip = Math.abs(restMillis) / frameLength;
				newTime += framesToSkip;
			}		
			newTime++;
			if (!animCore.getAnimManager().getTimelineManager().setCurrentTime(newTime)) {
				break;
			}
		}
		timeline.getTimelineControls().getPlayAnimationButton().setSelected(false);
		timeline.getTimelineControls().setControlsState(true);
	}

 };