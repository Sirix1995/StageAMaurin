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
 package de.grogra.nurbseditor2d;


import javax.media.opengl.GLJPanel;
import javax.swing.*;

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
import de.grogra.imp3d.objects.NURBSCurve;
import de.grogra.math.BSplineOfVertices;
import de.grogra.math.VertexListImpl;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.IO;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.swing.PanelSupport;
import de.grogra.pf.ui.swing.SwingPanel;



class CurveListener implements ActionListener{
	@Override
	public void actionPerformed(ActionEvent arg0) {
		ObjectGeometry2D.initCurve();	
	}
}

class CopyListener implements ActionListener{
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		NURBSCurve copiedCurve = new NURBSCurve();
		int index = ObjectGeometry2D.getCurveCounter()-1;

		float oldData[] = ObjectGeometry2D.getCurveData(ObjectGeometry2D.getNURBSCurve(index));
		float data[] = new float[oldData.length];
		
		
		if(data != null) {				
			int size = data.length;
			for(int i = 0; i < size; i++) {
				data[i] = oldData[i];
				if(i % 3 == 0) {
					data[i] += 2;
				}
			}
		
			VertexListImpl vertexlist = new VertexListImpl();
			vertexlist.setData(data);
			vertexlist.setDimension(3);
			BSplineOfVertices bspline = new BSplineOfVertices(vertexlist, 3, false, false);
			bspline.setRational(true);
			copiedCurve.setCurve(bspline);
			ObjectGeometry2D.addCurve(copiedCurve);
		}
	}
}


class ExportListener implements ActionListener{
	private Workbench workbench;
	
	public ExportListener(Workbench w) {
		workbench = w;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String output = ObjectGeometry2D.createRGGText();
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
	Curve2DPanel panel;
	
	public ResetListener(Curve2DPanel p) {
		panel = p;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String text = getButtonText(panel.group);
		
		if(text == "Curve") {
			ObjectGeometry2D.deleteCurve();	
		}else if(text == "Rectangle") {
			ObjectGeometry2D.deleteRectangle();
		}else if(text == "Triangle") {
			ObjectGeometry2D.deleteTriangle();
		}else if(text == "Circle") {
			ObjectGeometry2D.deleteCircle();
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

class RadioListener implements ActionListener{
	
	@Override
	public void actionPerformed(ActionEvent event) {
		Object object = event.getSource();
		String text = ((JRadioButton) object).getText();
		NURBSDisplay2D.setGeometry(text);
	}
}

public class Curve2DPanel extends PanelSupport{
	GLJPanel glpanel = new GLJPanel();		
	
	ButtonGroup group = new ButtonGroup();
	
	public Curve2DPanel(Workbench workbench) {
		super(new SwingPanel(null));
		Registry r = workbench.getRegistry();
		GraphManager graph = r.getProjectGraph();
		GraphState state = graph.getMainState();
		
		NURBSDisplay2D display = new NURBSDisplay2D(glpanel, state);

		
		JPanel upperPanel = new JPanel();
		JButton b0 = new JButton("add Curve");
		JButton b1 = new JButton("copy Curve");
		JButton b2 = new JButton("delete");
		JButton b3 = new JButton("create rgg-file");
		b0.addActionListener(new CurveListener());
		b1.addActionListener(new CopyListener());
		b2.addActionListener(new ResetListener(this));
		b3.addActionListener(new ExportListener(workbench));
		upperPanel.add(b0);
		upperPanel.add(b1);
		upperPanel.add(b2);
		upperPanel.add(b3);

		
		JPanel objectsPanel = new JPanel();
		JRadioButton curveButton = new JRadioButton("Curve", true);
		curveButton.setActionCommand("Curve");
		JRadioButton rectangleButton = new JRadioButton("Rectangle");
		curveButton.setActionCommand("Rectangle");
		JRadioButton triangleButton = new JRadioButton("Triangle");
		curveButton.setActionCommand("Triangle");
		JRadioButton circleButton = new JRadioButton("Circle");
		curveButton.setActionCommand("Circle");
		curveButton.addActionListener(new RadioListener());
		rectangleButton.addActionListener(new RadioListener());
		triangleButton.addActionListener(new RadioListener());
		circleButton.addActionListener(new RadioListener());
		
		group.add(curveButton);
		group.add(rectangleButton);
		group.add(triangleButton);
		group.add(circleButton);
		objectsPanel.add(curveButton);
		objectsPanel.add(rectangleButton);
		objectsPanel.add(triangleButton);
		objectsPanel.add(circleButton);
		
		JTabbedPane register = new JTabbedPane();
		register.addTab("Object", upperPanel);
		register.addTab("Objects", objectsPanel);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());           

		glpanel.addGLEventListener(display);
		glpanel.addMouseListener(new InputListener(glpanel));
		glpanel.addMouseWheelListener(new WheelListener());
		FPSAnimator animator = new FPSAnimator(glpanel, 60);
		animator.start();
	   
		glpanel.setPreferredSize(new Dimension(400, 400));
		panel.add(register, BorderLayout.NORTH);
		panel.add(glpanel, BorderLayout.CENTER);
		
		((SwingPanel) getComponent()).getContentPane().add(panel);
	}
}