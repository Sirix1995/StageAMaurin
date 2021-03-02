package de.grogra.imp3d.gl20;

import de.grogra.imp3d.gl20.GL20Const;
import de.grogra.imp3d.gl20.GL20Resource;
import de.grogra.imp3d.PolygonArray;

public class GL20ResourceMesh extends GL20Resource {
	/**
	 * polygonArray attribute bit
	 */
	final private static int POLYGON_ARRAY = 0x1;

	/**
	 * firstPolygonIndex attribute bit
	 */
	final private static int FIRST_POLYGON_INDEX = 0x2;

	/**
	 * lastPolygonIndex attribute bit
	 */
	final private static int LAST_POLYGON_INDEX = 0x4;

	/**
	 * all changes that was made since last update
	 */
	private int changeMask = GL20Const.ALL_CHANGED;

	/**
	 * polygonArray attribute
	 */
	private PolygonArray polygonArray = null;

	/**
	 * firstPolygonIndex attribute
	 */
	private int firstPolygonIndex = GL20Const.INVALID_ID;

	/**
	 * lastPolygonIndex attribute
	 */
	private int lastPolygonIndex = GL20Const.INVALID_ID;

	/**
	 * start element index in element array buffer that should used
	 */
	private int startElementIndex;

	/**
	 * number of elements after <code>startElementIndex</code> in element array buffer
	 * that should used
	 */
	private int elementUsedCount;

	/**
	 * elements per primitive. can be 3 for triangles or 4 for quadrilaterals
	 */
	private int elementsPerPrimitive;

	/**
	 * primitive-polygon ratio.
	 * e.g. polygons from <code>polygonArray</code> are quadrilaterals and non-planer
	 * then primitives will get planar triangles. so the ratio from primitive to polygon
	 * is 2:1 = 2
	 */
	private int primitivePolygonRatio;

	/**
	 * drawing flags
	 */
	private int drawingFlags = 0;

	/**
	 * element array buffer index, is assign from <code>GL20GfxServer</code>
	 */
	private int elementArrayBufferIndex = GL20Const.INVALID_ID;

	/**
	 * array buffer index, is assign from <code>GL20GfxServer</code>
	 */
	private int arrayBufferIndex = GL20Const.INVALID_ID;

	/**
	 * context ID where the resource was marked as valid
	 */
	private int lastContextID = GL20Const.INVALID_ID;

	protected GL20ResourceMesh(int resourceClassType) {
		super(resourceClassType);

		assert (resourceClassType & GL20Resource.GL20RESOURCE_CLASS_MASK) == GL20Resource.GL20RESOURCE_CLASS_MESH;
	}

	/**
	 * set the <code>PolygonArray</code> that contain the mesh information
	 *
	 * @param polygonArray the <code>PolygonArray</code>
	 */
	final public void setPolygonArray(PolygonArray polygonArray) {
		if (this.polygonArray != null) {
			if (polygonArray == null) {
				this.polygonArray = null;
				changeMask |= POLYGON_ARRAY;
			}
			else if (polygonArray.userObject != this)
				this.polygonArray = null;
		}

		if ((this.polygonArray == null) && (polygonArray != null)) {
			this.polygonArray = polygonArray;
			this.polygonArray.userObject = this;

			changeMask |= POLYGON_ARRAY;
			setUsedPolygonArea(0,0);
		}
	}

	/**
	 * set the polygon index area that should used from the given <code>PolygonArray</code>
	 *
	 * @param firstPolygonIndex the index of the first polygon that should used
	 * @param polygonCount the number of polygons that should used start with <code>firstPolygonIndex</code>
	 * OR <code>0</code> when the rest of the <code>PolygonArray</code> should used
	 * OR a negative number <em>n</em>, so that all polygons starting with <code>firstPolygonIndex</code>
	 * and ending by <code>polygonArray.getPolygonCount()</code> - <em>n</em> will used
	 * @return <code>true</code> - polygon index area was successful setted.
	 * <code>false</code> - at least one parameter is invalid.
	 */
	final public boolean setUsedPolygonArea(int firstPolygonIndex,int polygonCount) {
		boolean returnValue = false;
		int polygonsInArray = 0;

		if ((polygonArray != null) && (firstPolygonIndex >= 0) &&
			(firstPolygonIndex < (polygonsInArray = polygonArray.getPolygonCount()))) {
			int endPolygonIndex = 0;

			if (polygonCount > 0) {
				if (firstPolygonIndex + polygonCount <= polygonsInArray)
					endPolygonIndex = firstPolygonIndex + polygonCount;
			}
			else if (polygonCount == 0) {
				endPolygonIndex = polygonsInArray;
			}
			else {
				if (polygonsInArray + polygonCount > firstPolygonIndex)
					endPolygonIndex = polygonsInArray + polygonCount;
			}

			if (endPolygonIndex > 0) {
				this.firstPolygonIndex  = firstPolygonIndex;
				this.lastPolygonIndex = endPolygonIndex;

				changeMask |= FIRST_POLYGON_INDEX | LAST_POLYGON_INDEX;

				returnValue = true;
			}
		}

		return returnValue;
	}

	/**
	 * draw the mesh
	 */
	public void draw() {
		if (drawingFlags != 0) {
			GL20GfxServer gfxServer = GL20GfxServer.getInstance();
			gfxServer.drawElementArrayBuffer(elementArrayBufferIndex,arrayBufferIndex,
											 startElementIndex,elementUsedCount,drawingFlags);
		}
	}

	/**
	 * check if this <code>GL20ResourceMesh</code> is up to date
	 *
	 * @return <code>true</code> - this <code>GL20ResourceMesh</code> is up to date
	 * @see <code>GL20Resource</code>
	 */
	public boolean isUpToDate() {
		if ((changeMask != 0) || (GL20GfxServer.getInstance().getContextID() != lastContextID))
			return false;
		else
			return super.isUpToDate();
	}

	/**
	 * update the state of this <code>GL20ResourceMesh</code>
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void update() {
		GL20GfxServer gfxServer = GL20GfxServer.getInstance();
		int currentContextID = gfxServer.getContextID();

		if ((changeMask != 0) || (currentContextID != lastContextID)) {
			// something has changed
			if ((currentContextID == lastContextID) && ((changeMask & POLYGON_ARRAY) != 0)) {
				// context stay the same, delete old buffers
				gfxServer.deleteElementArrayBuffer(elementArrayBufferIndex);
				elementArrayBufferIndex = GL20Const.INVALID_ID;
				gfxServer.deleteArrayBuffer(arrayBufferIndex);
				arrayBufferIndex = GL20Const.INVALID_ID;
			}

			if (((changeMask & POLYGON_ARRAY) != 0) || (currentContextID != lastContextID)) {
				// buffers are invalid
				drawingFlags = 0;
				arrayBufferIndex = GL20Const.INVALID_ID;
				elementArrayBufferIndex = GL20Const.INVALID_ID;

				if (polygonArray != null) {
					elementArrayBufferIndex = gfxServer.createElementArrayBuffer();
					arrayBufferIndex = gfxServer.createArrayBuffer();

					boolean haveToTriangleize = true;
					if (polygonArray.edgeCount == 3)
						haveToTriangleize = false;
					else {
						if (polygonArray.planar == true)
							haveToTriangleize = false;
					}

					int vertexSize = polygonArray.dimension * (Float.SIZE / 8) +
									 2 * (Float.SIZE / 8) + 	// U and V floats
									 3 * (Float.SIZE / 8);	// x,y and z floats for normal

					final int vertexCount = polygonArray.getVertexCount();

					gfxServer.openArrayBuffer(arrayBufferIndex,vertexCount * vertexSize);
					float[] tempNormal = new float[3];
					for (int vertexIndex=0;vertexIndex < vertexCount;vertexIndex++) {
						if (polygonArray.dimension == 3)
							gfxServer.writeArrayBuffer3f(polygonArray.vertices.get(vertexIndex * 3),polygonArray.vertices.get(vertexIndex * 3 + 1),polygonArray.vertices.get(vertexIndex * 3 + 2));
						else
							gfxServer.writeArrayBuffer4f(polygonArray.vertices.get(vertexIndex * 4),polygonArray.vertices.get(vertexIndex * 4 + 1),polygonArray.vertices.get(vertexIndex * 4 + 2),polygonArray.vertices.get(vertexIndex * 4 + 3));

						polygonArray.getNormal(tempNormal,vertexIndex);
						//gfxServer.writeArrayBufferfv(tempNormal,3);
						gfxServer.writeArrayBuffer3f(-tempNormal[0], -tempNormal[1], -tempNormal[2]);

						gfxServer.writeArrayBuffer2f(polygonArray.uv.get(vertexIndex * 2),polygonArray.uv.get(vertexIndex * 2 + 1));
					}
					gfxServer.closeArrayBuffer(arrayBufferIndex);

					if (polygonArray.dimension == 3)
						drawingFlags |= GL20GfxServer.HAS_VERTEX_3_FLOAT;
					else
						drawingFlags |= GL20GfxServer.HAS_VERTEX_4_FLOAT;

					drawingFlags |= GL20GfxServer.ORDER_V_N_UV;
					drawingFlags |= GL20GfxServer.HAS_NORMALS_FLOAT;
					drawingFlags |= GL20GfxServer.HAS_TEXTURE_UV_FLOAT;

					final int polygonCount = polygonArray.getPolygonCount();
					int indexCount = polygonCount;
					int sortMethod;
					if (polygonArray.edgeCount == 3) {
						indexCount *= 3;
						sortMethod = 0;
						elementsPerPrimitive = 3;
						primitivePolygonRatio = 1;
						drawingFlags |= GL20GfxServer.TRIANGLES;
					}
					else {
						if (haveToTriangleize == true) {
							indexCount *= 6;
							sortMethod = 1;
							elementsPerPrimitive = 3;
							primitivePolygonRatio = 2;
							drawingFlags |= GL20GfxServer.TRIANGLES;
						}
						else {
							indexCount *= 4;
							sortMethod = 2;
							elementsPerPrimitive = 4;
							primitivePolygonRatio = 1;
							drawingFlags |= GL20GfxServer.QUADS;
						}
					}

					gfxServer.openElementArrayBuffer(elementArrayBufferIndex,indexCount * (Integer.SIZE / 8));
					int[] tempIndices = new int[4];
					int[] tempNormals = new int[4];
					for (int polygonIndex=0;polygonIndex < polygonCount;polygonIndex++) {
						polygonArray.getPolygon(polygonIndex,tempIndices, tempNormals);
						switch (sortMethod) {
						case 0:
							gfxServer.writeElementArrayBufferiv(tempIndices,3);
							break;
						case 1:
							gfxServer.writeElementArrayBufferiv(tempIndices,3);
							gfxServer.writeElementArrayBuffer3i(tempIndices[0],tempIndices[2],tempIndices[3]);
							break;
						case 2:
							gfxServer.writeElementArrayBufferiv(tempIndices,4);
							break;
						}
					}
					gfxServer.closeElementArrayBuffer(elementArrayBufferIndex);

					drawingFlags |= GL20GfxServer.ELEMENTS_INT;
				}

				lastContextID = currentContextID;
			}

			if (((changeMask & FIRST_POLYGON_INDEX) != 0) || ((changeMask & LAST_POLYGON_INDEX) != 0)) {
				if (polygonArray != null) {
					startElementIndex = firstPolygonIndex * primitivePolygonRatio * elementsPerPrimitive;
					elementUsedCount = (lastPolygonIndex - firstPolygonIndex) * primitivePolygonRatio * elementsPerPrimitive;
				}
			}

			changeMask = 0;
		}

		super.update();
	}

	/**
	 * destroy this <code>GL20ResourceMesh</code>
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void destroy() {
		GL20GfxServer gfxServer = GL20GfxServer.getInstance();
		gfxServer.deleteArrayBuffer(arrayBufferIndex);
		gfxServer.deleteElementArrayBuffer(elementArrayBufferIndex);
		super.destroy();
	}
}