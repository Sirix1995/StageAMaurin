/*
 * Copyright (C) 2012 GroIMP Developer Team
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

package de.grogra.imp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import de.grogra.graph.ChangeBoundaryListener;
import de.grogra.graph.EdgeChangeListener;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.GraphManager;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.swing.PanelSupport;
import de.grogra.pf.ui.swing.SwingPanel;
import de.grogra.pf.ui.swing.WindowSupport;
import de.grogra.util.I18NBundle;
import de.grogra.util.Map;

public class SlotOverviewManager {

	boolean needUpdate = false;

	private void createObjectInspector(Panel panel, final Context ctx, GraphManager graph,
			Map params, final SlotOverview textPanel) {

		Container c = ((SwingPanel) panel.getComponent()).getContentPane();
		((PanelSupport)panel).initialize((WindowSupport) ctx.getWindow(), params);

			// refresh view when graph changes
			graph.addChangeBoundaryListener(new ChangeBoundaryListener() {
				@Override
				public void beginChange(GraphState gs) {}
				@Override
				public void endChange(GraphState gs) {
					if (!needUpdate) return;
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							textPanel.updateUI();
						}
					});
					needUpdate = false;
				}
				@Override
				public int getPriority() {
					return 0;
				}
			});
			
			graph.addEdgeChangeListener(new EdgeChangeListener() {
				@Override
				public void edgeChanged(Object source, Object target, Object edge, GraphState gs) {
					needUpdate = true;
				}
			});


		I18NBundle thisI18NBundle = ctx.getWorkbench().getRegistry().getPluginDescriptor("de.grogra.imp").getI18NBundle();
		// Buttons
		JButton refreshButton = new JButton(thisI18NBundle.getString ("refreshButton.Name"));
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						textPanel.updateUI();
					}
				});
			}
		});
		
		
		JCheckBox idCheckBox = new JCheckBox(thisI18NBundle.getString ("idCheckBox.Name"), false);
		idCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						textPanel.updateUI();
					}
				});
			}
		});
		textPanel.setIdCheckBox(idCheckBox);

		// Button Panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(refreshButton);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(idCheckBox);

		// Layout
		c.setLayout(new BorderLayout());
		c.add(buttonPanel, BorderLayout.NORTH);	
		c.add(textPanel.getPanel(), BorderLayout.CENTER);
		c.add (Box.createHorizontalStrut (4), BorderLayout.EAST);
		c.add (Box.createHorizontalStrut (4), BorderLayout.WEST);
		c.add (Box.createVerticalStrut (5), BorderLayout.SOUTH);
	}


	public static Panel createSlotOverview (Context ctx, Map params) {
		GraphManager graph = ctx.getWorkbench().getRegistry().getProjectGraph();
		UIToolkit ui = UIToolkit.get(ctx);
		Panel p = ui.createPanel(ctx, null, params);
		SlotOverviewManager mgr = new SlotOverviewManager();
		mgr.createObjectInspector(p, ctx, graph, params, new SlotOverview(ctx, graph));
		return p;
	}
	
}
