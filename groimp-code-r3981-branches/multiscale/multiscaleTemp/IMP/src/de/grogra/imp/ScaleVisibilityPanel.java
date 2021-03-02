package de.grogra.imp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Scale;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.swing.SwingPanel;
import de.grogra.util.Map;

public class ScaleVisibilityPanel {

	private static ScaleVisibilityPanel mypanel;
	
	final protected Context ctx;
	private View view;
	private ArrayList<Scale> scales;
	private JCheckBox[] vChoices;
	private ScaleVisibilityListener myListener;
	private JPanel panel;
	
	public class ScaleVisibilityListener implements ItemListener
	{
		private ScaleVisibilityPanel parent;
		
		public ScaleVisibilityListener(ScaleVisibilityPanel panel)
		{
			super();
			this.parent = panel;
		}
		
		@Override
		public void itemStateChanged(ItemEvent e) {

            Object source = e.getSource();
            for(int i=0; i<vChoices.length; ++i)
            {
            	if (source == vChoices[i]) {
            		boolean v;
            		if(e.getStateChange() == ItemEvent.DESELECTED)
            			v=false;
            		else
            			v=true;
            		
            		parent.getScale(i).setVisible(v);
            		
            		{
	            		ViewComponent vc = View.getViewComponent(ctx);
						if (vc != null)
						{
							vc.repaint (ViewComponent.ALL);
						}
            		}
            	}
            }
            
		}
	}
	
	public static ScaleVisibilityPanel getInstance(View view, Context ctx)
	{
		if(mypanel == null)
			mypanel = new ScaleVisibilityPanel(view,ctx);
		
		return mypanel;
	}
	
	public static ScaleVisibilityPanel getInstance()
	{
		return mypanel;
	}
	
	public static Panel createScaleVisibilityPanel (Context ctx, Map params)
	{
		View v = View.get(ctx);
		
		mypanel = ScaleVisibilityPanel.getInstance(v,ctx);
		
		GraphManager graph = ctx.getWorkbench().getRegistry().getProjectGraph();
		mypanel.clearScales();
		
		graph.getScales(mypanel.getScales());
		
		if(mypanel.getScaleCount() == 0)
			return null;
		
		UIToolkit ui = UIToolkit.get(ctx);
		Panel p = ui.createPanel(ctx, null, params);
		
		Container c = ((SwingPanel) p.getComponent()).getContentPane();
		
		JPanel scalePanel = new JPanel();
		mypanel.setPanel(scalePanel);
		scalePanel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		
		mypanel.resetVChoices(mypanel.getScaleCount());
		
		for(int i=0; i<mypanel.getVChoiceCount(); ++i)
		{
			Scale s = mypanel.getScale(i);
			mypanel.setVChoice(i, s.getNType().getSimpleName(), s.isVisible());
			
			gc.gridx = 0;
	        gc.gridy = i;
	        gc.anchor = GridBagConstraints.NORTHWEST;
	        gc.insets = new Insets(2, 0, 0, 2);
	        gc.weightx = i;
	        gc.weighty = i;
			scalePanel.add(mypanel.getVChoice(i),gc);
		}
		
		
		c.setLayout(new BorderLayout());
        c.add(scalePanel, BorderLayout.WEST);
		
		return p;
	}

	private ScaleVisibilityPanel(View view, Context ctx)
	{
		this.ctx = ctx;
		this.view = view;
		scales = new ArrayList<Scale>();
		myListener = new ScaleVisibilityListener(this);
	}
	
	public ArrayList<Scale> getScales() {
		return scales;
	}

	public void addScale(Scale s)
	{
		scales.add(s);
	}
	
	public Scale getScale(int index)
	{
		return scales.get(index);
	}
	
	public int getScaleCount()
	{
		return scales.size();
	}

	public void clearScales()
	{
		this.scales.clear();
	}
	
	public void resetVChoices(int size)
	{
		this.vChoices = new JCheckBox[size];
	}
	
	public void setVChoice(int index, String name, boolean checked)
	{
		this.vChoices[index] = new JCheckBox(name, checked);
		this.vChoices[index].addItemListener(myListener);
	}
	
	public JCheckBox getVChoice(int index)
	{
		return this.vChoices[index];
	}
	
	public int getVChoiceCount()
	{
		return this.vChoices.length;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}
	
	public void reset(Graph g)
	{
		if(g instanceof GraphManager)
		{
			GraphManager gm = (GraphManager)g;
			
			this.clearScales();
			gm.getScales(getScales());
			
			this.panel.removeAll();
			
			resetVChoices(getScaleCount());
			
			panel.setLayout(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			
			resetVChoices(getScaleCount());
			
			for(int i=0; i<getVChoiceCount(); ++i)
			{
				Scale s = getScale(i);
				setVChoice(i, s.getNType().getSimpleName(), s.isVisible());
				
				gc.gridx = 0;
		        gc.gridy = i;
		        gc.anchor = GridBagConstraints.NORTHWEST;
		        gc.insets = new Insets(2, 0, 0, 2);
		        gc.weightx = i;
		        gc.weighty = i;
				panel.add(getVChoice(i),gc);
			}
			
			panel.updateUI();
		}
	}

	public JPanel getPanel() {
		return panel;
	}

	public void setPanel(JPanel panel) {
		this.panel = panel;
	}
}
