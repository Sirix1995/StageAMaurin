package de.grogra.math;

public interface ChannelMapVisitor {

	void visit(Graytone graytone);

	void visit(RGBColor rgbColor);

	void visit(ChannelMap channelMap);

}
