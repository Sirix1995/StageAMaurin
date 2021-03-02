package de.grogra.imp3d.shading;

import de.grogra.math.ChannelMapVisitor;

public interface ChannelMapNodeVisitor extends ChannelMapVisitor {

	void visit(AffineUVTransformation affineUVTransformation);

	void visit(BumpMap bumpMap);

	void visit(Carpenter carpenter);

	void visit(ChannelBlend channelBlend);

	void visit(Checker checker);

	void visit(Gradient gradient);

	void visit(Granite granite);

	void visit(ImageMap imageMap);

	void visit(Julia julia);

	void visit(Laplace3D laplace3d);

	void visit(Leopard leopard);

	void visit(Mandel mandel);

	void visit(Smooth3D smooth3d);

	void visit(SunSkyLight sunSkyLight);

	void visit(Turbulence turbulence);

	void visit(VolumeChecker volumeChecker);

	void visit(VolumeTurbulence volumeTurbulence);

	void visit(Wood wood);

	void visit(XYZTransformation xyzTransformation);
	
	void visit(Filter filter);
	
	void visit(ChannelMapNode map);
	
	void visit( VolumeFunction volumeFunction );

}
