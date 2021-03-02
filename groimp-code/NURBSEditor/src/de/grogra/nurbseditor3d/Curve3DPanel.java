/*
 * Copyright (C) 2020 GroIMP Developer Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/**
 * @author      elsaromm
 * @version     1.0                                      
 * @since       2022.10.30
 */
package de.grogra.nurbseditor3d;


import javax.media.opengl.GLJPanel;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Point4f;

import com.sun.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

import de.grogra.graph.GraphState;
import de.grogra.graph.impl.GraphManager;
import de.grogra.imp3d.glsl.GLDisplay;
import de.grogra.imp3d.objects.NURBSCurve;
import de.grogra.math.BSplineOfVertices;
import de.grogra.math.VertexListImpl;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.IO;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.swing.PanelSupport;
import de.grogra.pf.ui.swing.SwingPanel;


class AddingListener implements ActionListener{
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		if(((JButton) (source)).getText() == "add NURBS-Curve") {
			ObjectGeometry3D.initCurve();
		}
		else {
			ObjectGeometry3D.initSurface();
		}	
	}
}

class CopyListener implements ActionListener{
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		if(((JButton) (source)).getText() == "copy NURBS-Curve") {
			NURBSCurve copiedCurve = new NURBSCurve();
			int index = ObjectGeometry3D.getCurveCounter()-1;

			float oldData[] = ObjectGeometry3D.getCurveData(ObjectGeometry3D.getNURBSCurve(index));
			float data[] = new float[oldData.length];
			
			if(data != null) {				
				int size = data.length;
				for(int i = 0; i < size; i++) {
					data[i] = oldData[i];
					if(i % 4 == 1) {
						data[i] += 1;
					}
				}
			
				VertexListImpl vertexlist = new VertexListImpl();
				vertexlist.setData(data);
				vertexlist.setDimension(4);
				BSplineOfVertices bspline = new BSplineOfVertices(vertexlist, 3, false, false);
				bspline.setRational(true);
				copiedCurve.setCurve(bspline);
				ObjectGeometry3D.addCurve(copiedCurve);
			}
		}
		else {
			int index = ObjectGeometry3D.getSurfaceCounter();
			if(index > 0){
				float data[] = ObjectGeometry3D.getSurfaceData(ObjectGeometry3D.getNURBSSurface(index-1));
				if(data != null) {				
					int size = data.length;
					ObjectGeometry3D.initSurface();
					for(int i = 0; i < size/4; i++) {	
						ObjectGeometry3D.addSurfacePoint(new Point4f(data[i * 4], data[i*4 + 1]+1, data[i * 4 + 2], data[i * 4 + 3]));	
					}
				}
			}
		}	
	}
}


class ExportListener implements ActionListener{
	private Workbench workbench;
	NURBSCurve curve = ObjectGeometry3D.getNURBSCurve(0);

	public ExportListener(Workbench w) {
		workbench = w;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String output = ObjectGeometry3D.createRGGText();
		File f = new File("try1.rgg");
		String filename = "try1.rgg";
		BufferedWriter writer;
		try {
			f.createNewFile();
			f.deleteOnExit();
				
			writer = new BufferedWriter(new FileWriter(f));
			writer.write(output);
			writer.close();		
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			FileSource fs = FileSource.createFileSource(filename, IO.getMimeType(filename), workbench, null);
			workbench.open(fs, null);
		}catch(NullPointerException e) {
			e.printStackTrace();
		}
	}
}


class ResetListener implements ActionListener{
	Curve3DPanel panel;
	
	public ResetListener(Curve3DPanel p) {
		panel = p;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String text = getButtonText(panel.group);
		if(text == "Curve") {
			ObjectGeometry3D.deleteCurve();
		}else if(text == "Surface" ) {
			ObjectGeometry3D.deleteSurface();	
		}else if(text == "Box") {
			ObjectGeometry3D.deleteQuad();	
		}else if(text == "Cone") {
			ObjectGeometry3D.deleteCone();	
		}else if(text == "Sphere") {
			ObjectGeometry3D.deleteSphere();	
		}else if(text == "Cylinder") {
			ObjectGeometry3D.deleteCylinder();	
		}else if(text == "Frustum") {
			ObjectGeometry3D.deleteFrustum();	
		}
	}
		
	public String getButtonText(ButtonGroup group) {
        for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
            	return button.getText();
            }
        }
        return null;
    }
}


class RotationSliderListener implements ChangeListener{
	NURBSDisplay3D display;
	JSlider slider;
	public RotationSliderListener(JSlider slider, NURBSDisplay3D display) {
		this.display = display;
		this.slider = slider;
	}
	
	@Override
	public void stateChanged(ChangeEvent event) {
		int[] r = NURBSDisplay3D.getRotation();

		if(slider.getName() == "rotateYSlider") {
			r[1] = slider.getValue() ;
		}else if(slider.getName() == "rotateZSlider"){
			r[2] = slider.getValue();
		}
		display.setRotation(r);
	}
}


class RadioListener implements ActionListener{
	
	@Override
	public void actionPerformed(ActionEvent event) {
		Object object = event.getSource();
		String text =((JRadioButton) object).getText();
		NURBSDisplay3D.setGeometry(text);
	}
}


class SpinnerListener implements ChangeListener{

	@Override
	public void stateChanged(ChangeEvent e) {
		int value = (int) ((JSpinner)(e.getSource())).getValue();
		int surface = ObjectGeometry3D.getSurfaceCounter()-1;
		ObjectGeometry3D.setUCount(surface, value);
	}
}



public class Curve3DPanel extends PanelSupport{
	GLJPanel glpanel = new GLJPanel();
			
	GLDisplay display = new GLDisplay();
	public static JSlider rotateYSlider = new JSlider(-180, 180, 0);
	public static JSlider rotateZSlider = new JSlider(JSlider.VERTICAL, -180, 180, 0);
	
	ButtonGroup group = new ButtonGroup();

	
	public Curve3DPanel(Workbench workbench) {
		super(new SwingPanel(null));
		Registry r = workbench.getRegistry();
		GraphManager graph = r.getProjectGraph();
		GraphState state = graph.getMainState();
		

		NURBSDisplay3D display = new NURBSDisplay3D(glpanel, state);
				
		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(2, 3, 2, 2));
		JButton addCurveButton = new JButton("add NURBS-Curve");
		JButton addSurfaceButton = new JButton("add NURBS-Surface");
		JButton deleteButton = new JButton("delete");
		JButton copyCurveButton = new JButton("copy NURBS-Curve");
		JButton copySurfaceButton = new JButton("copy NURBS-Surface");
		JButton exportButton = new JButton("create rgg-file");
		addCurveButton.addActionListener(new AddingListener());
		addSurfaceButton.addActionListener(new AddingListener());
		copyCurveButton.addActionListener(new CopyListener());
		copySurfaceButton.addActionListener(new CopyListener());
		deleteButton.addActionListener(new ResetListener(this));
		exportButton.addActionListener(new ExportListener(workbench));
		p1.add(addCurveButton);
		p1.add(copyCurveButton);
		p1.add(deleteButton);
		p1.add(addSurfaceButton);
		p1.add(copySurfaceButton);		
		p1.add(exportButton);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());           

		glpanel.addGLEventListener(display);
		glpanel.addMouseListener(new InputListener());
		glpanel.addMouseWheelListener(new WheelListener());
		FPSAnimator animator = new FPSAnimator(glpanel, 60);
		animator.start();
	   
		JPanel p3 = new JPanel();
//		p3.setLayout(new GridLayout());
		rotateYSlider.setName("rotateYSlider");
		rotateYSlider.addChangeListener(new RotationSliderListener(rotateYSlider, display));
		rotateYSlider.setMajorTickSpacing(45);
		rotateYSlider.setMinorTickSpacing(15);
		rotateYSlider.setPaintTicks(true);
		rotateYSlider.setPaintLabels(true);
		p3.add(rotateYSlider);
		

		JPanel p5 = new JPanel();
		//p5.setLayout(new GridLayout());
		rotateZSlider.setName("rotateZSlider");
		rotateZSlider.addChangeListener(new RotationSliderListener(rotateZSlider, display));
		rotateZSlider.setMajorTickSpacing(45);
		rotateZSlider.setMinorTickSpacing(15);
		rotateZSlider.setPaintTicks(true);
		rotateZSlider.setPaintLabels(true);
		p5.add(rotateZSlider);
	
		
		JPanel p7 = new JPanel(new GridLayout(2, 6, 5, 5));
		JRadioButton curveButton = new JRadioButton("Curve", true);
		JRadioButton surfaceButton = new JRadioButton("Surface");
		JRadioButton rectangleButton = new JRadioButton("Box");
		JRadioButton triangleButton = new JRadioButton("Cone");
		JRadioButton circleButton = new JRadioButton("Sphere");
		JRadioButton cylinderButton = new JRadioButton("Cylinder");
		JRadioButton frustumButton = new JRadioButton("Frustum");
		curveButton.addActionListener(new RadioListener());
		surfaceButton.addActionListener(new RadioListener());
		rectangleButton.addActionListener(new RadioListener());
		triangleButton.addActionListener(new RadioListener());
		circleButton.addActionListener(new RadioListener());
		cylinderButton.addActionListener(new RadioListener());
		frustumButton.addActionListener(new RadioListener());
		group.add(curveButton);
		group.add(surfaceButton);
		group.add(rectangleButton);
		group.add(triangleButton);
		group.add(circleButton);
		group.add(cylinderButton);
		group.add(frustumButton);
		p7.add(curveButton);
		p7.add(surfaceButton);
		p7.add(rectangleButton);
		p7.add(triangleButton);
		p7.add(circleButton);
		p7.add(cylinderButton);
		p7.add(frustumButton);
		
		
		JLabel uCount = new JLabel("uCount");
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
		spinner.addChangeListener(new SpinnerListener());
		p7.add(uCount);
		p7.add(spinner);
		
		JTabbedPane register = new JTabbedPane();
		register.addTab("Object", p1);
		register.addTab("Objects", p7);
		
		panel.add(register, BorderLayout.NORTH);
		panel.add(glpanel, BorderLayout.CENTER);
		panel.add(p3, BorderLayout.SOUTH);
		panel.add(p5, BorderLayout.EAST);
		((SwingPanel) getComponent()).getContentPane().add(panel);
	}
}