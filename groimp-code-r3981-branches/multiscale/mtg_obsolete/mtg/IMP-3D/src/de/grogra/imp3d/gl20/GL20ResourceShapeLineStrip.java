package de.grogra.imp3d.gl20;

import javax.vecmath.Vector4f;

import de.grogra.imp3d.gl20.GL20Const;
import de.grogra.imp3d.gl20.GL20Resource;
import de.grogra.imp3d.gl20.GL20ResourceShape;
import de.grogra.imp3d.LineArray;

public class GL20ResourceShapeLineStrip extends GL20ResourceShape {
	/**
	 * lineArray attribute bit 
	 */
	final private static int LINE_ARRAY = 0x1;
	
	/**
	 * color attribute bit
	 */
	final private static int COLOR = 0x2;
	
	/**
	 * all changes that was made since last update
	 */
	private int changeMask = GL20Const.ALL_CHANGED;
	
	/**
	 * lineArray attribute
	 */
	private LineArray lineArray = null;
	
	/**
	 * color attribute
	 */
	private Vector4f color = new Vector4f(1.0f,1.0f,1.0f,1.0f);
	
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
		
	public GL20ResourceShapeLineStrip() {
		super(GL20Resource.GL20RESOURCE_SHAPE_LINE_STRIP);
	}
	
	/**
	 * set the <code>LineArray</code> that contains the line strip information
	 * 
	 * @param lineArray the <code>LineArray</code>
	 */
	final public void setLineArray(LineArray lineArray) {
		if (this.lineArray != null) {
			if (lineArray == null) {
				this.lineArray = null;
				changeMask |= LINE_ARRAY;
			}
			else
				/**
				 * FIXME update will not force a new <code>LineArray</code>
				 * just the <code>LineArray.vertices</code> and <code>LineArray.lines</codes>
				 * where updated. we have have no secure way to detect a real update
				 */
				// if (lineArray.userObject != this)
				this.lineArray = null;
		}
		
		if ((this.lineArray == null) && (lineArray != null)) {
			this.lineArray = lineArray;
			this.lineArray.userObject = this;
			changeMask |= LINE_ARRAY;
		}
	}
	
	/**
	 * set the color of this line strip
	 * 
	 * @param color the color of this line strip
	 */
	final public void setColor(Vector4f color) {
		if (this.color.equals(color) == false) {
			this.color.set(color);
			changeMask |= COLOR;
		}
	}

	/**
	 * tell this <code>GL20ResourceShape</code> that it should apply the
	 * geometry to the <code>GL20GfxServer</code>
	 */
	public void applyGeometry() {
		super.applyGeometry();
		draw();
	}
	
	/**
	 * draw the line strip
	 */
	private void draw() {
		if (drawingFlags != 0) {
			GL20GfxServer gfxServer = GL20GfxServer.getInstance();
			gfxServer.setCurrentColor(color);
			gfxServer.drawElementArrayBuffer(elementArrayBufferIndex,arrayBufferIndex,
											 startElementIndex,elementUsedCount,drawingFlags);
		}
	}	
	
	/**
	 * check if this <code>GL20ResourceShapeLineStrip</code> is up to date
	 *
	 * @return <code>true</code> - this <code>GL20ResourceShapeLineStrip</code> is up to date
	 * @see <code>GL20Resource</code>
	 */
	public boolean isUpToDate() {
		if ((changeMask != 0) || (GL20GfxServer.getInstance().getContextID() != lastContextID))
			return false;
		else
			return super.isUpToDate();
	}
	
	/**
	 * update the state of this <code>GL20ResourceShapeLineStrip</code>
	 * 
	 * @see <code>GL20Resource</code>
	 */
	public void update() {
		GL20GfxServer gfxServer = GL20GfxServer.getInstance();
		int currentContextID = gfxServer.getContextID();
		
		if ((changeMask != 0) || (currentContextID != lastContextID)) {
			// something has changed
			if ((currentContextID == lastContextID) && ((changeMask & LINE_ARRAY) != 0)) {
				// context stay the same, delete old buffers
				gfxServer.deleteElementArrayBuffer(elementArrayBufferIndex);
				elementArrayBufferIndex = GL20Const.INVALID_ID;
				gfxServer.deleteArrayBuffer(arrayBufferIndex);
				arrayBufferIndex = GL20Const.INVALID_ID;
			}
			
			if ((currentContextID != lastContextID) || ((changeMask & LINE_ARRAY) != 0)) {
				// buffers are invalid
				drawingFlags = 0;
				arrayBufferIndex = GL20Const.INVALID_ID;
				elementArrayBufferIndex = GL20Const.INVALID_ID;
				
				if (lineArray != null) {
					elementArrayBufferIndex = gfxServer.createElementArrayBuffer();
					arrayBufferIndex = gfxServer.createArrayBuffer();
					
					drawingFlags |= GL20GfxServer.LINES;
					
					final int vertexCount = lineArray.vertices.size();
					final int vertexSize = lineArray.dimension * (Float.SIZE / 8);
					
					gfxServer.openArrayBuffer(arrayBufferIndex, vertexCount * vertexSize);
					for (int vertexIndex=0;vertexIndex < vertexCount;vertexIndex++) {
						if (lineArray.dimension == 3)
							gfxServer.writeArrayBuffer3f(lineArray.vertices.get(vertexIndex * 3), lineArray.vertices.get(vertexIndex * 3 + 1), lineArray.vertices.get(vertexIndex * 3 + 2));
						else
							gfxServer.writeArrayBuffer4f(lineArray.vertices.get(vertexIndex * 4), lineArray.vertices.get(vertexIndex * 4 + 1), lineArray.vertices.get(vertexIndex * 4 + 2), lineArray.vertices.get(vertexIndex * 4 + 3));
					}
					gfxServer.closeArrayBuffer(arrayBufferIndex);
					
					if (lineArray.dimension == 3)
						drawingFlags |= GL20GfxServer.HAS_VERTEX_3_FLOAT;
					else
						drawingFlags |= GL20GfxServer.HAS_VERTEX_4_FLOAT;
					drawingFlags |= GL20GfxServer.HAS_NO_NORMALS;
					drawingFlags |= GL20GfxServer.HAS_NO_TEXTURE_UV;
					
					drawingFlags |= GL20GfxServer.ORDER_V_N_UV;
					
					int lineSegmentCount = 0;
					boolean firstElement = true;
					for (int i=0;i < lineArray.lines.size();i++) {
						int index = lineArray.lines.get(i);
						if (index < 0)
							firstElement = true;
						else {
							if (firstElement == true)
								firstElement = false;
							else
								lineSegmentCount++;
						}
					}
					
					gfxServer.openElementArrayBuffer(elementArrayBufferIndex, lineSegmentCount * 2 * (Integer.SIZE / 8));
					int startIndex = -1;
					int endIndex = -1;
					startElementIndex = 0;
					elementUsedCount = 0;
					for (int i=0;i < lineArray.lines.size();i++) {
						int index = lineArray.lines.get(i);
						
						if (index < 0) {
							startIndex = endIndex = -1;
						}
						else {
							endIndex = index;
							
							if ((startIndex >= 0) && (endIndex >= 0)) {
								elementUsedCount += gfxServer.writeElementArrayBuffer2i(startIndex, endIndex);
								startIndex = endIndex;
								endIndex = -1;
							}
							else {
								startIndex = endIndex;
								endIndex = -1;
							}
						}
					}
					gfxServer.closeElementArrayBuffer(elementArrayBufferIndex);
					drawingFlags |= GL20GfxServer.ELEMENTS_INT;
				}
				
				lastContextID = currentContextID;
			}
			
			changeMask = 0;
		}
		
		// apply changes to <code>GL20ResourceShape</code>
		super.update();
	}
	
	/**
	 * destroy this <code>GL20ResourceShapeLineStrip</code>
	 * 
	 * @see <code>GL20Resource</code>
	 */
	public void destory() {
		GL20GfxServer gfxServer = GL20GfxServer.getInstance();
		gfxServer.deleteArrayBuffer(arrayBufferIndex);
		gfxServer.deleteElementArrayBuffer(elementArrayBufferIndex);
		super.destroy();
	}
}