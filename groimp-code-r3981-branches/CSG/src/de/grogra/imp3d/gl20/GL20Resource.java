package de.grogra.imp3d.gl20;

public class GL20Resource {
	/**
	 * the class mask
	 */
	final public static int GL20RESOURCE_CLASS_MASK = 0xFFFF0000;
	
	/**
	 * the type mask
	 */
	final public static int GL20RESOURCE_TYPE_MASK = 0x0000FFFF;

	/**
	 * a shape class <code>GL20Resource</code>
	 *
	 * @see <code>GL20ResourceShape</code>
	 */
	final public static int GL20RESOURCE_CLASS_SHAPE = 0x00010000;

	/**
	 * a light class <code>GL20Resource</code>
	 *
	 * @see <code>GL20ResourceLight</code>
	 */
	final public static int GL20RESOURCE_CLASS_LIGHT = 0x00020000;

	/**
	 * a mesh class <code>GL20Resource</code>
	 *
	 * @see <code>GL20ResourceMesh</code>
	 */
	final public static int GL20RESOURCE_CLASS_MESH = 0x00030000;
	
	/**
	 * a shader class <code>GL20Resource</code>
	 * 
	 * @see <code>GL20ResourceShader</code>
	 */
	final public static int GL20RESOURCE_CLASS_SHADER = 0x00040000;
	
	/**
	 * a shader fragment class <code>GL20Resource</code>
	 * 
	 * @see <code>GL20ResourceShaderFragment</code>
	 */
	final public static int GL20RESOURCE_CLASS_SHADERFRAGMENT = 0x00050000;
	
	/**
	 * a texture class <code>GL20Resource</code>
	 * 
	 * @see <code>GL20ResourceTexture</code>
	 */
	final public static int GL20RESOURCE_CLASS_TEXTURE = 0x00060000;

	// ------------------------------------------------------------------------
	// shape types
	// ------------------------------------------------------------------------
	/**
	 * a box shape
	 *
	 * @see <code>GL20ResourceShapeBox</code>
	 */
	final public static int GL20RESOURCE_SHAPE_BOX = GL20RESOURCE_CLASS_SHAPE | 0x0001;

	/**
	 * a frustum shape
	 *
	 * @see <code>GL20ResourceShapeFrustum</code>
	 */
	final public static int GL20RESOURCE_SHAPE_FRUSTUM = GL20RESOURCE_CLASS_SHAPE | 0x0002;
	
	/**
	 * a line shape
	 * 
	 * @see <code>GL20ResourceShapeLine</code>
	 */
	final public static int GL20RESOURCE_SHAPE_LINE = GL20RESOURCE_CLASS_SHAPE | 0x0003;
	
	/**
	 * a line strip shape
	 * 
	 * @see <code>GL20ResourceShapeLineStrip</code>
	 */
	final public static int GL20RESOURCE_SHAPE_LINE_STRIP = GL20RESOURCE_CLASS_SHAPE | 0x0004;

	/**
	 * a parallelogram shape
	 *
	 * @see <code>GL20ResourceShapeParallelogram</code>
	 */
	final public static int GL20RESOURCE_SHAPE_PARALLELOGRAM = GL20RESOURCE_CLASS_SHAPE | 0x0005;

	/**
	 * a plane shape
	 *
	 * @see <code>GL20ResourceShapePlane</code>
	 */
	final public static int GL20RESOURCE_SHAPE_PLANE = GL20RESOURCE_CLASS_SHAPE | 0x0006;

	/**
	 * a polygon shape
	 *
	 * @see <code>GL20ResourceShapePolygons</code>
	 */
	final public static int GL20RESOURCE_SHAPE_POLYGONS = GL20RESOURCE_CLASS_SHAPE | 0x0007;

	/**
	 * a sphere shape
	 *
	 * @see <code>GL20ResourceShapeSphere</code>
	 */
	final public static int GL20RESOURCE_SHAPE_SPHERE = GL20RESOURCE_CLASS_SHAPE | 0x0008;

	// ------------------------------------------------------------------------
	// light types
	// ------------------------------------------------------------------------
	/**
	 * a point light
	 *
	 * @see <code>GL20ResourceLightPoint</code>
	 */
	final public static int GL20RESOURCE_LIGHT_POINT = GL20RESOURCE_CLASS_LIGHT | 0x0001;

	/**
	 * a directional light
	 *
	 * @see <code>GL20ResourceLightDirectional</code>
	 */
	final public static int GL20RESOURCE_LIGHT_DIRECTIONAL = GL20RESOURCE_CLASS_LIGHT | 0x0002;

	/**
	 * a spot light
	 *
	 * @see <code>GL20ResourceLightSpot</code>
	 */
	final public static int GL20RESOURCE_LIGHT_SPOT = GL20RESOURCE_CLASS_LIGHT | 0x0003;

	// ------------------------------------------------------------------------
	// mesh types
	// ------------------------------------------------------------------------
	/**
	 * a single user mesh
	 *
	 * @see <code>GL20ResourceMeshSingleUser</code>
	 */
	final public static int GL20RESOURCE_MESH_SINGLE_USER = GL20RESOURCE_CLASS_MESH | 0x0001;

	/**
	 * a multi user mesh
	 *
	 * @see <code>GL20ResourceMeshMultiUser</code>
	 */
	final public static int GL20RESOURCE_MESH_MULTI_USER = GL20RESOURCE_CLASS_MESH | 0x0002;
	
	// ------------------------------------------------------------------------
	// shader types
	// ------------------------------------------------------------------------
	/**
	 * a shader fragment
	 * 
	 * @see <code>GL20ResourceShaderFragment</code>
	 */
	final public static int GL20RESOURCE_SHADER_PHONG = GL20RESOURCE_CLASS_SHADER | 0x0001;
	
	/**
	 * a RGBA fragment
	 * 
	 * @see <code>GL20ResourceShaderFragmentRGBA
	 */
	final public static int GL20RESOURCE_SHADER_RGBA = GL20RESOURCE_CLASS_SHADER | 0x0002;
	
	// ------------------------------------------------------------------------
	// shader fragment types
	// ------------------------------------------------------------------------	
	/**
	 * a graytone fragment
	 * 
	 * @see <code>GL20ResourceShaderFragmentGraytone</code>
	 */
	final public static int GL20RESOURCE_SHADERFRAGMENT_GRAYTONE = GL20RESOURCE_CLASS_SHADERFRAGMENT | 0x0001;
	
	/**
	 * a RGB fragment
	 * 
	 * @see <code>GL20ResourceShaderFragmentRGB</code>
	 */
	final public static int GL20RESOURCE_SHADERFRAGMENT_RGB = GL20RESOURCE_CLASS_SHADERFRAGMENT | 0x0002;
	
	/**
	 * a blend fragment
	 * 
	 * @see <code>GL20ResourceShaderFragmentBlend</code>
	 */
	final public static int GL20RESOURCE_SHADERFRAGMENT_BLEND = GL20RESOURCE_CLASS_SHADERFRAGMENT | 0x0003;
	
	// ------------------------------------------------------------------------
	// texture types
	// ------------------------------------------------------------------------
	/**
	 * a texture of 1, 2 and 3 dimensions
	 * 
	 * @see <code>GL20ResourceTexture</code>
	 */
	final public static int GL20RESOURCE_TEXTURE = GL20RESOURCE_CLASS_TEXTURE | 0x0001;

	
	/**
	 * TODO implement the <code>GL20ResourceTextureCubeMap</code> class
	 * a cube map texture 
	 * 
	 * @see <code>GL20ResourceTextureCubeMap</code>
	 */
	final public static int GL20RESOURCE_TEXTURE_CUBEMAP = GL20RESOURCE_CLASS_TEXTURE | 0x0002;

	/**
	 * the class and the type of the resource
	 */
	private int resourceClassType;

	protected GL20Resource(int resourceClassType) {
		this.resourceClassType = resourceClassType;
	}

	/**
	 * return the type of the <code>GL20Resource</code>
	 *
	 * @return the type of the <code>GL20Resource</code>
	 */
	public int getResourceType() {
		return (resourceClassType & GL20RESOURCE_TYPE_MASK);
	}

	/**
	 * return the class of the <code>GL20Resource</code>
	 *
	 * @return the class of the <code>GL20Resource</code>
	 */
	public int getResourceClass() {
		return (resourceClassType & GL20RESOURCE_CLASS_MASK);
	}

	/**
	 * return the class and the type of the <code>GL20Resource</code>
	 *
	 * @return the class and the type of the <code>GL20Resource</code>
	 */
	public int getResourceClassType() {
		return resourceClassType;
	}

	/**
	 * check if this <code>GL20Resource</code> is up to date.
	 * the resource should check if all external resources are still valid,
	 * but don't update them in this method.
	 * every implementation must implement this method and must call
	 * <code>super.isUpToDate()</code>.
	 *
	 * @return <code>true</code> - this <code>GL20Resource</code> is up to date
	 * @see <code>update()</code>
	 */
	public boolean isUpToDate() {
		return true;
	}

	/**
	 * update the state of this <code>GL20Resource</code>
	 */
	public void update() {
		// nothing to do
	}

	/**
	 * destroy the resource. every class have to implement this method and have
	 * to call <code>super.destroy()</code> at the end.
	 */
	public void destroy() {
		// nothing to do
	}
}