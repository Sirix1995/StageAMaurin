package de.grogra.imp3d.shading;

import de.grogra.imp3d.objects.AmbientLight;
import de.grogra.imp3d.objects.DirectionalLight;
import de.grogra.imp3d.objects.Parallelogram;
import de.grogra.imp3d.objects.PointLight;
import de.grogra.imp3d.objects.Sky;
import de.grogra.imp3d.objects.SpotLight;

public interface LightVisitor {

	public void visit(PointLight pointLight) ;
	
	public void visit(SpotLight spotLight) ;

	public void visit(SunSkyLight sunSkyLight);

	public void visit(Sky sky);

	public void visit(Parallelogram parallelogram);

	public void visit(AmbientLight ambientLight);

	public void visit(DirectionalLight directionalLight);
	
	public void visit(Light light);

}