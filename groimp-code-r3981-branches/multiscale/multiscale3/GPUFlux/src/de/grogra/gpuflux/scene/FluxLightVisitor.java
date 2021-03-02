package de.grogra.gpuflux.scene;

import javax.vecmath.Matrix4d;

import de.grogra.gpuflux.scene.filter.ObjectFilter;
import de.grogra.gpuflux.scene.light.FluxLightBuilder;
import de.grogra.graph.Graph;
import de.grogra.graph.Path;
import de.grogra.imp3d.DisplayVisitor;
import de.grogra.imp3d.ViewConfig3D;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.LightNode;
import de.grogra.imp3d.objects.Sky;
import de.grogra.imp3d.shading.Shader;
import de.grogra.ray2.ProgressMonitor;

public class FluxLightVisitor extends DisplayVisitor {

	private FluxLightBuilder lightBuilder;

	public void visitScene(Graph graph, ViewConfig3D view, ObjectFilter measureFilter, boolean enableSensors, boolean sampleExplicit, ProgressMonitor monitor)
	{
		lightBuilder = new FluxLightBuilder();
		
		//traverse the graph and build flux primitives
		long sceneBuildTime = System.currentTimeMillis ();
		graph.accept (null, this, null);
		sceneBuildTime = System.currentTimeMillis () - sceneBuildTime;
	}
		
	@Override
	protected void visitImpl(Object object, boolean asNode, Shader s, Path path) {
		
		// get the current shape
		@SuppressWarnings("unchecked")
		Object shape = state.getObjectDefault (object, asNode, Attributes.SHAPE, null);
		
		// if light, add light
		if (asNode && (object instanceof LightNode))
		{
			Matrix4d t = getCurrentTransformation ();
			LightNode lightNode = (LightNode)object;
			lightBuilder.buildLight( lightNode.getLight() , t );
		}
		
		if (shape instanceof Sky)
		{
			Matrix4d t = getCurrentTransformation ();
			LightNode sky = new LightNode();
			sky.setLight((Sky)shape);
			sky.setTransform(t);
			lightBuilder.buildLight( sky.getLight(), t );
		}
	}
	
	public FluxLightBuilder getLightBuilder() {
		return lightBuilder;
	}
	

}
