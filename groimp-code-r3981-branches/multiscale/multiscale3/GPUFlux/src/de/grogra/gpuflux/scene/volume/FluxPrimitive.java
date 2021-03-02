package de.grogra.gpuflux.scene.volume;
import java.io.IOException;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.gpuflux.scene.shading.FluxShader;
import de.grogra.imp3d.Renderable;
import de.grogra.vecmath.BoundingBox3d;
import de.grogra.vecmath.geom.TransformableVolume;
import de.grogra.vecmath.geom.Variables;

/**
 * 
 * @author Dietger van Antwerpen
 *
 */

public abstract class FluxPrimitive extends FluxVolume
{
	public final int PRIM_TRANSFORMABLE = 0x80;
	
	public final int PRIM_PLANE = PRIM_TRANSFORMABLE | 1;
	public final int PRIM_SPHERE = PRIM_TRANSFORMABLE | 2;
	public final int PRIM_FRUSTUM = PRIM_TRANSFORMABLE | 3;
	public final int PRIM_BOX = PRIM_TRANSFORMABLE | 4;
	public final int PRIM_TRIANGLE = 5;
	public final int PRIM_PARALLEL = 6;
	
	public abstract void getExtent (BoundingBox3d bb , Variables temp);
	
	public void setOwner(Renderable owner) {
		this.owner = owner;
	}
	
	protected void serialize(ComputeByteBuffer out, int type, Matrix4f world2obj) throws IOException {
		serialize( out, type );
		
		Matrix3f rot = new Matrix3f();
		Vector3f trans = new Vector3f();
		world2obj.getRotationScale(rot);
		world2obj.get(trans);
		
		// serialize world to object transformation matrix
		out.write( world2obj );
	}
	
	public void serialize ( ComputeByteBuffer out , int type , TransformableVolume v ) throws IOException
	{
		serialize( out, type );
		
		Matrix3d rot = new Matrix3d();
		Vector3d trans = new Vector3d();
		
		v.getTransformation( rot , trans );
		Matrix3f rotf = new Matrix3f(rot);
		Vector3f transf = new Vector3f(trans);
		
		// serialize world to object transformation matrix
		out.write( rotf );
		out.write( transf );
	}
	
	public void setFluxShader(FluxShader shader) {
		this.shader = shader;
	}
	
	private void serialize(ComputeByteBuffer out, int type) throws IOException {
		out.writeInt(type);
		out.writeInt(groupIndex);
		if( shader == null )
			out.writeInt(0);
		else
			out.writeInt(shader.getOffset());
		out.writeFloat(	currentIOR );
		super.serialize(out);
	}

	protected Renderable owner;
	
	private FluxShader shader;

	private float currentIOR;

	private int groupIndex = -1;

	public void setIOR(float currentIOR) {
		this.currentIOR = currentIOR;
	}

	public void setGroupIndex(int groupIndex) {
		this.groupIndex = groupIndex;
	} 
	
	public int getGroupIndex(){ return groupIndex; };

}