package de.grogra.animation.trackview;

import java.awt.Container;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.swing.PanelSupport;
import de.grogra.pf.ui.swing.SwingPanel;
import de.grogra.pf.ui.swing.WindowSupport;
import de.grogra.util.Map;

public class TrackViewWindow extends PanelSupport{

	private TrackViewPanel trackViewPanel;
	
	public TrackViewWindow(Context ctx, Map params) {
		super(new SwingPanel(null));
		Container c = ((SwingPanel) getComponent()).getContentPane();
		this.trackViewPanel = new TrackViewPanel(ctx);
		c.add(trackViewPanel);
		initialize((WindowSupport) ctx.getWindow(), params);
	}
	
}
