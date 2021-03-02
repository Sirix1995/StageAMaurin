package de.grogra.animation.timeline;

public class TimeContext {

	private int currentTime;
	private int start;
	private int end;
	private int fps;
	private int timeSteps;
	private boolean stepOnExecution;
	
	public TimeContext(int start, int end, int fps, int timeSteps, boolean stepOnExecution) {
		this.start = start;
		this.end = end;
		this.fps = fps;
		this.timeSteps = timeSteps;
		this.stepOnExecution = stepOnExecution;
	}
	
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getTimeSteps() {
		return timeSteps;
	}
	public void setTimeSteps(int timeSteps) {
		this.timeSteps = timeSteps;
	}
	public int getFps() {
		return fps;
	}
	public void setFps(int fps) {
		this.fps = fps;
	}
	public int getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
	}
	public boolean isStepOnExecution() {
		return stepOnExecution;
	}
	public void setStepOnExecution(boolean stepOnExecution) {
		this.stepOnExecution = stepOnExecution;
	}
}
