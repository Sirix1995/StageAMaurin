package de.grogra.imp3d.shading;

public interface ShaderVisitor {

	public void visit(ShaderRef shaderRef);

	public void visit(Phong phong);

	public void visit(RGBAShader rgbaShader);

	public void visit(SideSwitchShader sideSwitchShader);

	public void visit(SunSkyLight sunSkyLight);

	public void visit(AlgorithmSwitchShader algorithmSwitchShader);
	
	public void visit(Shader shader);
}