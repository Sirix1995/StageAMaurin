package de.grogra.animation.trackview;

import java.awt.Container;
import de.grogra.animation.AnimCore;
import de.grogra.animation.AnimManager;
import de.grogra.animation.Init;
import de.grogra.animation.handler.Handler;
import de.grogra.animation.timeline.TimeContext;
import de.grogra.graph.impl.GraphManager;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.swing.PanelSupport;
import de.grogra.pf.ui.swing.SwingPanel;
import de.grogra.pf.ui.swing.WindowSupport;
import de.grogra.util.Map;

public class TrackViewManager {

	final private static String ANIMTRACKVIEWPANEL = "AnimTrackViewPanel";
	final private static String ANIMTRACKVIEWPANELPARAMS = "AnimTrackViewPanelParams";

	final private Workbench wb;
	final private TrackView trackView;
	
	
	public TrackViewManager(Workbench wb, GraphManager graph, AnimManager animManager, TimeContext timeContext) {
		this.wb = wb;
		
		trackView = new TrackView(wb, graph, animManager, timeContext);
		
		Panel panel = (Panel) wb.getProperty(ANIMTRACKVIEWPANEL);
		if (panel != null) {
			fillPanel(wb, this, panel, (Map) wb.getProperty(ANIMTRACKVIEWPANELPARAMS));
		}
	}
	
	public static void fillPanel(Workbench wb, TrackViewManager trackViewManager, Panel panel, Map params) {
		TrackView trackView = trackViewManager.getTrackView();
			
		// create view
		Container c = ((SwingPanel) panel.getComponent()).getContentPane();
		((PanelSupport)panel).initialize((WindowSupport) wb.getWindow(), params);

		c.removeAll();
		c.add(trackView);
	}
	
	public static Panel createTrackView(Context ctx, Map params) {
		Workbench wb = Workbench.current();

		UIToolkit ui = UIToolkit.get(wb);
		Panel panel = ui.createPanel(wb, null, params);
		
		wb.setProperty(ANIMTRACKVIEWPANEL, panel);
		wb.setProperty(ANIMTRACKVIEWPANELPARAMS, params);
		
		AnimCore animCore = (AnimCore) wb.getProperty(Init.ANIMCORE);
		fillPanel(wb, animCore.getAnimManager().getTrackViewManager(), panel, params);
		
		return panel;
	}

	public TrackView getTrackView() {
		return trackView;
	}
	
}
