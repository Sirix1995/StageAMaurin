package de.grogra.gpuflux.scene.light;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3f;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.gpuflux.scene.shading.FluxShader;
import de.grogra.gpuflux.scene.shading.FluxShaderBuilder;
import de.grogra.gpuflux.scene.shading.channel.FluxChannelMap;
import de.grogra.graph.ArrayPath;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.Instantiator;
import de.grogra.graph.Path;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.ViewConfig3D;
import de.grogra.imp3d.Visitor3D;
import de.grogra.imp3d.objects.AmbientLight;
import de.grogra.imp3d.objects.DirectionalLight;
import de.grogra.imp3d.objects.LightNode;
import de.grogra.imp3d.objects.Parallelogram;
import de.grogra.imp3d.objects.PhysicalLight;
import de.grogra.imp3d.objects.PointLight;
import de.grogra.imp3d.objects.Sky;
import de.grogra.imp3d.objects.SpectralLight;
import de.grogra.imp3d.objects.SpotLight;
import de.grogra.imp3d.shading.Light;
import de.grogra.imp3d.shading.LightVisitor;
import de.grogra.imp3d.shading.Shader;
import de.grogra.imp3d.shading.SunSkyLight;
import de.grogra.ray.physics.Environment;

public class FluxLightBuilder implements LightVisitor {

	private final Vector<FluxLight> lights = new Vector<FluxLight>();
	private Matrix4d currentTransformation;
	private int lightSampleCount = 0;
	private FluxShaderBuilder shaderBuilder;
	private FluxLight sky = null;
	private FluxLight currentLight;
	private Graph graph;
	private String log = "";

	public FluxLightBuilder( Graph graph, AbstractList<LightNode> lights )
	{
		buildLights(graph,lights);
	}
	
	public FluxLightBuilder() {
		this.shaderBuilder = new FluxShaderBuilder()
		{
			@Override
			protected void warning( String warning )
			{
				FluxLightBuilder.this.warning( warning );
			};
		};
	}

	private void log(String log) {
		this.log  += "Log, " + log + "\n";
	}
	
	class TransformVisitor3D extends Visitor3D
	{
		protected void init(GraphState gs)
		{
			Matrix4d m = new Matrix4d ();
			m.setIdentity ();
			init (gs, gs.getGraph ().getTreePattern (), m);
		}
		
		@Override
		protected void visitEnterImpl(Object object, boolean asNode,
				Path path) {
		}

		@Override
		protected void visitLeaveImpl(Object object, boolean asNode,
				Path path) {
		}
		
		public Matrix4d getGlobalTransformation(Node node )
		{
			Matrix4d mtrx = new Matrix4d();
			mtrx.setIdentity();
			
			if( node == null )
				return mtrx;
			
			Stack<Node> stack = new Stack<Node>();
			while( node != null )
			{
				stack.push(node);
				node = node.getSource();
			};
			
			Node root = stack.pop();
					
			ArrayPath placeInPath = new ArrayPath(graph);
			
			return getGlobalTransformation(root,stack,placeInPath);
		}
		
		private Matrix4d getGlobalTransformation(Node v, Stack<Node> stack, ArrayPath path) {
			Matrix4d mtrx;
			
			path.pushNode (v, v.getId() >= 0 ? v.getId() : v.hashCode ());
			visitEnter(path, true);
			
			Instantiator i;
			if ((i = v.getInstantiator ()) != null)
			{
				boolean b = true;
				state.beginInstancing (v,
						path.getObjectId (-1));
				b = i.instantiate (path, this);
			}
			
			if( !stack.empty() )
			{
				Node child = stack.pop();
				
				path.pushEdgeSet (v.getEdgeTo(child), -1, false);
						
				mtrx = getGlobalTransformation( child, stack, path);

				path.popEdgeSet ();
			}
			else
			{
				mtrx = (Matrix4d) getCurrentTransformation().clone();
			}
			
			if ((i = v.getInstantiator ()) != null)
			{
				state.endInstancing ();
			}
			
			visitLeave(v, path, true);
			path.popNode ();
			
			return mtrx;
		}
	};
	
	private void warning(String warning) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(PointLight pointLight) {
		
		if( pointLight instanceof PhysicalLight )
		{
			addLight( new FluxPhysicalLight((PhysicalLight)pointLight) , pointLight );
		}
		else
		{
			addLight( new FluxPointLight(pointLight) , pointLight );
		}
	}

	@Override
	public void visit(SunSkyLight sunSkyLight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Sky sky) {
		
		Shader s = sky.getShader();
		FluxShader fluxShader = shaderBuilder.buildShader( s );
		FluxSkyLight fluxSky = new FluxSkyLight(sky, fluxShader);
		addLight( fluxSky , sky );
		this.sky = fluxSky;
	}

	@Override
	public void visit(Parallelogram parallelogram) {
		if( parallelogram.getLight() != null )
			addLight( new FluxParallelLight(parallelogram) , parallelogram );
	}

	@Override
	public void visit(AmbientLight ambientLight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(DirectionalLight directionalLight) {
		addLight( new FluxDirectionalLight(directionalLight) , directionalLight );
	}
	
	private void addLight(FluxLight fluxLight, Light light) {
		fluxLight.setTransformation( currentTransformation );
		currentLight = fluxLight; 
	}
	
	public int getSampleCount(){ return lightSampleCount; };
	
    
    /**
     * Verifies whether this matrix is Orthogonal .
	 * @return true if this matrix is Orthogonal
     */
    public static final boolean isOrthogonalMatrix(Matrix3f m) {
    	Vector3f v1 = new Vector3f();
		Vector3f v2 = new Vector3f();
		Vector3f v3 = new Vector3f();
		m.getColumn(0, v1 );
		m.getColumn(1, v2);
		m.getColumn(2, v3);
		
		return !(
			Math.abs( v1.dot(v1) - 1.0 ) > 0.001 || 
			Math.abs( v2.dot(v2) - 1.0 ) > 0.001 ||
			Math.abs( v3.dot(v3) - 1.0 ) > 0.001 ||
			Math.abs( v1.dot(v2) ) > 0.001 ||
			Math.abs( v2.dot(v3) ) > 0.001 ||
			Math.abs( v1.dot(v3) ) > 0.001 );
    }

	public void buildLight(Light s, Matrix4d transformation) {
		Matrix3f m1 = new Matrix3f();
		transformation.getRotationScale( m1 );
		
		if( !isOrthogonalMatrix(m1) )
		{
			throw new UnsupportedOperationException("Light source transformation must be orthonormal.");
		}
		

		currentTransformation = transformation;
		
		FluxLight fluxLight = constructLight( s );
		if( fluxLight != null )
		{
			lights .add(fluxLight);
			lightSampleCount  += fluxLight.getSampleCount();
		}
	}
	
	public FluxLight constructLight(Light s) {
		currentLight = null;
		if( s != null )
		{
			s.accept(this);
		}
		return currentLight;
	}
	
	public float[] getCummulativePowerBuffer(Environment env)
	{
		// collect the power of the light sources
		final float[] cumPowerOfLight = new float[lights.size()];
		float totalPower = 0;
		for (int i = 0; i < lights.size(); i++)
		{
			totalPower += (float) lights.get(i).getLight().getTotalPower (env);
			cumPowerOfLight[i] = totalPower;
		}
		
		return cumPowerOfLight;
	}
	
	public void serializeCummulativePowerBuffer(ComputeByteBuffer out, Environment env) throws IOException {
		float [] cumPowers = getCummulativePowerBuffer(env);
		
		for( int i = 0 ; i < cumPowers.length ; i++ )
		{
			out.writeFloat(cumPowers[i]);
		}
	}
	
	public void serialize(ComputeByteBuffer light_out,
			ComputeByteBuffer offset_out) throws IOException {
		// serialize shaders
		shaderBuilder.serialize(light_out, light_out);
		
		// iterate over all light
		Iterator<FluxLight> itr = lights.iterator();
		while(itr.hasNext())
		{
			// get next light
			FluxLight light = itr.next();
			
			// serialize light
			light.serialize( light_out );
			
			// serialize offset in the light buffer
			offset_out.writeInt( light.getOffset() );
		};
	}
	
	public FluxLight getSky()
	{
		return sky;
	}
	
	public int getLightCount() {
		return lights.size();
	}

	@Override
	public void visit(SpotLight spotLight) {
		addLight( new FluxSpotLight(spotLight) , spotLight );
	}

	@Override
	public void visit(Light light) {
		if( light instanceof SpectralLight )
		{
			SpectralLight spectralLight = (SpectralLight)light;
			
			if(spectralLight.getLight() instanceof PointLight) {
				PointLight p = (PointLight)spectralLight.getLight();
				if(p.getPower()!=100) {
					spectralLight.setPower(p.getPower());
				}
			}
			FluxLight input = constructLight( spectralLight.getLight() );
			//spectralLight.getLight()
			addLight( new FluxSpectralLight(spectralLight,input) , spectralLight );
		}
	}

	public void buildLights(Graph graph, AbstractList<LightNode> lights) {
		this.graph = graph;
		// gather lights
		
		// initialize display visitor
		Matrix4d m = new Matrix4d ();
		m.setIdentity ();
		
		// gather lights
		TransformVisitor3D visitor = new TransformVisitor3D();
		visitor.init(GraphState.current (graph));
		for( LightNode lightNode : lights )
		{
			Light light = lightNode.getLight();
			Matrix4d mtrx = visitor.getGlobalTransformation(lightNode);
			buildLight( light , mtrx  );
		}
		
		log( "Light count: " + getLightCount() );
		log( "Light sample count: " + getSampleCount() );
	}

	public Vector<FluxLight> getLights() {
		return lights;
	}

	public Vector<FluxShader> getLightShaders() {
		return shaderBuilder.getShaders();
	}

	public Vector<FluxChannelMap> getLightChannels() {
		return shaderBuilder.getChannelBuilder().getChannels();
	}

	public void finnish(boolean sampleExplicit, ViewConfig3D view) {
		// initialize display visitor
		Matrix4d m = new Matrix4d ();
		m.setIdentity ();
		
		// set default light
		if( ((sampleExplicit && (getSampleCount() == 0)) || (!sampleExplicit && (getLightCount() == 0)))  && (view != null) )
		{
			buildLight( view.getDefaultLight (m) , m  );
		}
	}
}
