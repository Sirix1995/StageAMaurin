package de.grogra.animation.timeline;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JSlider;

public class TimelineSlider extends JSlider implements MouseListener {

	private static final long serialVersionUID = -745479214911207932L;

	public TimelineSlider() {
		super();
		this.addMouseListener(this);
	}
	
	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		// click anywhere on slider an knob jumps to this position
//		int newPos = (int) (this.getMinimum() + ((this.getMaximum() - this.getMinimum()) * (e.getX()-10.0)) / (getWidth()-20.0));
//		setValue(newPos);
	}

	public void mouseReleased(MouseEvent e) {
		
	}
	
}
