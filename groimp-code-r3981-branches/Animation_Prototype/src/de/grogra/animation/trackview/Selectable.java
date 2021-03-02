package de.grogra.animation.trackview;

public interface Selectable {

	public boolean isSelected(CanvasContext ctx, int canvasX, int canvasY);
	
	public void moveTo(CanvasContext ctx, double newX, double newY);
	
}
