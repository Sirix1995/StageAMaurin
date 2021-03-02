package de.grogra.bwinReader;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.swing.WindowSupport;
import de.grogra.util.Map;

public class GuiLauncher {
	public static Panel createPanel(Context ctx, Map params) {
		SettingGui panel = (SettingGui) new SettingGui(ctx.getWorkbench()).initialize((WindowSupport) ctx.getWindow(), params);
		return panel;
	}
}