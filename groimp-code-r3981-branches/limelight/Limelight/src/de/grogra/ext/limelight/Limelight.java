package de.grogra.ext.limelight;


import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;

import de.grogra.ext.limelight.integrators.*;
import de.grogra.ext.limelight.objects.LLCamera;
import de.grogra.imp3d.View3D;
import de.grogra.imp3d.ViewConfig3D;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.ray2.Options;
import de.grogra.reflect.Type;
import de.grogra.util.EnumerationType;
import de.grogra.util.Map;
import de.grogra.vecmath.Math2;

public class Limelight extends de.grogra.imp.Renderer implements Options,
		Runnable {

	public static final EnumerationType RAYPROCESSOR = new EnumerationType(
			"rayprocessor", LimelightPlugin.I18N, new String[] { "standard",
					"pathtracer" }, new Class[] { LimelightRaytracer.class,
					LimelightPathtracer.class }, Type.CLASS);

	private int gridSize;
	private LLCamera camera;
	private Matrix4d cameraTransform;
	private LimelightImage image;
	private Map params;
	private ViewConfig3D view3D;
	private LimelightVisitor visitor;
	private Integrator integrator;
	private MersenneTwister rand;

	public Limelight(Map params) {
		this.params = params;
	}

	public String getName() {
		return "Limelight";
	}

	public void render() throws IOException {
		view3D = (View3D) view;
		rand=new MersenneTwister(1);
	
		camera = new LLCamera(view3D.getCamera(),width,height);
		cameraTransform = new Matrix4d();
		cameraTransform.m33 = 1;
		Math2.invertAffine (view3D.getCamera().getWorldToViewTransformation(),
			cameraTransform);
		
		image = new LimelightImage(width, height);
		visitor = new LimelightVisitor(view3D.getGraph(), view3D.getEpsilon(),
				this, view3D,camera, cameraTransform);
		view3D.getGraph().accept(null, visitor, null);
		System.out.println("Found " + visitor.getVolumeCount() + " Objects and "
				+ visitor.getLightCount() + " lights");

		integrator = new BiDiPathTracingIntegrator(3,visitor,rand);
		gridSize=1;
		
		Thread t = new Thread(this, getName());
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();

	}

	public void run() {
		Sample sample = new Sample(width, height, gridSize);
		Ray ray = new Ray();
		Spectrum3f Ls = new Spectrum3f(0.f, 0.f, 0.f);
		
		while (0.0!=sample.GetNextSample(rand)) {
			
			camera.GenerateRay(sample.toWorld(), ray);
			
			integrator.getRadiance(ray,Ls);

			image.addSample(sample, Ls);
		}
		image.scale(1.f/(float)(gridSize*gridSize));
		image.createBuffer();
		
		for (ImageObserver observer : observers) {
			this.imageUpdate(image, ImageObserver.ALLBITS, 0, 0, image
					.getWidth(), image.getHeight());
		}
	}

	public synchronized void dispose() {
		// TODO Auto-generated method stub

	}

	public Object get(String key, Object defaultValue) {
		return params.get(key, defaultValue);
	}

}
