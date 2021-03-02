package de.grogra.ext.x3d.objects;

import java.util.ArrayList;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.ext.x3d.Util;
import de.grogra.ext.x3d.interfaces.Value;
import de.grogra.imp3d.objects.MeshNode;
import de.grogra.imp3d.objects.PolygonMesh;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

/**
 * GroIMP node class for a x3d extrusion object.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DExtrusion extends MeshNode implements Value {

	protected boolean beginCap = true;
	//enh:field hidden getter setter
	
	protected boolean ccw = true;
	//enh:field hidden getter setter
	
	protected boolean convex = true;
	//enh:field hidden getter setter
	
	protected float creaseAngle = 0;
	//enh:field hidden getter setter
	
	protected float[] crossSection = new float[]{1,1, 1,-1, -1,-1, -1,1, 1,1};
	//enh:field hidden getter setter
	
	protected boolean endCap = true;
	//enh:field hidden getter setter
	
	protected float[] orientation = new float[]{0, 0, 1, 0};
	//enh:field hidden getter setter
	
	protected float[] scale = new float[]{1, 1};
	//enh:field hidden getter setter
	
	protected boolean solid = true;
	//enh:field hidden getter setter
	
	protected float[] spine = new float[]{0,0,0, 0,1,0};
	//enh:field hidden getter setter
	
	protected String def = null;
	protected String use = null;
	
	public String getDef() {
		return def;
	}
	
	public void setDef(String def) {
		this.def = def;
	}

	public String getUse() {
		return use;
	}
	
	public void setUse(String use) {
		this.use = use;
	}
	
	/**
	 * Constructor.
	 */
	public X3DExtrusion() {
		super();
	}

	protected class CrossSection {
		
		protected ArrayList<Point3f> vertices = null;
		
		public CrossSection() {
			vertices = new ArrayList<Point3f>();
		}

		public ArrayList<Point3f> getVertices() {
			return vertices;
		}

		public void setVertices(ArrayList<Point3f> vertices) {
			this.vertices = vertices;
		}
		
		
	}
	
	public void setValues() {
		PolygonMesh p = new PolygonMesh();
		
		//TODO: ccw und creaseAngle umsetzen
		
		IntList coordIndexList = new IntList();
		FloatList vertexList = new FloatList();
		FloatList normalList = new FloatList();
		FloatList uvList = new FloatList();
		
		// calculate some variables
		int crossSectionLength = crossSection.length;
		int scaleLength = scale.length;
		int orientationLength = orientation.length;
		int spineLength = spine.length;
		
		boolean isSpineClosed = false;
		if ((spine[0] == spine[spineLength-3]) &&
				(spine[1] == spine[spineLength-2]) && 
				(spine[2] == spine[spineLength-1]))
			isSpineClosed = true;
//		System.err.println("isSpineClosed: " + isSpineClosed);
		
		boolean isCrossSectionClosed = false;
		if ((crossSection[0] == crossSection[crossSectionLength-2]) &&
				(crossSection[1] == crossSection[crossSectionLength-1]))
			isCrossSectionClosed = true;
//		System.err.println("isCrossSectionClosed: " + isCrossSectionClosed);
		
		// if too less scale values given, use the first two for all
		float[] newScale = null;
		int newScaleLength = 0;
		if (scaleLength*3 < spineLength*2) {
			newScaleLength = spineLength*2/3;
			newScale = new float[newScaleLength];
			for (int i = 0; i < newScaleLength; i=i+2) {
				newScale[i+0] = scale[0];
				newScale[i+1] = scale[1];
			}
		}
		else {
			newScale = scale.clone();
			newScaleLength = scaleLength;
		}

		
		// flip y and z coordinates in orientation
		float[] tmpOrientation = orientation.clone();
		for (int i = 0; i < orientationLength; i=i+4) {
			float temp = tmpOrientation[i+1];
			tmpOrientation[i+1] = tmpOrientation[i+2];
			tmpOrientation[i+2] = temp;
			tmpOrientation[i+0] = tmpOrientation[i+0];
		}
		
		// if too less orientation values given, use the first four for all
		float[] newOrientation = null;
		int newOrientationLength = 0;
		if (orientationLength*3 < spineLength*4) {
			newOrientationLength = spineLength*4/3;
			newOrientation = new float[newOrientationLength];
			for (int i = 0; i < newOrientationLength; i=i+4) {
				newOrientation[i+0] = tmpOrientation[0];
				newOrientation[i+1] = tmpOrientation[1];
				newOrientation[i+2] = tmpOrientation[2];
				newOrientation[i+3] = tmpOrientation[3];
			}
		}
		else {
			newOrientation = orientation.clone();
			newOrientationLength = orientationLength;
		}
			
		// flip y and z coordinates in spine points
		float[] tmpSpine = spine.clone();
		for (int i = 0; i < spineLength; i=i+3) {
			float temp = tmpSpine[i+1];
			tmpSpine[i+1] = -tmpSpine[i+2];
			tmpSpine[i+2] = temp;
		}
		
		// test if entire spline is collinear
		ArrayList<Point3d> spinePointList = new ArrayList<Point3d>();
		for (int sn = 0; sn < spineLength; sn=sn+3) {
			spinePointList.add(new Point3d(tmpSpine[sn], tmpSpine[sn+1], tmpSpine[sn+2]));
		}
		boolean pointsOnLine = Util.pointsOnLine(spinePointList);
//		System.err.println("pointsOnLine: " + pointsOnLine);
		Vector3d previousZAxis = null;
		
		ArrayList<CrossSection> cs = new ArrayList<CrossSection>();
		int spineCount = spineLength / 3;
		// create for every spine a crossSection
		for (int j = 0; j < spineCount; j++) {
			CrossSection newCS = new CrossSection();
			
			ArrayList<Point3f> vertList = new ArrayList<Point3f>();
			for (int i = 0; i < crossSectionLength; i=i+2) {
				// create point in y=0 plane
				Point3f point = new Point3f(crossSection[i], crossSection[i+1], 0);
				
				// scale point
				point.x *= newScale[j*2+0];
				point.y *= newScale[j*2+1];
			
				// rotate point through orienting at spine
				Vector3d yAxis = null;
				Vector3d zAxis = null;

				// test if entire spline is collinear
				if (pointsOnLine) {
					// rotate point through orientation attribute
					AxisAngle4d rot = new AxisAngle4d(newOrientation[j*4 + 0], newOrientation[j*4 + 1],
							newOrientation[j*4 + 2], newOrientation[j*4 + 3]);
					Matrix4d orientMatrix = new Matrix4d();
					orientMatrix.setIdentity();
					orientMatrix.set(rot);
					orientMatrix.transform(point);
					
					// find yAxis
					yAxis = new Vector3d(tmpSpine[spineLength-3] - tmpSpine[0],
							tmpSpine[spineLength-2] - tmpSpine[1],
							tmpSpine[spineLength-1] - tmpSpine[2]);
				
					Matrix4d transMatrix = Util.vectorsToTransMatrix(new Vector3d(0, 0, 1), yAxis);
					transMatrix.transform(point);
					
				}
				else {
					// rotate point through orientation attribute
					AxisAngle4d rot = new AxisAngle4d(newOrientation[j*4 + 0], newOrientation[j*4 + 1],
							newOrientation[j*4 + 2], -newOrientation[j*4 + 3]);
					Matrix4d orientMatrix = new Matrix4d();
					orientMatrix.setIdentity();
					orientMatrix.set(rot);
					orientMatrix.transform(point);
					
					//TODO: abfangen wenn cross ueber 2 linear abhaengige vektoren (noetig bei
					// mittleren und letztem spine point) (unklar, was ich damit meinte. fange aber
					// schon ab, wenn zAxis nicht berechnet werden kann)
					
					if (j == 0) {
						// first spine
						if (isSpineClosed) {
							// calculate y-axis from second spine point to spine point before last
							yAxis = new Vector3d(tmpSpine[3] - tmpSpine[spineLength-6],
									tmpSpine[4] - tmpSpine[spineLength-5],
									tmpSpine[5] - tmpSpine[spineLength-4]);
							zAxis = new Vector3d();
							Vector3d vec1 = new Vector3d(tmpSpine[3] - tmpSpine[0],
									tmpSpine[4] - tmpSpine[1],
									tmpSpine[5] - tmpSpine[2]);
							Vector3d vec2 = new Vector3d(tmpSpine[spineLength-6] - tmpSpine[0],
									tmpSpine[spineLength-5] - tmpSpine[1],
									tmpSpine[spineLength-4] - tmpSpine[2]);
							zAxis.cross(vec1, vec2);
						}
						else {
							// calculate y-axis from second spine point to first spine point
							yAxis = new Vector3d(tmpSpine[3] - tmpSpine[0],
									tmpSpine[4] - tmpSpine[1],
									tmpSpine[5] - tmpSpine[2]);
							zAxis = new Vector3d();
							Vector3d vec2 = new Vector3d(tmpSpine[0] - tmpSpine[3],
									tmpSpine[1] - tmpSpine[4],
									tmpSpine[2] - tmpSpine[5]);
							Vector3d vec1 = new Vector3d(tmpSpine[6] - tmpSpine[3],
									tmpSpine[7] - tmpSpine[4],
									tmpSpine[8] - tmpSpine[5]);
							zAxis.cross(vec1, vec2);
						}
						// if cross = 0 take z-axis from first spine point with z-axis
						int k = 0;
						while (zAxis.equals(new Vector3d(0, 0, 0))) {
							k++;
							
							Vector3d vec1 = new Vector3d(tmpSpine[k*3+3] - tmpSpine[k*3+0],
									tmpSpine[k*3+4] - tmpSpine[k*3+1],
									tmpSpine[k*3+5] - tmpSpine[k*3+2]);
							Vector3d vec2 = new Vector3d(tmpSpine[k*3-3] - tmpSpine[k*3+0],
									tmpSpine[k*3-2] - tmpSpine[k*3+1],
									tmpSpine[k*3-1] - tmpSpine[k*3+2]);
							zAxis.cross(vec1, vec2);
							
						}
						
					}
					else if (j == spineCount - 1) {
						// last spine
						if (isSpineClosed) {
							// calculate y-axis from second spine point to spine point before last
							yAxis = new Vector3d(tmpSpine[3] - tmpSpine[spineLength-6],
									tmpSpine[4] - tmpSpine[spineLength-5],
									tmpSpine[5] - tmpSpine[spineLength-4]);
							zAxis = new Vector3d();
							Vector3d vec1 = new Vector3d(tmpSpine[3] - tmpSpine[0],
									tmpSpine[4] - tmpSpine[1],
									tmpSpine[5] - tmpSpine[2]);
							Vector3d vec2 = new Vector3d(tmpSpine[spineLength-6] - tmpSpine[0],
									tmpSpine[spineLength-5] - tmpSpine[1],
									tmpSpine[spineLength-4] - tmpSpine[2]);
							zAxis.cross(vec1, vec2);
						}
						else {
							// calculate y-axis from second spine point to first spine point
							yAxis = new Vector3d(tmpSpine[spineLength-3] - tmpSpine[spineLength-6],
									tmpSpine[spineLength-2] - tmpSpine[spineLength-5],
									tmpSpine[spineLength-1] - tmpSpine[spineLength-4]);
							zAxis = new Vector3d();
							Vector3d vec1 = new Vector3d(tmpSpine[spineLength-3] - tmpSpine[spineLength-6],
									tmpSpine[spineLength-2] - tmpSpine[spineLength-5],
									tmpSpine[spineLength-1] - tmpSpine[spineLength-4]);
							Vector3d vec2 = new Vector3d(tmpSpine[spineLength-9] - tmpSpine[spineLength-6],
									tmpSpine[spineLength-8] - tmpSpine[spineLength-5],
									tmpSpine[spineLength-7] - tmpSpine[spineLength-4]);
							zAxis.cross(vec1, vec2);
						}
					}
					else {
						// spine in the middle
						yAxis = new Vector3d(tmpSpine[j*3+3] - tmpSpine[j*3-3],
								tmpSpine[j*3+4] - tmpSpine[j*3-2],
								tmpSpine[j*3+5] - tmpSpine[j*3-1]);					
						zAxis = new Vector3d();
						Vector3d vec1 = new Vector3d(tmpSpine[j*3+3] - tmpSpine[j*3+0],
								tmpSpine[j*3+4] - tmpSpine[j*3+1],
								tmpSpine[j*3+5] - tmpSpine[j*3+2]);
						Vector3d vec2 = new Vector3d(tmpSpine[j*3-3] - tmpSpine[j*3+0],
								tmpSpine[j*3-2] - tmpSpine[j*3+1],
								tmpSpine[j*3-1] - tmpSpine[j*3+2]);
						zAxis.cross(vec1, vec2);
					}
				
				
					yAxis.normalize();
					// if zAxis could not be calculated take zAxis from previous spine
					if (zAxis.equals(new Vector3d(0, 0, 0))) {
						zAxis = previousZAxis;
					}
					else
						zAxis.normalize();

					if (previousZAxis == null) {
						previousZAxis = new Vector3d();
					}
					else {
						double dot = zAxis.dot(previousZAxis);
						if (dot < 0)
							zAxis.negate();
					}
					previousZAxis = zAxis;
					
					Vector3d xAxis = new Vector3d();
					xAxis.cross(yAxis, zAxis);
					xAxis.normalize();
					
					Matrix4d transMatrix = new Matrix4d();
					transMatrix.setIdentity();
					transMatrix.setColumn(0, xAxis.x, xAxis.y, xAxis.z, 0);
					transMatrix.setColumn(1, zAxis.x, zAxis.y, zAxis.z, 0);
					transMatrix.setColumn(2, yAxis.x, yAxis.y, yAxis.z, 0);
					
					transMatrix.transform(point);
				
				}
				
				// translate point to spine point
				point.x += tmpSpine[j*3+0];
				point.y += tmpSpine[j*3+1];
				point.z += tmpSpine[j*3+2];
			
				vertList.add(point);
			}			
			newCS.setVertices(vertList);
			cs.add(newCS);
		}
		
		// copy vertices from cs list to vertexList
		int k = 0;
		
		// create begin cap
		if (beginCap) {
			ArrayList<Point3f> vertList = cs.get(0).getVertices();
			int end = vertList.size() - (isCrossSectionClosed ? 2 : 1);
			
			// calculate normal
			Vector3f vec1 = new Vector3f(vertList.get(0).x - vertList.get(1).x,
										 vertList.get(0).y - vertList.get(1).y,
										 vertList.get(0).z - vertList.get(1).z);
			
			Vector3f vec2 = new Vector3f(vertList.get(2).x - vertList.get(1).x,
					 					 vertList.get(2).y - vertList.get(1).y,
					 					 vertList.get(2).z - vertList.get(1).z);
			Vector3f normal = new Vector3f();
			normal.cross(vec1, vec2);
			normal.normalize();
			
			// calculate uv's
			float minx = crossSection[0], miny = crossSection[1],
				maxx = crossSection[0], maxy = crossSection[1];
			for (int i = 1; i < end; i++) {
				float x = crossSection[2*i];
				float y = crossSection[2*i+1];
				minx = Math.min(minx, x);
				miny = Math.min(miny, y);
				maxx = Math.max(maxx, x);
				maxy = Math.max(maxy, y);
			}
			
			// iterate over cross section points
			for (int i = 1; i < end; i++) {
				coordIndexList.add(k++);
				vertexList.add(vertList.get(0).x);
				vertexList.add(vertList.get(0).y);
				vertexList.add(vertList.get(0).z);
				normalList.add(normal.x);
				normalList.add(normal.y);
				normalList.add(normal.z);
				uvList.add((crossSection[0] - minx) / (maxx - minx));
				uvList.add((crossSection[1] - miny) / (maxy - miny));
				
				coordIndexList.add(k++);
				vertexList.add(vertList.get(i+1).x);
				vertexList.add(vertList.get(i+1).y);
				vertexList.add(vertList.get(i+1).z);
				normalList.add(normal.x);
				normalList.add(normal.y);
				normalList.add(normal.z);
				uvList.add((crossSection[2*(i+1)+0] - minx) / (maxx - minx));
				uvList.add((crossSection[2*(i+1)+1] - miny) / (maxy - miny));
				
				coordIndexList.add(k++);
				vertexList.add(vertList.get(i).x);
				vertexList.add(vertList.get(i).y);
				vertexList.add(vertList.get(i).z);
				normalList.add(normal.x);
				normalList.add(normal.y);
				normalList.add(normal.z);
				uvList.add((crossSection[2*(i)+0] - minx) / (maxx - minx));
				uvList.add((crossSection[2*(i)+1] - miny) / (maxy - miny));
			}
		}
		
		// create triangles from one cs to the next
		for (int j = 0; j < spineCount - 1; j++) {
			ArrayList<Point3f> vert1List = cs.get(j+0).getVertices();
			ArrayList<Point3f> vert2List = cs.get(j+1).getVertices();
			
			int end = vert1List.size() - 1;
			
			for (int i = 0; i < end; i++) {
				Vector3f vec1 = new Vector3f(vert1List.get(i+1).x - vert1List.get(i).x,
											 vert1List.get(i+1).y - vert1List.get(i).y,
											 vert1List.get(i+1).z - vert1List.get(i).z);

				Vector3f vec2 = new Vector3f(vert2List.get(i).x - vert1List.get(i).x,
					 					 	 vert2List.get(i).y - vert1List.get(i).y,
					 					 	 vert2List.get(i).z - vert1List.get(i).z);
				
				Vector3f normal = new Vector3f();
				normal.cross(vec1, vec2);
				normal.normalize();
				
				float endf = end;
				float spinef = spineCount - 1;
				
				coordIndexList.add(k++);
				vertexList.add(vert1List.get(i).x);
				vertexList.add(vert1List.get(i).y);
				vertexList.add(vert1List.get(i).z);
				normalList.add(normal.x);
				normalList.add(normal.y);
				normalList.add(normal.z);
				uvList.add((i + 0) / endf);
				uvList.add((j + 0) / spinef);
			
				coordIndexList.add(k++);
				vertexList.add(vert1List.get(i+1).x);
				vertexList.add(vert1List.get(i+1).y);
				vertexList.add(vert1List.get(i+1).z);
				normalList.add(normal.x);
				normalList.add(normal.y);
				normalList.add(normal.z);
				uvList.add((i + 1) / endf);
				uvList.add((j + 0) / spinef);
				
				coordIndexList.add(k++);
				vertexList.add(vert2List.get(i).x);
				vertexList.add(vert2List.get(i).y);
				vertexList.add(vert2List.get(i).z);
				normalList.add(normal.x);
				normalList.add(normal.y);
				normalList.add(normal.z);
				uvList.add((i + 0) / endf);
				uvList.add((j + 1) / spinef);
				
				coordIndexList.add(k++);
				vertexList.add(vert1List.get(i+1).x);
				vertexList.add(vert1List.get(i+1).y);
				vertexList.add(vert1List.get(i+1).z);
				normalList.add(normal.x);
				normalList.add(normal.y);
				normalList.add(normal.z);
				uvList.add((i + 1) / endf);
				uvList.add((j + 0) / spinef);
			
				coordIndexList.add(k++);
				vertexList.add(vert2List.get(i+1).x);
				vertexList.add(vert2List.get(i+1).y);
				vertexList.add(vert2List.get(i+1).z);
				normalList.add(normal.x);
				normalList.add(normal.y);
				normalList.add(normal.z);
				uvList.add((i + 1) / endf);
				uvList.add((j + 1) / spinef);
				
				coordIndexList.add(k++);
				vertexList.add(vert2List.get(i).x);
				vertexList.add(vert2List.get(i).y);
				vertexList.add(vert2List.get(i).z);
				normalList.add(normal.x);
				normalList.add(normal.y);
				normalList.add(normal.z);
				uvList.add((i + 0) / endf);
				uvList.add((j + 1) / spinef);
			}
		}
		
		// create end cap
		if (endCap) {
			ArrayList<Point3f> vertList = cs.get(cs.size()-1).getVertices();
			int end = vertList.size() - (isCrossSectionClosed ? 2 : 1);
			
			// calculate normal
			Vector3f vec1 = new Vector3f(vertList.get(0).x - vertList.get(1).x,
										 vertList.get(0).y - vertList.get(1).y,
										 vertList.get(0).z - vertList.get(1).z);
			
			Vector3f vec2 = new Vector3f(vertList.get(2).x - vertList.get(1).x,
					 					 vertList.get(2).y - vertList.get(1).y,
					 					 vertList.get(2).z - vertList.get(1).z);
			Vector3f normal = new Vector3f();
			normal.cross(vec2, vec1);
			normal.normalize();

			// calculate uv's
			float minx = crossSection[0],
				  miny = crossSection[1],
				  maxx = crossSection[0],
				  maxy = crossSection[1];
			for (int i = 1; i < end; i++) {
				float x = crossSection[2*i];
				float y = crossSection[2*i+1];
				minx = Math.min(minx, x);
				miny = Math.min(miny, y);
				maxx = Math.max(maxx, x);
				maxy = Math.max(maxy, y);
			}
			
			// iterate over cross section points
			for (int i = 1; i <  end; i++) {
				coordIndexList.add(k++);
				vertexList.add(vertList.get(0).x);
				vertexList.add(vertList.get(0).y);
				vertexList.add(vertList.get(0).z);
				normalList.add(normal.x);
				normalList.add(normal.y);
				normalList.add(normal.z);
				uvList.add((crossSection[0] - minx) / (maxx - minx));
				uvList.add((crossSection[1] - miny) / (maxy - miny));
				
				coordIndexList.add(k++);
				vertexList.add(vertList.get(i).x);
				vertexList.add(vertList.get(i).y);
				vertexList.add(vertList.get(i).z);
				normalList.add(normal.x);
				normalList.add(normal.y);
				normalList.add(normal.z);
				uvList.add((crossSection[2*(i)+0] - minx) / (maxx - minx));
				uvList.add((crossSection[2*(i)+1] - miny) / (maxy - miny));
				
				coordIndexList.add(k++);
				vertexList.add(vertList.get(i+1).x);
				vertexList.add(vertList.get(i+1).y);
				vertexList.add(vertList.get(i+1).z);
				normalList.add(normal.x);
				normalList.add(normal.y);
				normalList.add(normal.z);
				uvList.add((crossSection[2*(i+1)+0] - minx) / (maxx - minx));
				uvList.add((crossSection[2*(i+1)+1] - miny) / (maxy - miny));
			}
		}
		
		p.setVertexData(vertexList);
		p.setIndexData(coordIndexList);
		p.setNormalData(normalList.toArray());
		p.setTextureData(uvList.toArray());
		
		this.setVisibleSides(solid?0:2);
		
		this.setPolygons(p);
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field beginCap$FIELD;
	public static final NType.Field ccw$FIELD;
	public static final NType.Field convex$FIELD;
	public static final NType.Field creaseAngle$FIELD;
	public static final NType.Field crossSection$FIELD;
	public static final NType.Field endCap$FIELD;
	public static final NType.Field orientation$FIELD;
	public static final NType.Field scale$FIELD;
	public static final NType.Field solid$FIELD;
	public static final NType.Field spine$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (X3DExtrusion.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 0:
					((X3DExtrusion) o).beginCap = (boolean) value;
					return;
				case 1:
					((X3DExtrusion) o).ccw = (boolean) value;
					return;
				case 2:
					((X3DExtrusion) o).convex = (boolean) value;
					return;
				case 5:
					((X3DExtrusion) o).endCap = (boolean) value;
					return;
				case 8:
					((X3DExtrusion) o).solid = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 0:
					return ((X3DExtrusion) o).isBeginCap ();
				case 1:
					return ((X3DExtrusion) o).isCcw ();
				case 2:
					return ((X3DExtrusion) o).isConvex ();
				case 5:
					return ((X3DExtrusion) o).isEndCap ();
				case 8:
					return ((X3DExtrusion) o).isSolid ();
			}
			return super.getBoolean (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 3:
					((X3DExtrusion) o).creaseAngle = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 3:
					return ((X3DExtrusion) o).getCreaseAngle ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 4:
					((X3DExtrusion) o).crossSection = (float[]) value;
					return;
				case 6:
					((X3DExtrusion) o).orientation = (float[]) value;
					return;
				case 7:
					((X3DExtrusion) o).scale = (float[]) value;
					return;
				case 9:
					((X3DExtrusion) o).spine = (float[]) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 4:
					return ((X3DExtrusion) o).getCrossSection ();
				case 6:
					return ((X3DExtrusion) o).getOrientation ();
				case 7:
					return ((X3DExtrusion) o).getScale ();
				case 9:
					return ((X3DExtrusion) o).getSpine ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new X3DExtrusion ());
		$TYPE.addManagedField (beginCap$FIELD = new _Field ("beginCap", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.BOOLEAN, null, 0));
		$TYPE.addManagedField (ccw$FIELD = new _Field ("ccw", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.BOOLEAN, null, 1));
		$TYPE.addManagedField (convex$FIELD = new _Field ("convex", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.BOOLEAN, null, 2));
		$TYPE.addManagedField (creaseAngle$FIELD = new _Field ("creaseAngle", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.FLOAT, null, 3));
		$TYPE.addManagedField (crossSection$FIELD = new _Field ("crossSection", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (float[].class), null, 4));
		$TYPE.addManagedField (endCap$FIELD = new _Field ("endCap", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.BOOLEAN, null, 5));
		$TYPE.addManagedField (orientation$FIELD = new _Field ("orientation", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (float[].class), null, 6));
		$TYPE.addManagedField (scale$FIELD = new _Field ("scale", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (float[].class), null, 7));
		$TYPE.addManagedField (solid$FIELD = new _Field ("solid", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.BOOLEAN, null, 8));
		$TYPE.addManagedField (spine$FIELD = new _Field ("spine", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (float[].class), null, 9));
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new X3DExtrusion ();
	}

	public boolean isBeginCap ()
	{
		return beginCap;
	}

	public void setBeginCap (boolean value)
	{
		this.beginCap = (boolean) value;
	}

	public boolean isCcw ()
	{
		return ccw;
	}

	public void setCcw (boolean value)
	{
		this.ccw = (boolean) value;
	}

	public boolean isConvex ()
	{
		return convex;
	}

	public void setConvex (boolean value)
	{
		this.convex = (boolean) value;
	}

	public boolean isEndCap ()
	{
		return endCap;
	}

	public void setEndCap (boolean value)
	{
		this.endCap = (boolean) value;
	}

	public boolean isSolid ()
	{
		return solid;
	}

	public void setSolid (boolean value)
	{
		this.solid = (boolean) value;
	}

	public float getCreaseAngle ()
	{
		return creaseAngle;
	}

	public void setCreaseAngle (float value)
	{
		this.creaseAngle = (float) value;
	}

	public float[] getCrossSection ()
	{
		return crossSection;
	}

	public void setCrossSection (float[] value)
	{
		crossSection$FIELD.setObject (this, value);
	}

	public float[] getOrientation ()
	{
		return orientation;
	}

	public void setOrientation (float[] value)
	{
		orientation$FIELD.setObject (this, value);
	}

	public float[] getScale ()
	{
		return scale;
	}

	public void setScale (float[] value)
	{
		scale$FIELD.setObject (this, value);
	}

	public float[] getSpine ()
	{
		return spine;
	}

	public void setSpine (float[] value)
	{
		spine$FIELD.setObject (this, value);
	}

//enh:end
}
