package de.grogra.animation.develop;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

public class TimelineMain extends JFrame {
	int min = 0;
	int max = 100;
	int value = min;
	JSlider slider = new JSlider(min, max, value);

	public TimelineMain() {
		setSize(800, 300);
		setLocation(400, 200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		slider.addMouseListener(new MouseListener() {
			public void mousePressed(MouseEvent me) {
				slider.setValue(min + ((max - min) * me.getX())
						/ slider.getWidth());
			}

			public void mouseClicked(MouseEvent me) {
			}

			public void mouseReleased(MouseEvent me) {
			}

			public void mouseEntered(MouseEvent me) {
			}

			public void mouseExited(MouseEvent me) {
			}
		});
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);
		
		JPanel panel = new JPanel();
		panel.add(slider);
		
		getContentPane().add(panel);
	}

	public static void main(String args[]) {
		new TimelineMain().setVisible(true);
	}
}
