package de.grogra.animation.timeline;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.ArrayList;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.Icon;
import javax.swing.JSlider;

public class MThumbSlider extends JSlider implements ComponentListener, ContainerListener {

	private static final long serialVersionUID = -1317960246491508595L;
	protected int thumbNum;
	protected ArrayList<BoundedRangeModel> sliderModels;
	protected ArrayList<Icon> thumbRenderers;
	private static final String uiClassID = "MThumbSliderUI";

	public MThumbSlider() {
		thumbNum = 0;
		sliderModels = new ArrayList<BoundedRangeModel>();
		thumbRenderers = new ArrayList<Icon>();
		updateUI();
	}
	
	public void addThumb(int position) {
		thumbNum++;
		sliderModels.add(new DefaultBoundedRangeModel(position, 0, getMinimum(), getMaximum()));
		thumbRenderers.add(null);
		updateUI();
	}

	public void createThumbs(int n, int[] positions) {
		thumbNum = n;
		for (int i = 0; i < n; i++) {
			sliderModels.add(new DefaultBoundedRangeModel(positions[i], 0, getMinimum(), getMaximum()));
			thumbRenderers.add(null);
		}
		updateUI();
	}
	
	public void clearThumbs() {
		thumbNum = 0;
		sliderModels.clear();
		thumbRenderers.clear();
		updateUI();
	}
	
	public void updateUI() {
		AssistantUIManager.setUIName(this);
		super.updateUI();
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public int getThumbNum() {
		return thumbNum;
	}

	public int getValueAt(int index) {
		return getModelAt(index).getValue();
	}

	public void setValueAt(int n, int index) {
		getModelAt(index).setValue(n);
		// should I fire?
	}

	public BoundedRangeModel getModelAt(int index) {
		return sliderModels.get(index);
	}

	public Icon getThumbRendererAt(int index) {
		return thumbRenderers.get(index);
	}

	public void setThumbRendererAt(Icon icon, int index) {
		thumbRenderers.set(index, icon);
	}
	
	public void componentHidden(ComponentEvent e) {
		this.updateUI();
	}
	public void componentMoved(ComponentEvent e) {
		this.updateUI();
	}
	public void componentResized(ComponentEvent e) {
		this.updateUI();
	}
	public void componentShown(ComponentEvent e) {
		this.updateUI();
	}
	public void componentAdded(ContainerEvent e) {
		this.updateUI();
	}
	public void componentRemoved(ContainerEvent e) {
		this.updateUI();
	}
}
