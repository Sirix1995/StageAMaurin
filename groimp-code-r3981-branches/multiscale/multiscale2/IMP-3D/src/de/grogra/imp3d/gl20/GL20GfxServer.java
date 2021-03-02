package de.grogra.imp3d.gl20;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector4f;

import de.grogra.imp.edit.ViewSelection;
import de.grogra.imp3d.gl20.GL20Const;
import de.grogra.imp3d.gl20.GL20MeshServer;
import de.grogra.imp3d.gl20.GL20Node;
import de.grogra.imp3d.gl20.GL20Resource;
import de.grogra.imp3d.gl20.GL20ResourceShape;
import de.grogra.imp3d.gl20.GL20ResourceLight;

public class GL20GfxServer {
	/**
	 * 
	 */
	final private static int MAX_TEXTURE_STAGES = 16;
	
	/**
	 * primitive mask
	 */
	final public static int PRIMITIVE_MASK = 0xF;

	/**
	 * triangle indicator
	 */
	final public static int TRIANGLES = 0x1;

	/**
	 * quadrilaterals indicator
	 */
	final public static int QUADS = 0x2;
	
	/**
	 * line indicator
	 */
	final public static int LINES = 0x3;

	/**
	 * normal mask
	 */
	final public static int NORMAL_MASK = 0xF0;

	/**
	 * no normals are stored
	 */
	final public static int HAS_NO_NORMALS = 0x0;

	/**
	 * normals are stored in byte indicator
	 */
	final public static int HAS_NORMALS_BYTE = 0x10;

	/**
	 * normals are stored in float indicator
	 */
	final public static int HAS_NORMALS_FLOAT = 0x20;

	/**
	 * texture mask
	 */
	final public static int TEXTURE_MASK = 0xF00;

	/**
	 * no texture coordinates are stored
	 */
	final public static int HAS_NO_TEXTURE_UV = 0x0;

	/**
	 * texture coordinates are stored in short indicator
	 */
	final public static int HAS_TEXTURE_UV_SHORT = 0x100;

	/**
	 * texture coordinates are stored in float indicator
	 */
	final public static int HAS_TEXTURE_UV_FLOAT = 0x200;

	/**
	 * vertex mask
	 */
	final public static int VERTEX_MASK = 0xF000;

	/**
	 * vertex with 3 elements are stored in short indicator
	 */
	final public static int HAS_VERTEX_3_SHORT = 0x1000;

	/**
	 * vertex with 3 elements are stored in float indicator
	 */
	final public static int HAS_VERTEX_3_FLOAT = 0x2000;

	/**
	 * vertex with 4 elements are stored in short indicator
	 */
	final public static int HAS_VERTEX_4_SHORT = 0x3000;

	/**
	 * vetrex with 4 elements are stored in float indicator
	 */
	final public static int HAS_VERTEX_4_FLOAT = 0x4000;

	/**
	 * order mask
	 */
	final public static int ORDER_MASK = 0xF0000;

	/**
	 * vertex - normal - uv order indicator
	 */
	final public static int ORDER_V_N_UV = 0x10000;

	/**
	 * vertex - uv - normal order indicator
	 */
	final public static int ORDER_V_UV_N = 0x20000;

	/**
	 * normal - vertex - uv order indicator
	 */
	final public static int ORDER_N_V_UV = 0x30000;

	/**
	 * normal - uv - vertex order indicator
	 */
	final public static int ORDER_N_UV_V = 0x40000;

	/**
	 * uv - vertex - normal order indicator
	 */
	final public static int ORDER_UV_V_N = 0x50000;

	/**
	 * uv - normal - vertex order indicator
	 */
	final public static int ORDER_UV_N_V = 0x60000;

	/**
	 * elements storage mask
	 */
	final public static int ELEMENTS_MASK = 0xF00000;

	/**
	 * elements are stored as byte indicator
	 */
	final public static int ELEMENTS_BYTE = 0x100000;

	/**
	 * elements are stored as short
	 */
	final public static int ELEMENTS_SHORT = 0x200000;

	/**
	 * elements are stored as int
	 */
	final public static int ELEMENTS_INT = 0x300000;

	/**
	 * the server instance of this <code>GL20GfxServer</code>
	 */
	private static GL20GfxServer singleton = new GL20GfxServer();

	/**
	 * the ID of the current frame, will be incremented every frame
	 */
	private static int currentFrameID;

	/**
	 * the ID of the current OpenGL context, will be incremented every context change
	 */
	private static int currentContextID;

	/**
	 * stores the array buffer index when the array buffer is opened for writing
	 */
	private int openedArrayBufferIndex;

	/**
	 * stores the element array buffer index when the element array buffer is opened for writing
	 */
	private int openedElementArrayBufferIndex;

	/**
	 * the currently bound array buffer index
	 */
	private int boundArrayBufferIndex;

	/**
	 * the currently bound element array buffer index
	 */
	private int boundElementArrayBufferIndex;

	/**
	 * the currently opened array buffer
	 */
	private ByteBuffer arrayBuffer;

	/**
	 * the currently opened element array buffer
	 */
	private ByteBuffer elementArrayBuffer;

	/**
	 * free capacity of the opened array buffer
	 */
	private int freeArrayBufferCapacityRestBytes;

	/**
	 * free capacity of the opened element array buffer
	 */
	private int freeElementArrayBufferCapacityRestBytes;
	
	/**
	 * the current view to clip matrix
	 */
	private Matrix4d viewToClipMatrix = new Matrix4d(GL20Const.identityMatrix4d);
	
	/**
	 * the world to view matrix
	 */
	private Matrix4d worldToViewMatrix = new Matrix4d(GL20Const.identityMatrix4d);
	
	/**
	 * final world to clip matrix
	 */
	private Matrix4d worldToClipMatrix = new Matrix4d(GL20Const.identityMatrix4d);

	/**
	 * if we are between <code>beginScene()</code> and <code>endScene()</code>
	 */
	private boolean inScene = false;

	/**
	 * if we are currently in <code>renderScene()</code>
	 */
	private boolean inRendering = false;
	
	/**
	 * current color
	 */
	private Vector4f currentColor = new Vector4f(1.0f,1.0f,1.0f,1.0f);
	
	/**
	 * currently bound texture indices
	 */
	private int[] boundTextureIndex = new int[MAX_TEXTURE_STAGES];
	
	/**
	 * currently bound texture dimensions
	 */
	private int[] boundTextureDimension = new int[MAX_TEXTURE_STAGES];
	
	/**
	 * available texture stages
	 */
	private int textureStagesAvailable = 0;

	/**
	 * should <code>setWorldTransformationMatrix()</code> calculate the normal matrix
	 */
	private boolean calculateNormalMatrix;
	
	/**
	 * list of nodes that are selected
	 */
	private ArrayList<GL20Node> selectedNodes = new ArrayList<GL20Node>();

	/**
	 * list of nodes in the current scene that has an opaque shader
	 */
	private ArrayList<GL20Node> opaqueShaderNodes = new ArrayList<GL20Node>();
	
	/**
	 * list of nodes that contains line strips
	 */
	private ArrayList<GL20Node> lineStripNodes = new ArrayList<GL20Node>();
	
	/**
	 * list of nodes for drawing the current tool
	 */
	private ArrayList<GL20Node> toolNodes = new ArrayList<GL20Node>();

	/**
	 * the current OpenGL context
	 */
	private GL currentGL;
	
	/**
	 * current GL program handle
	 */
	private int programHandle = 0;

	private GL20GfxServer() {
		currentFrameID = 0;
		currentContextID = 0;
	}

	/**
	 * get the server instance of this <code>GL20GfxServer</code>
	 *
	 * @return the server instance
	 */
	final public static GL20GfxServer getInstance() {
		return singleton;
	}

	/**
	 * get the current frame ID. every frame the frame ID will be
	 * incremented.
	 *
	 * @return the current frame ID
	 */
	final public int getFrameID() {
		return currentFrameID;
	}

	/**
	 * get the current OpenGL context ID. every context change the context
	 * ID will be incremented.
	 *
	 * @return the current context ID
	 */
	final public int getContextID() {
		return currentContextID;
	}
	
	/**
	 * set the view to clip matrix. can only be setted before <code>beginScene()</code>
	 * 
	 * @param viewToClipMatrix the matrix that should set
	 * @return <code>true</code> - the view to clip matrix was set successful
	 */
	final public boolean setViewToClipMatrix(Matrix4d viewToClipMatrix) {
		boolean returnValue = false;
		
		if ((inScene == false) && (inRendering == false)) {
			this.viewToClipMatrix.set(viewToClipMatrix);
			returnValue = true;
		}
		
		return returnValue;
	}
	
	/**
	 * get the view to clip matrix
	 * 
	 * @return the view to clip matrix
	 */
	final public Matrix4d getViewToClipMatrix() {
		return viewToClipMatrix;
	}
	
	/**
	 * set the world to view matrix. can only be setted before <code>beginScene()</code>
	 * 
	 * @param worldToViewMatrix the world to view matrix
	 * @return <code>true</code> - the world to view matrix was set successful
	 */
	final public boolean setWorldToViewMatrix(Matrix4d worldToViewMatrix) {
		boolean returnValue = false;
		
		if ((inScene == false) && (inRendering == false)) {
			this.worldToViewMatrix.set(worldToViewMatrix);
			returnValue = true;
		}
		
		return returnValue;
	}
	
	/**
	 * get the world to view matrix
	 * 
	 * @return the world to view matrix
	 */
	final public Matrix4d getWorldToViewMatrix() {
		return worldToViewMatrix;
	}
	
	/**
	 * get the world to clip matrix.
	 * only valid when <code>inScene()</code> or <code>inRendering()</code>
	 * 
	 * @return <code>null</code> - not <code>inScene()</code> or <code>inRendering()</code>
	 * otherwise - the world to clip matrix 
	 */
	final public Matrix4d getWorldToClipMatrix() {
		if ((inScene == true) || (inRendering == true))
			return worldToClipMatrix;
		else
			return null;
	}
	
	/**
	 * set the current color
	 * 
	 * @param color the color that should set
	 */
	final public void setCurrentColor(Vector4f color) {
		this.currentColor.set(color);
		if (inRendering == true) {
			currentGL.glColor4f(color.x, color.y, color.z, color.w);
		}
	}
	
	/**
	 * get the current color
	 * 
	 * @return the current color
	 */
	final public Vector4f getCurrentColor() {
		return currentColor;
	}
	
	final public void checkExtensions() {
		if (currentGL != null) {
			// check for available texture stages
			int[] temp = new int[1]; 
			currentGL.glGetIntegerv(GL.GL_MAX_TEXTURE_UNITS, temp, 0);
			
			textureStagesAvailable = (temp[0] < MAX_TEXTURE_STAGES) ? temp[0] : MAX_TEXTURE_STAGES;
		}
	}
	
	/**
	 * begin a new scene
	 *
	 * @return <code>true</code> - successful
	 */
	final public boolean beginScene(GL currentGL) {
		boolean returnValue = false;

		if (inScene == false) {
			if (this.currentGL != currentGL) {
				// OpenGL context has changed
				currentContextID++;
				
				this.currentGL = currentGL;
				checkExtensions();				
				
				programHandle = currentGL.glCreateProgram();
				int vertexShaderHandle = currentGL.glCreateShader(GL.GL_VERTEX_SHADER);
				int fragmentShaderHandle = currentGL.glCreateShader(GL.GL_FRAGMENT_SHADER);
				
				String vertexShader[] = new String[1];
				vertexShader[0] =
						"varying vec3 normal, lightV, vertexPosEye;" +
						"varying vec4 diffuse, ambientGlobal, ambient;" +
						"varying float vertexLightDist;" +
						"" +
						"void main(void)" +
						"{" +
						"vec3 vertexLightV;" +
						"" +
						"normal = -normalize(gl_NormalMatrix * gl_Normal);" +
						"" +
						"vertexPosEye = vec3(gl_ModelViewMatrix * gl_Vertex);" +
						"vertexLightV = vec3(vertexPosEye - vec3(gl_LightSource[0].position));" +
						"lightV = -normalize(vertexLightV);" +
						"vertexLightDist = length(vertexLightV);" +
						"" +
						"diffuse = gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse;" +
						"" +
						"ambient = gl_FrontMaterial.ambient * gl_LightSource[0].ambient;" +
						"ambientGlobal = gl_FrontMaterial.ambient * gl_LightModel.ambient;" +
						"" +
						"gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;" +
						"}";
				IntBuffer length = IntBuffer.allocate(1);
				length.put(0,vertexShader[0].length());
				currentGL.glShaderSource(vertexShaderHandle,1,vertexShader,length);
				
				String fragmentShader[] = new String[1];
				fragmentShader[0] = 
						"varying vec3 normal, lightV, vertexPosEye;" +
						"varying vec4 diffuse, ambientGlobal, ambient;" +
						"varying float vertexLightDist;" +
						"" +
						"void main(void)" +
						"{" +
						"	vec3 normalN, halfVN;" +
						"	float NdotL, NdotHV;" +
						"	vec4 color = ambientGlobal;" +
						"	float att;" +
						"	" +
						"	normalN = normalize(normal);" +
						"	" +
						"	NdotL = max(dot(normalN,normalize(vec3(gl_LightSource[0].position) - vertexPosEye)),0.0);" +
						"	" +
						"	if (NdotL > 0.0) {" +
						"		att = 1.0 / (gl_LightSource[0].constantAttenuation + " +
						"					 gl_LightSource[0].linearAttenuation * vertexLightDist + " +
						"					 gl_LightSource[0].quadraticAttenuation * vertexLightDist * vertexLightDist);" +
						"		color += att * (diffuse * NdotL + ambient);" +
						"		" +
						"		halfVN = normalize(vec3(gl_LightSource[0].position) - (2.0 * vertexPosEye).xyz);" +
						"		NdotHV = max(dot(normalN,halfVN),0.0);" +
						"		color += att * gl_FrontMaterial.specular * gl_LightSource[0].specular *" +
						"				 pow(NdotHV,gl_FrontMaterial.shininess);" +
						"	}" +
						"	" +
						"	gl_FragColor = color;" +
						"}";
				length.put(0,fragmentShader[0].length());
				currentGL.glShaderSource(fragmentShaderHandle,1,fragmentShader,length);

				currentGL.glCompileShader(vertexShaderHandle);
				if (checkGLObjectInfoLog(vertexShaderHandle,"VertexShader") == true) {
					currentGL.glCompileShader(fragmentShaderHandle);
					if (checkGLObjectInfoLog(fragmentShaderHandle,"FragmentShader") == true) {
						currentGL.glAttachShader(programHandle, vertexShaderHandle);
						currentGL.glAttachShader(programHandle, fragmentShaderHandle);
						currentGL.glDeleteShader(vertexShaderHandle);
						currentGL.glDeleteShader(fragmentShaderHandle);
						currentGL.glLinkProgram(programHandle);
						if (checkGLObjectInfoLog(programHandle,"Program") == true)
							System.out.print("jup!");
					}
				}
			}
						
			// create 'world to clip' matrix
			worldToClipMatrix.set(viewToClipMatrix);
			worldToClipMatrix.mul(worldToViewMatrix);			

			selectedNodes.clear();
			lineStripNodes.clear();
			
			final int toolNodeCount = toolNodes.size();
			for (int i=0;i < toolNodeCount;i++) {
				toolNodes.get(i).destroy();
			}
			toolNodes.clear();
			opaqueShaderNodes.clear();

			boundArrayBufferIndex = GL20Const.INVALID_ID;
			boundElementArrayBufferIndex = GL20Const.INVALID_ID;
			openedArrayBufferIndex = GL20Const.INVALID_ID;
			openedElementArrayBufferIndex = GL20Const.INVALID_ID;
			
			// set bound textures to 0
			// set dimensions of all bound texture stages to 0
			for (int textureStage=0;textureStage < textureStagesAvailable;textureStage++) {
				boundTextureIndex[textureStage] = 0;
				boundTextureDimension[textureStage] = 0;
			}

			currentFrameID++;
			inScene = true;
			returnValue = true;
		}

		return returnValue;
	}

	/**
	 * end the current scene
	 */
	final public void endScene() {
		if (inScene == true) {
			inScene = false;
			renderScene();
		}
	}

	/**
	 * check if this <code>GL20GfxServer</code> is between <code>beginScene()</code>
	 * and <code>endScene()</code>
	 *
	 * @return <code>true</code> - this <code>GL20GfxServer</code> is between
	 * <code>beginScene()</code> and <code>endScene()</code>
	 */
	final public boolean inScene() {
		return inScene;
	}
	
	final private boolean checkGLObjectInfoLog(int shaderHandle,String text) {
		boolean returnValue = false;
		
		int infoLogLength[] = new int[1];
		currentGL.glGetObjectParameterivARB(shaderHandle, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, infoLogLength, 0);
		if (infoLogLength[0] > 1) {
			byte errorMsg[] = new byte[infoLogLength[0]];
			int errorMsgLength[] = new int[1];
			currentGL.glGetInfoLogARB(shaderHandle, infoLogLength[0], errorMsgLength, 0, errorMsg, 0);
			System.out.println(text + ": " + new String(errorMsg));
		}
		else {
			returnValue = true;
			System.out.println(text + ": successful!");
		}
		
		return returnValue;
	}

	/**
	 * render the complete scene
	 *
	 * @return <code>true</code> - successful rendered the scene
	 */
	final private boolean renderScene() {
		boolean returnValue = false;

		if (inScene == false) {
			inRendering = true;
			
			// set up OpenGL stuff for new frame
			currentGL.glClearColor(0.75f, 0.75f, 0.75f, 1.0f);
			currentGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			
			currentGL.glMatrixMode(GL.GL_PROJECTION);
			currentGL.glLoadMatrixd(transformToGLMatrix(viewToClipMatrix),0);
			currentGL.glMatrixMode(GL.GL_MODELVIEW);
			currentGL.glLoadMatrixd(transformToGLMatrix(worldToViewMatrix),0);
			
			float lightPos[] = new float[4];
			lightPos[0] = 0.0f;	//(float)Math.sin(Math.PI / 180.0f * ((double)calendar.getTimeInMillis() / 23.43f)) * 2.0f;
			lightPos[1] = 0.0f;	//(float)Math.cos(Math.PI / 180.0f * ((double)calendar.getTimeInMillis() / 25.34f)) * 2.0f;
			lightPos[2] = 0.0f;
			lightPos[3] = 0.0f;
			currentGL.glLightfv(GL.GL_LIGHT0,GL.GL_POSITION,lightPos,0);
			currentGL.glMatrixMode(GL.GL_PROJECTION);
//			currentGL.glPushMatrix();
			currentGL.glMultMatrixd(transformToGLMatrix(worldToViewMatrix),0);
			currentGL.glMatrixMode(GL.GL_MODELVIEW);
			currentGL.glLoadIdentity();
			
			GL20MeshServer meshServer = GL20MeshServer.getInstance();
			meshServer.updateAllMultiUserMeshes();
			
			currentGL.glShadeModel(GL.GL_SMOOTH);
			
			currentGL.glEnable(GL.GL_DEPTH_TEST);
			currentGL.glDepthFunc(GL.GL_LESS);
			currentGL.glEnable(GL.GL_LIGHTING);
			currentGL.glEnable(GL.GL_LIGHT0);
			currentGL.glEnable(GL.GL_NORMALIZE);
			currentGL.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			currentGL.glEnable(GL.GL_CULL_FACE);
//			currentGL.glEnable(GL.GL_COLOR_MATERIAL);
//			currentGL.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);
			float diffuseColor[] = new float[4];
			diffuseColor[0] = 0.3f;
			diffuseColor[1] = 0.3f;
			diffuseColor[2] = 0.3f;
			diffuseColor[3] = 1.0f;
			float specularColor[] = new float[4];
			specularColor[0] = 0.2f;
			specularColor[1] = 0.2f;
			specularColor[2] = 0.2f;
			specularColor[3] = 1.0f;
//			currentGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, diffuseColor, 0);
			currentGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, specularColor, 0);
			currentGL.glMaterialf(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS,128.0f);
			currentGL.glCullFace(GL.GL_BACK);
			
			currentGL.glColor4f(1.0f,1.0f,1.0f,1.0f);
			currentGL.glBegin(GL.GL_LINES);
			currentGL.glVertex3f(-1.0f, 0.0f, 1.0f);
			currentGL.glVertex3f(1.0f, 0.0f, 1.0f);
			currentGL.glVertex3f(0.0f, -1.0f, 1.0f);
			currentGL.glVertex3f(0.0f, 1.0f, 1.0f);
			currentGL.glVertex3f(0.0f, 0.0f, 0.0f);
			currentGL.glVertex3f(0.0f, 0.0f, 2.0f);
			currentGL.glEnd();			

			//currentGL.glUseProgram(programHandle);
			
			currentGL.glEnableClientState(GL.GL_NORMAL_ARRAY);
//			currentGL.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
			currentGL.glEnableClientState(GL.GL_VERTEX_ARRAY);
			currentGL.glColor4f(0.75f,0.75f,0.75f,1.0f);

			calculateNormalMatrix = true;

			final int nodeCount = opaqueShaderNodes.size();
			for (int nodeIndex=0;nodeIndex < nodeCount;nodeIndex++) {
				GL20ResourceShape resourceShape = (GL20ResourceShape)(opaqueShaderNodes.get(nodeIndex)).getResource();
				GL20ResourceShader resourceShader = resourceShape.getShader();
				if (resourceShader.isUpToDate() == false)
					resourceShader.update();
				
				resourceShader.applyShader(false);
				
				currentGL.glUseProgram(programHandle);
				int matrixLocation = currentGL.glGetUniformLocation(programHandle,GL20GLSLCode.WORLD_TO_VIEW_MATRIX_NAME);
				if (matrixLocation != -1) {
					transformToGLMatrix(worldToViewMatrix);
					float matrix[] = new float[16];
					for (int i=0;i < 16;i++)
						matrix[i] = (float)openGLMatrix[i];
					currentGL.glUniformMatrix4fv(matrixLocation, 1, false, matrix, 0);
				}
				resourceShape.applyGeometry();
				
				currentGL.glUseProgram(0);
			}
			
			currentGL.glUseProgram(0);
			
			// draw line stripes
			calculateNormalMatrix = false;
			currentGL.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
			currentGL.glDisableClientState(GL.GL_NORMAL_ARRAY);
			currentGL.glDisable(GL.GL_NORMALIZE);
			currentGL.glDisable(GL.GL_LIGHTING);
			currentGL.glDisable(GL.GL_LIGHT0);
			
			final int lineStripNodeCount = lineStripNodes.size();
			for (int nodeIndex=0;nodeIndex < lineStripNodeCount;nodeIndex++) {
				GL20Node currentNode = lineStripNodes.get(nodeIndex);
				GL20ResourceShape resourceShape = (GL20ResourceShape)currentNode.getResource();
				currentGL.glColor4f(1.0f,1.0f,1.0f,1.0f);
				resourceShape.applyGeometry();
			}
			
			// draw selection
			currentGL.glDisable(GL.GL_DEPTH_TEST);
			currentGL.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);
			final int selectedNodeCount = selectedNodes.size();
			final float oneOver255 = 1.0f / 255.0f;
			for (int nodeIndex=0;nodeIndex < selectedNodeCount;nodeIndex++) {
				GL20Node currentNode = selectedNodes.get(nodeIndex);
				int packedColor = ViewSelection.getColor(0xFFFFFFFF, currentNode.getSelectionState(), true);
				currentGL.glColor4f((float)((packedColor >> 16) & 0xFF) * oneOver255,
									(float)((packedColor >> 8) & 0xFF) * oneOver255,
									(float)(packedColor & 0xFF) * oneOver255,
									1.0f);
				
				GL20ResourceShape resourceShape = (GL20ResourceShape)currentNode.getResource();
				resourceShape.applyGeometry();
			}
			
			// draw tools
			boolean lightingEnabled = false;
			currentGL.glPolygonMode(GL.GL_FRONT_AND_BACK,GL.GL_FILL);
			final int toolNodeCount = toolNodes.size();
			for (int nodeIndex=0;nodeIndex < toolNodeCount;nodeIndex++) {
				GL20Node currentNode = toolNodes.get(nodeIndex);
				GL20ResourceShape resourceShape = (GL20ResourceShape)currentNode.getResource();
				if (resourceShape != null) {
					if ((resourceShape.getResourceClassType() == GL20Resource.GL20RESOURCE_SHAPE_LINE_STRIP) ||
						(resourceShape.getResourceClassType() == GL20Resource.GL20RESOURCE_SHAPE_LINE)) {
						if (lightingEnabled == true) {
							currentGL.glDisable(GL.GL_LIGHTING);
							currentGL.glDisable(GL.GL_LIGHT0);
							currentGL.glDisable(GL.GL_NORMALIZE);
							currentGL.glDisableClientState(GL.GL_NORMAL_ARRAY);
							calculateNormalMatrix = true;
							lightingEnabled = false;
						}
					}
					else {
						if (lightingEnabled == false) {
							currentGL.glEnable(GL.GL_LIGHTING);
							currentGL.glEnable(GL.GL_LIGHT0);
							currentGL.glEnable(GL.GL_NORMALIZE);
							currentGL.glEnableClientState(GL.GL_NORMAL_ARRAY);
							calculateNormalMatrix = false;
							lightingEnabled = true;
						}
					}
					resourceShape.applyGeometry();
				}
			}
			
			if (lightingEnabled == true) {
				currentGL.glDisable(GL.GL_LIGHTING);
				currentGL.glDisable(GL.GL_LIGHT0);
				currentGL.glDisable(GL.GL_NORMALIZE);
				currentGL.glDisableClientState(GL.GL_NORMAL_ARRAY);
			}
			currentGL.glDisableClientState(GL.GL_VERTEX_ARRAY);

			currentGL.glMatrixMode(GL.GL_PROJECTION);
			currentGL.glPopMatrix();
			
			currentGL.glFlush();
			
			inRendering = false;
			returnValue = true;
		}

		return returnValue;
	}

	/**
	 * add a <code>GL20Node</code> to the current scene.
	 *
	 * @param node the node that should be added
	 */
	final public void addNodeToScene(GL20Node node) {
		assert inScene == true;

		GL20Resource resource = node.getResource();

		if (resource == null)
			// node has no <code>GL20Resource</code>
			return;

		switch (resource.getResourceClass()) {
		case GL20Resource.GL20RESOURCE_CLASS_SHAPE:
			addShapeNodeToScene(node);
			break;
		case GL20Resource.GL20RESOURCE_CLASS_LIGHT:
			addLightNodeToScene(node);
			break;
		}
	}
	
	/**
	 * add a <code>GL20Node</code> to the current tool nodes
	 * 
	 * @param node the node that should be added
	 */
	final public void addNodeToTool(GL20Node node) {
		assert inScene == true;
		
		toolNodes.add(node);
	}

	/**
	 * add a <code>GL20Node</code> with a <code>GL20ResourceShape</code>
	 * resource to the scene
	 *
	 * @param node the <code>GL20Node</code> with a <code>GL20ResourceShape</code>
	 */
	final private void addShapeNodeToScene(GL20Node node) {
		GL20ResourceShape shapeResource = (GL20ResourceShape)node.getResource();

		if (shapeResource.getResourceClassType() != GL20Resource.GL20RESOURCE_SHAPE_LINE_STRIP)
			opaqueShaderNodes.add(node);
		else
			lineStripNodes.add(node);
			
		if (node.getSelectionState() != 0)
			selectedNodes.add(node);
	}

	/**
	 * add a <code>GL20Node</code> with a <code>GL20ResourceLight</code>
	 * resource to the scene
	 *
	 * @param node the <code>GL20Node</code> with a <code>GL20ResourceLight</code>
	 */
	final private void addLightNodeToScene(GL20Node node) {
		GL20ResourceLight lightResource = (GL20ResourceLight)node.getResource();
	}

	private double[] openGLMatrix = new double[16];

	final private double[] transformToGLMatrix(Matrix4d matrix) {
		openGLMatrix[0] = matrix.m00; openGLMatrix[1] = matrix.m10; openGLMatrix[2] = matrix.m20; openGLMatrix[3] = matrix.m30;
		openGLMatrix[4] = matrix.m01; openGLMatrix[5] = matrix.m11; openGLMatrix[6] = matrix.m21; openGLMatrix[7] = matrix.m31;
		openGLMatrix[8] = matrix.m02; openGLMatrix[9] = matrix.m12; openGLMatrix[10] = matrix.m22; openGLMatrix[11] = matrix.m32;
		openGLMatrix[12] = matrix.m03; openGLMatrix[13] = matrix.m13; openGLMatrix[14] = matrix.m23; openGLMatrix[15] = matrix.m33;

		return openGLMatrix;
	}

	/**
	 * set the world transformation matrix
	 *
	 * @param worldMatrix the world transformation matrix
	 */
	final public void setWorldTransformationMatrix(Matrix4d worldMatrix) {
		if (inRendering == true) {
			//currentGL.glLoadMatrixd(transformToGLMatrix(worldToViewMatrix),0);
			currentGL.glLoadMatrixd(transformToGLMatrix(worldMatrix),0);

			if (calculateNormalMatrix == true) {
				// TODO
			}
		}
	}

	/**
	 * create a new array buffer that can contain vertex data
	 *
	 * @return the index of the created array buffer
	 */
	final public int createArrayBuffer() {
		int[] returnValue = new int[1];

		currentGL.glGenBuffers(1,returnValue,0);

		return returnValue[0];
	}

	/**
	 * delete an array buffer given by its index
	 *
	 * @param index the array buffer index
	 */
	final public void deleteArrayBuffer(int arrayBufferIndex) {
		if (arrayBufferIndex > 0) {
			int ids[] = new int[1];
			currentGL.glDeleteBuffers(1,ids,0);
		}
	}

	/**
	 * create a new element array buffer that can contain indices to vertices
	 * in a array buffer
	 *
	 * @return the index of the created element array buffer
	 * @see <code>createArrayBuffer</code>
	 */
	final public int createElementArrayBuffer() {
		return createArrayBuffer();
	}

	/**
	 * delete an element array buffer given by its index
	 *
	 * @param index the element array buffer index
	 */
	final public void deleteElementArrayBuffer(int elementArrayBufferIndex) {
		deleteArrayBuffer(elementArrayBufferIndex);
	}

	/**
	 * bind an array buffer
	 *
	 * @param arrayBufferIndex the index of the array buffer that should bound
	 * @return <code>true</code> - the buffer with the given index was bound successful
	 */
	final private boolean bindArrayBuffer(int arrayBufferIndex) {
		boolean returnValue = false;

		if ((arrayBufferIndex != GL20Const.INVALID_ID) && (openedArrayBufferIndex == GL20Const.INVALID_ID)) {
			if (boundArrayBufferIndex != arrayBufferIndex) {
				currentGL.glBindBuffer(GL.GL_ARRAY_BUFFER,arrayBufferIndex);
				boundArrayBufferIndex = arrayBufferIndex;
			}

			returnValue = true;
		}

		return returnValue;
	}
	
	final private boolean unbindArrayBuffer(int arrayBufferIndex) {
		boolean returnValue = false;
		
		if ((arrayBufferIndex != GL20Const.INVALID_ID) && (openedArrayBufferIndex == GL20Const.INVALID_ID) &&
			(boundArrayBufferIndex == arrayBufferIndex)) {
			currentGL.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
			
			boundArrayBufferIndex = GL20Const.INVALID_ID;
			returnValue = true;
		}
		
		return returnValue;
	}

	/**
	 * bind an element array buffer
	 *
	 * @param elementArrayBufferIndex the index of the element array buffer that should bound
	 * @return <code>true</code> - the buffer with the given index was bound successful
	 */
	final private boolean bindElementArrayBuffer(int elementArrayBufferIndex) {
		boolean returnValue = false;

		if ((elementArrayBufferIndex != GL20Const.INVALID_ID) && (openedElementArrayBufferIndex == GL20Const.INVALID_ID)) {
			if (boundElementArrayBufferIndex != elementArrayBufferIndex) {
				currentGL.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER,elementArrayBufferIndex);
				boundElementArrayBufferIndex = elementArrayBufferIndex;
			}

			returnValue = true;
		}

		return returnValue;
	}
	
	final private boolean unbindElementArrayBuffer(int elementArrayBufferIndex) {
		boolean returnValue = false;
		
		if ((elementArrayBufferIndex != GL20Const.INVALID_ID) && (openedElementArrayBufferIndex == GL20Const.INVALID_ID) &&
			(boundElementArrayBufferIndex == elementArrayBufferIndex)) {
			currentGL.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
			
			boundElementArrayBufferIndex = GL20Const.INVALID_ID;
			returnValue = true;
		}
		
		return returnValue;
	}	

	/**
	 * set the texture attributes for the current array buffer
	 *
	 * @param elementCount number of elements that one texture coordinate contains. 1,2,3 or 4.
	 * @param elementType data type of an element. GL_SHORT or GL_FLOAT are valid.
	 * @param offset offset in bytes from the beginning of a vertex block to the first element
	 * @param stride number of bytes after last element to the first element in the next vertex block
	 */
	final private void setTextureAttributes(int elementCount,int elementType,int offset,int stride) {
		currentGL.glTexCoordPointer(elementCount,elementType,stride,offset);
	}

	/**
	 * set the normal attributes for the current array buffer
	 *
	 * @param elementType data type of an element. GL_BYTE or GL_FLOAT are valid.
	 * @param offset offset in bytes from the beginning of a vertex block to the first element
	 * @param stride number of bytes after last element to the first element in the next vertex block
	 */
	final private void setNormalAttributes(int elementType,int offset,int stride) {
		currentGL.glNormalPointer(elementType,stride,offset);
	}

	/**
	 * set the vertex attributes for the current array buffer
	 *
	 * @param elementCount number of elements that one vertex contains. 2, 3 or 4.
	 * @param elementType data type of an element. GL_SHORT or GL_FLOAT are valid.
	 * @param offset offset in bytes from the beginning of a vertex block to the first element
	 * @param stride number of bytes after last element to the first element of the next vertex block
	 */
	final private void setVertexAttributes(int elementCount,int elementType,int offset,int stride) {
		currentGL.glVertexPointer(elementCount,elementType,stride,offset);
	}

	/**
	 * draw an array buffer while using an element array buffer for the element indices.
	 * with <code>firstElementIndex</code> and <code>elementUsedCount</code> you can control
	 * if the whole element array buffer or just a part of it should used for drawing
	 *
	 * @param elementArrayBufferIndex the index of the element array buffer
	 * @param arrayBufferIndex the index of the array buffer
	 * @param firstElementIndex the index of the first index in element array buffer
	 * @param elementUsedCount the number of elements that should used starting at <code>firstElementIndex</code>
	 * @param flags flags the inform the <code>GL20GfxServer</code> how the buffers are build up
	 * @return <code>true</code> - successful drawing
	 */
	final public boolean drawElementArrayBuffer(int elementArrayBufferIndex,int arrayBufferIndex,int firstElementIndex,int elementUsedCount,int flags) {
		boolean returnValue = false;

		// FIXME only for debug reason
		// System.out.println(elementArrayBufferIndex + "," + arrayBufferIndex + "," + firstElementIndex + "," + elementUsedCount + "," + flags);

		if ((inRendering == true) &&
			(elementArrayBufferIndex != GL20Const.INVALID_ID) &&
		    (arrayBufferIndex != GL20Const.INVALID_ID) &&
		    (firstElementIndex >= 0) &&
		    (elementUsedCount > 0) &&
		    (((flags & PRIMITIVE_MASK) == TRIANGLES) || ((flags & PRIMITIVE_MASK) == QUADS) || ((flags & PRIMITIVE_MASK) == LINES)) &&
		    (openedElementArrayBufferIndex == GL20Const.INVALID_ID) &&
		    (openedArrayBufferIndex == GL20Const.INVALID_ID)) {
			if (bindElementArrayBuffer(elementArrayBufferIndex) == true) {
				if (bindArrayBuffer(arrayBufferIndex) == true) {
					int vertexSize = 0;
					int vertexElementType = 0;
					int vertexElementCount = 0;
					switch (flags & VERTEX_MASK) {
					case HAS_VERTEX_3_SHORT:
						vertexSize = 3 * (Short.SIZE / 8);
						vertexElementType = GL.GL_SHORT;
						vertexElementCount = 3;
						break;
					case HAS_VERTEX_4_SHORT:
						vertexSize = 4 * (Short.SIZE / 8);
						vertexElementType = GL.GL_SHORT;
						vertexElementCount = 4;
						break;
					case HAS_VERTEX_3_FLOAT:
						vertexSize = 3 * (Float.SIZE / 8);
						vertexElementType = GL.GL_FLOAT;
						vertexElementCount = 3;
						break;
					case HAS_VERTEX_4_FLOAT:
						vertexSize = 4 * (Float.SIZE / 8);
						vertexElementType = GL.GL_FLOAT;
						vertexElementCount = 4;
						break;
					}

					int normalSize = 0;
					int normalElementType = 0;
					switch (flags & NORMAL_MASK) {
					case HAS_NO_NORMALS:
						break;
					case HAS_NORMALS_BYTE:
						normalSize = 3 * (Byte.SIZE / 8);
						normalElementType = GL.GL_BYTE;
						break;
					case HAS_NORMALS_FLOAT:
						normalSize = 3 * (Float.SIZE / 8);
						normalElementType = GL.GL_FLOAT;
						break;
					}

					int uvSize = 0;
					int uvElementType = 0;
					int uvElementCount = 0;
					switch (flags & TEXTURE_MASK) {
					case HAS_NO_TEXTURE_UV:
						break;
					case HAS_TEXTURE_UV_SHORT:
						uvSize = 2 * (Short.SIZE / 8);
						uvElementType = GL.GL_SHORT;
						uvElementCount = 2;
						break;
					case HAS_TEXTURE_UV_FLOAT:
						uvSize = 2 * (Float.SIZE / 8);
						uvElementType = GL.GL_FLOAT;
						uvElementCount = 2;
						break;
					}

					final int sizePerVertex = vertexSize + normalSize + uvSize;

					int vertexOffset = 0;
					int normalOffset = 0;
					int uvOffset = 0;
					switch (flags & ORDER_MASK) {
					case ORDER_V_N_UV:
						vertexOffset = 0;
						normalOffset = vertexSize;
						uvOffset = normalSize + normalOffset;
						break;
					case ORDER_V_UV_N:
						vertexOffset = 0;
						uvOffset = vertexSize;
						normalOffset = uvSize + uvOffset;
						break;
					case ORDER_N_V_UV:
						normalOffset = 0;
						vertexOffset = normalSize;
						uvOffset = vertexSize + vertexOffset;
						break;
					case ORDER_N_UV_V:
						normalOffset = 0;
						uvOffset = normalSize;
						vertexOffset = uvSize + uvOffset;
						break;
					case ORDER_UV_V_N:
						uvOffset = 0;
						vertexOffset = uvSize;
						normalOffset = vertexSize + vertexOffset;
						break;
					case ORDER_UV_N_V:
						uvOffset = 0;
						normalOffset = uvSize;
						vertexOffset = normalSize + normalOffset;
						break;
					}

					if (uvSize != 0) {
						// we have texture data interleaved in the array buffer
						setTextureAttributes(uvElementCount,uvElementType,uvOffset,sizePerVertex);
					}

					if (normalSize != 0) {
						// we have normal data interleaved in the array buffer
						setNormalAttributes(normalElementType,normalOffset,sizePerVertex);
					}

					if (vertexSize != 0) {
						// we have vertex data interleaved in the array buffer
						setVertexAttributes(vertexElementCount,vertexElementType,vertexOffset,sizePerVertex);
					}

					int elementMode = 0;
					switch (flags & PRIMITIVE_MASK) {
					case TRIANGLES:
						elementMode = GL.GL_TRIANGLES;
						break;
					case QUADS:
						elementMode = GL.GL_QUADS;
						break;
					case LINES:
						elementMode = GL.GL_LINES;
						break;
					}

					int elementType = 0;
					int elementSize = 0;
					switch (flags & ELEMENTS_MASK) {
					case ELEMENTS_BYTE:
						elementType = GL.GL_UNSIGNED_BYTE;
						elementSize = (Byte.SIZE / 8);
						break;
					case ELEMENTS_SHORT:
						elementType = GL.GL_UNSIGNED_SHORT;
						elementSize = (Short.SIZE / 8);
						break;
					case ELEMENTS_INT:
						elementType = GL.GL_UNSIGNED_INT;
						elementSize = (Integer.SIZE / 8);
						break;
					}
					
					currentGL.glDrawElements(elementMode,elementUsedCount,elementType,firstElementIndex * elementSize);

					returnValue = true;
					
					unbindArrayBuffer(arrayBufferIndex);
				}
				
				unbindElementArrayBuffer(elementArrayBufferIndex);
			}
		}

		return returnValue;
	}

	/**
	 * open an array buffer for writing
	 *
	 * @param arrayBufferIndex the index of the array buffer that should opened
	 * @param size the size in bytes that the buffer should contain
	 * @return <code>true</code> - buffer is opened for writing
	 * @see <code>createArrayBuffer()</code>
	 */
	final public boolean openArrayBuffer(int arrayBufferIndex,int size) {
		boolean returnValue = false;

		if ((arrayBufferIndex > 0) && (size > 0)) {
			if (bindArrayBuffer(arrayBufferIndex) == true) {
				currentGL.glBufferData(GL.GL_ARRAY_BUFFER,size,null,GL.GL_STATIC_DRAW);
				arrayBuffer = currentGL.glMapBuffer(GL.GL_ARRAY_BUFFER,GL.GL_WRITE_ONLY);
				
				openedArrayBufferIndex = arrayBufferIndex;
				freeArrayBufferCapacityRestBytes = size;
				returnValue = true;
			}
		}

		return returnValue;
	}

	/**
	 * open an element array buffer for writing
	 *
	 * @param elementArrayBufferIndex the index of the element array buffer that should opened
	 * @param size the size in bytes that the buffer should contain
	 * @return <code>true</code> - buffer is opened for writing
	 * @see <code>createElementArrayBuffer()</code>
	 */
	final public boolean openElementArrayBuffer(int elementArrayBufferIndex,int size) {
		boolean returnValue = false;

		if ((elementArrayBufferIndex > 0) && (size > 0)) {
			if (bindElementArrayBuffer(elementArrayBufferIndex) == true) {
				currentGL.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER,size,null,GL.GL_STATIC_DRAW);
				elementArrayBuffer = currentGL.glMapBuffer(GL.GL_ELEMENT_ARRAY_BUFFER,GL.GL_WRITE_ONLY);
				elementArrayBuffer.order(java.nio.ByteOrder.nativeOrder());
				
				openedElementArrayBufferIndex = elementArrayBufferIndex;
				freeElementArrayBufferCapacityRestBytes = size;
				returnValue = true;
			}
		}

		return returnValue;
	}

	/**
	 * close an array buffer, that was opened before with <code>openArrayBuffer()</code>
	 *
	 * @param arrayBufferIndex the index of the array buffer that should closed
	 */
	final public void closeArrayBuffer(int arrayBufferIndex) {
		if ((arrayBufferIndex > 0) && (openedArrayBufferIndex == arrayBufferIndex)) {
			currentGL.glUnmapBuffer(GL.GL_ARRAY_BUFFER);

			openedArrayBufferIndex = GL20Const.INVALID_ID;
			unbindArrayBuffer(arrayBufferIndex);
			arrayBuffer = null;
		}
	}


	/**
	 * close an element array buffer that was opened before with <code>openElementArrayBuffer()</code>
	 *
	 * @param elementArrayBufferIndex the index of the element array buffer that should closed
	 */
	final public void closeElementArrayBuffer(int elementArrayBufferIndex) {
		if ((elementArrayBufferIndex > 0) && (openedElementArrayBufferIndex == elementArrayBufferIndex)) {
			currentGL.glUnmapBuffer(GL.GL_ELEMENT_ARRAY_BUFFER);

			openedElementArrayBufferIndex = GL20Const.INVALID_ID;
			unbindElementArrayBuffer(elementArrayBufferIndex);
			elementArrayBuffer = null;
		}
	}
	
	/**
	 * create a new texture index
	 * 
	 * @param dimensions number of the dimensions that the texture should have
	 * @return the new texture index
	 */
	final public int createTextureIndex(int dimensions) {
		int[] returnValue = new int[1];
		returnValue[0] = 0;
		
		if ((currentGL != null) && 
			(dimensions > 0) &&
			(dimensions < 3)) {
			currentGL.glGenTextures(1, returnValue, 0);
		}
		
		return returnValue[0];
	}
	
	/**
	 * delete a texture index that was created via <code>createTextureIndex()</code>
	 * 
	 * @param dimensions number of the dimensions that the texture with <code>textureIndex</code> have
	 * @param textureIndex the texture index that should be deleted
	 */
	final public void deleteTextureIndex(int dimensions,int textureIndex) {
		if ((currentGL != null) && 
			(dimensions > 0) &&
			(dimensions < 3)) {
			// TODO kick out texture from texture stage if its bound currently
			
			int[] ids = new int[1];
			ids[0] = textureIndex;
			currentGL.glDeleteTextures(1,ids,0);
		}
	}
	
	/**
	 * 
	 * @param textureIndex
	 * @param width the width of the image in pixel. must be power of 2
	 * @param height the height of the image in pixel. must be power of 2
	 * @param depth the depth of the image in pixel. must be power of 2
	 * @param pixelData
	 * @return <code>0</code> image wasn't set
	 * otherwise the dimension of the image that was set
	 */
	final public int setTextureImage(int textureIndex,int width,int height,int depth,int[] pixelData) {
		int returnValue = 0;
		
		if ((GL20Const.isPowerOf2(width) == true) &&
			(GL20Const.isPowerOf2(height) == true) &&
			(GL20Const.isPowerOf2(depth) == true) &&
			(currentGL != null)) {
			int oldBoundTextureIndex = boundTextureIndex[0];
			int oldBoundTextureDimensions = boundTextureDimension[0];
			
			int target = 0;
			if (width > 0) {
				returnValue = 1;
				target = GL.GL_TEXTURE_1D;
				if (height > 1) {
					returnValue = 2;
					target = GL.GL_TEXTURE_2D;
					if (depth > 1) {
						returnValue = 3;
						target = GL.GL_TEXTURE_3D;
					}
				}
			}
			
			bindTextureIndex(returnValue,textureIndex,0);
			
			currentGL.glTexParameteri(target, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
			currentGL.glTexParameteri(target, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
			currentGL.glTexParameteri(target, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
			if (returnValue > 1) {
				currentGL.glTexParameteri(target, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
				if (returnValue > 2)
					currentGL.glTexParameteri(target, GL.GL_TEXTURE_WRAP_R, GL.GL_REPEAT);
			}
			
			IntBuffer pixelBuffer = IntBuffer.wrap(pixelData);
			switch (returnValue) {
			case 1:
				currentGL.glTexImage1D(target, 0, GL.GL_RGBA, width, 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, pixelBuffer);
				break;
			case 2:
				currentGL.glTexImage2D(target, 0, GL.GL_RGBA, width, height, 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, pixelBuffer);
				break;
			case 3:
				currentGL.glTexImage3D(target, 0, GL.GL_RGBA, width, height, depth, 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, pixelBuffer);
				break;
			}
			
			if (oldBoundTextureIndex != 0)
				bindTextureIndex(oldBoundTextureDimensions,oldBoundTextureIndex,0);
		}
		
		return returnValue;
	}
	
	/**
	 * bind a texture given by its <code>textureIndex</code> to a texture
	 * stage given by its <code>textureStage</code>.
	 * 
	 * @param dimensions number of dimensions of the texture with given <code>textureIndex</code>
	 * @param textureIndex a texture index that was return by <code>createTextureIndex</code> 
	 * @param textureStage the stage to which the texture should be bound
	 * @return <code>true</code> successful bound to given texture stage
	 * <code>false</code> <code>textureStage</code> and/or <code>textureIndex</code> are invalid
	 */
	final public boolean bindTextureIndex(int dimensions, int textureIndex,int textureStage) {
		boolean returnValue = false;
		
		if ((currentGL != null) && (textureIndex != 0) &&
			(textureStage < textureStagesAvailable) &&
			(dimensions > 0) && (dimensions < 3)) {
			// TODO check textureStage for valid stage
			
			int target = 0;
			switch (dimensions) {
			case 1:
				target = GL.GL_TEXTURE_1D;
				break;
			case 2:
				target = GL.GL_TEXTURE_2D;
				break;
			}
			
			currentGL.glActiveTexture(GL.GL_TEXTURE0 + textureStage);
			currentGL.glEnable(target);
			currentGL.glBindTexture(target, textureIndex);
			
			boundTextureIndex[textureStage] = textureIndex;
			boundTextureDimension[textureStage] = dimensions;
			
			returnValue = true;
		}
		
		return returnValue;
	}
	
	/**
	 * unbind a texture from a given <code>textureStage</code>
	 *  
	 * @param dimensions number of dimensions that the texture of given <code>textureStage</code> has
	 * @param textureStage the stage which should unbind
	 */
	final public void unbindTexture(int dimensions, int textureStage) {
		if ((currentGL != null) &&
			(textureStage < textureStagesAvailable) &&
			(dimensions == boundTextureDimension[textureStage])) {
			// TODO check textureStage for valid stage
			
			int target = 0;
			
			switch (dimensions) {
			case 1:
				target = GL.GL_TEXTURE_1D;
				break;
			case 2:
				target = GL.GL_TEXTURE_2D;
				break;
			}
			
			currentGL.glActiveTexture(GL.GL_TEXTURE0 + textureStage);
			currentGL.glBindTexture(target, 0);
			currentGL.glDisable(target);
			
			boundTextureIndex[textureStage] = 0;
			boundTextureDimension[textureStage] = 0;
		}
	}
	
	final public int createShader(GL20GLSLCode code) {
		if ((this.inRendering == true) && (this.inScene == false)) {
			String vertexShaderSource[] = new String[1];
			vertexShaderSource[0] = code.getShaderCodeVertex(false);
			String fragmentShaderSource[] = new String[1];
			fragmentShaderSource[0] = code.getShaderCodeFragment();

			if (programHandle != 0)
				currentGL.glDeleteProgram(programHandle);
			programHandle = currentGL.glCreateProgram();
			
			int vertexShaderHandle = currentGL.glCreateShader(GL.GL_VERTEX_SHADER);
			int fragmentShaderHandle = currentGL.glCreateShader(GL.GL_FRAGMENT_SHADER);
			
			int length[] = new int[1];
			
			// compile vertex shader
			length[0] = vertexShaderSource[0].length();
			currentGL.glShaderSource(vertexShaderHandle,1,vertexShaderSource,length,0);
			
			// compile fragment shader
			length[0] = fragmentShaderSource[0].length();
			currentGL.glShaderSource(fragmentShaderHandle,1, fragmentShaderSource,length,0);
			
			currentGL.glCompileShader(vertexShaderHandle);
			if (checkGLObjectInfoLog(vertexShaderHandle,"VertexShader") == true) {
				currentGL.glCompileShader(fragmentShaderHandle);
				if (checkGLObjectInfoLog(fragmentShaderHandle,"FragmentShader") == true) {
					currentGL.glAttachShader(programHandle,vertexShaderHandle);
					currentGL.glAttachShader(programHandle,fragmentShaderHandle);
					currentGL.glDeleteShader(vertexShaderHandle);
					currentGL.glDeleteShader(fragmentShaderHandle);
					currentGL.glLinkProgram(programHandle);
					if (checkGLObjectInfoLog(programHandle,"Program") == true)
						System.out.println("YES!!!");
				}
			}
		}
		
		return 0;
	}
	
	/**
	 * draw a single line
	 * should only called for very few reasons, e.g. like lines for tools
	 * 
	 * @param start the <code>start</code> coordinate of the line
	 * @param end the <code>end</code> coordinate of the line
	 */
	final public void drawLine(Tuple3f start,Tuple3f end) {
		if (inRendering == true) {
			currentGL.glBegin(GL.GL_LINES);
			currentGL.glVertex3f(start.x, start.y, start.z);
			currentGL.glVertex3f(end.x, end.y, end.z);
			currentGL.glEnd();
		}
	}

	final public int writeArrayBuffer2f(float x,float y) {
		int returnValue = 0;

		if ((arrayBuffer != null) && (freeArrayBufferCapacityRestBytes >= 2 * (Float.SIZE / 8))) {
			arrayBuffer = arrayBuffer.putFloat(x);
			arrayBuffer = arrayBuffer.putFloat(y);

			freeArrayBufferCapacityRestBytes -= 2 * (Float.SIZE / 8);
			returnValue = 2;
		}

		return returnValue;
	}

	final public int writeArrayBuffer3f(float x,float y,float z) {
		int returnValue = 0;

		if ((arrayBuffer != null) && (freeArrayBufferCapacityRestBytes >= 3 * (Float.SIZE / 8))) {
			arrayBuffer = arrayBuffer.putFloat(x);
			arrayBuffer = arrayBuffer.putFloat(y);
			arrayBuffer = arrayBuffer.putFloat(z);
			
			freeArrayBufferCapacityRestBytes -= 3 * (Float.SIZE / 8);
			returnValue = 3;
		}

		return returnValue;
	}

	final public int writeArrayBuffer4f(float x,float y,float z,float w) {
		int returnValue = 0;

		if ((arrayBuffer != null) && (freeArrayBufferCapacityRestBytes >= 4 * (Float.SIZE / 8))) {
			arrayBuffer.putFloat(x);
			arrayBuffer.putFloat(y);
			arrayBuffer.putFloat(z);
			arrayBuffer.putFloat(w);

			freeArrayBufferCapacityRestBytes -= 4 * (Float.SIZE / 8);
			returnValue = 4;
		}

		return returnValue;
	}

	final public int writeArrayBufferfv(float[] v,int count) {
		int returnValue = 0;

		if ((arrayBuffer != null) && (freeArrayBufferCapacityRestBytes >= count * (Float.SIZE / 8))) {
			for (int i=0;i < count;i++)
				arrayBuffer.putFloat(v[i]);
			
			freeArrayBufferCapacityRestBytes -= count * (Float.SIZE / 8);
			returnValue = count;
		}

		return returnValue;
	}

	final public int writeArrayBuffer2i(int a,int b) {
		int returnValue = 0;

		if ((arrayBuffer != null) && (freeArrayBufferCapacityRestBytes >= 2 * (Integer.SIZE / 8))) {
			arrayBuffer.putInt(a);
			arrayBuffer.putInt(b);

			freeArrayBufferCapacityRestBytes -= 2 * (Integer.SIZE / 8);
			returnValue = 2;
		}

		return returnValue;
	}

	final public int writeArrayBuffer3i(int a,int b,int c) {
		int returnValue = 0;

		if ((arrayBuffer != null) && (freeArrayBufferCapacityRestBytes >= 3 * (Integer.SIZE / 8))) {
			arrayBuffer.putInt(a);
			arrayBuffer.putInt(b);
			arrayBuffer.putInt(c);

			freeArrayBufferCapacityRestBytes -= 3 * (Integer.SIZE / 8);
			returnValue = 3;
		}

		return returnValue;
	}

	final public int writeArrayBuffer4i(int a,int b,int c,int d) {
		int returnValue = 0;

		if ((arrayBuffer != null) && (freeArrayBufferCapacityRestBytes >= 4 * (Integer.SIZE / 8))) {
			arrayBuffer.putInt(a);
			arrayBuffer.putInt(b);
			arrayBuffer.putInt(c);
			arrayBuffer.putInt(d);

			freeArrayBufferCapacityRestBytes -= 4 * (Integer.SIZE / 8);
			returnValue = 4;
		}

		return returnValue;
	}

	final public int writeArrayBufferiv(int[] v,int count) {
		int returnValue = 0;

		if ((arrayBuffer != null) && (freeArrayBufferCapacityRestBytes >= count * (Integer.SIZE / 8))) {
			for (int i=0;i < count;i++)
				arrayBuffer.putInt(v[i]);

			freeArrayBufferCapacityRestBytes -= count * (Integer.SIZE / 8);
			returnValue = count;
		}

		return returnValue;
	}

	final public int writeArrayBuffer2b(byte a,byte b) {
		int returnValue = 0;

		if ((arrayBuffer != null) && (freeArrayBufferCapacityRestBytes >= 2 * (Byte.SIZE / 8))) {
			arrayBuffer.put(a);
			arrayBuffer.put(b);

			freeArrayBufferCapacityRestBytes -= 2 * (Byte.SIZE / 8);
			returnValue = 2;
		}

		return returnValue;
	}

	final public int writeArrayBuffer3b(byte a,byte b,byte c) {
		int returnValue = 0;

		if ((arrayBuffer != null) && (freeArrayBufferCapacityRestBytes >= 3 * (Byte.SIZE / 8))) {
			arrayBuffer.put(a);
			arrayBuffer.put(b);
			arrayBuffer.put(c);

			freeArrayBufferCapacityRestBytes -= 3 * (Byte.SIZE / 8);
			returnValue = 3;
		}

		return returnValue;
	}

	final public int writeArrayBuffer4b(byte a,byte b,byte c,byte d) {
		int returnValue = 0;

		if ((arrayBuffer != null) && (freeArrayBufferCapacityRestBytes >= 4 * (Byte.SIZE / 8))) {
			arrayBuffer.put(a);
			arrayBuffer.put(b);
			arrayBuffer.put(c);
			arrayBuffer.put(d);

			freeArrayBufferCapacityRestBytes -= 4 * (Byte.SIZE / 8);
			returnValue = 4;
		}

		return returnValue;
	}

	final public int writeArrayBufferbv(byte[] v,int count) {
		int returnValue = 0;

		if ((arrayBuffer != null) && (freeArrayBufferCapacityRestBytes >= count * (Byte.SIZE / 8))) {
			arrayBuffer.put(v,0,count);

			freeArrayBufferCapacityRestBytes -= count * (Byte.SIZE / 8);
			returnValue = count;
		}

		return returnValue;
	}

	final public int writeElementArrayBuffer2f(float x,float y) {
		int returnValue = 0;

		if ((elementArrayBuffer != null) && (freeElementArrayBufferCapacityRestBytes >= 2 * (Float.SIZE / 8))) {
			elementArrayBuffer.putFloat(x);
			elementArrayBuffer.putFloat(y);

			freeElementArrayBufferCapacityRestBytes -= 2 * (Float.SIZE / 8);
			returnValue = 2;
		}

		return returnValue;
	}

	final public int writeElementArrayBuffer3f(float x,float y,float z) {
		int returnValue = 0;

		if ((elementArrayBuffer != null) && (freeElementArrayBufferCapacityRestBytes >= 3 * (Float.SIZE / 8))) {
			elementArrayBuffer.putFloat(x);
			elementArrayBuffer.putFloat(y);
			elementArrayBuffer.putFloat(z);

			freeElementArrayBufferCapacityRestBytes -= 3 * (Float.SIZE / 8);
			returnValue = 3;
		}

		return returnValue;
	}

	final public int writeElementArrayBuffer4f(float x,float y,float z,float w) {
		int returnValue = 0;

		if ((elementArrayBuffer != null) && (freeElementArrayBufferCapacityRestBytes >= 4 * (Float.SIZE / 8))) {
			elementArrayBuffer.putFloat(x);
			elementArrayBuffer.putFloat(y);
			elementArrayBuffer.putFloat(z);
			elementArrayBuffer.putFloat(w);

			freeElementArrayBufferCapacityRestBytes -= 4 * (Float.SIZE / 8);
			returnValue = 4;
		}

		return returnValue;
	}

	final public int writeElementArrayBufferfv(float[] v,int count) {
		int returnValue = 0;

		if ((elementArrayBuffer != null) && (freeElementArrayBufferCapacityRestBytes >= count * (Float.SIZE / 8))) {
			for (int i=0;i < count;i++)
				elementArrayBuffer.putFloat(v[i]);

			freeElementArrayBufferCapacityRestBytes -= count * (Float.SIZE / 8);
			returnValue = count;
		}

		return returnValue;
	}

	final public int writeElementArrayBuffer2i(int a,int b) {
		int returnValue = 0;

		if ((elementArrayBuffer != null) && (freeElementArrayBufferCapacityRestBytes >= 2 * (Integer.SIZE / 8))) {
			elementArrayBuffer.putInt(a);
			elementArrayBuffer.putInt(b);

			freeElementArrayBufferCapacityRestBytes -= 2 * (Integer.SIZE / 8);
			returnValue = 2;
		}

		return returnValue;
	}

	final public int writeElementArrayBuffer3i(int a,int b,int c) {
		int returnValue = 0;

		if ((elementArrayBuffer != null) && (freeElementArrayBufferCapacityRestBytes >= 3 * (Integer.SIZE / 8))) {
			elementArrayBuffer.putInt(a);
			elementArrayBuffer.putInt(b);
			elementArrayBuffer.putInt(c);

			freeElementArrayBufferCapacityRestBytes -= 3 * (Integer.SIZE / 8);
			returnValue = 3;
		}

		return returnValue;
	}

	final public int writeElementArrayBuffer4i(int a,int b,int c,int d) {
		int returnValue = 0;

		if ((elementArrayBuffer != null) && (freeElementArrayBufferCapacityRestBytes >= 4 * (Integer.SIZE / 8))) {
			elementArrayBuffer.putInt(a);
			elementArrayBuffer.putInt(b);
			elementArrayBuffer.putInt(c);
			elementArrayBuffer.putInt(d);

			freeElementArrayBufferCapacityRestBytes -= 4 * (Integer.SIZE / 8);
			returnValue = 4;
		}

		return returnValue;
	}

	final public int writeElementArrayBufferiv(int[] v,int count) {
		int returnValue = 0;

		if ((elementArrayBuffer != null) && (freeElementArrayBufferCapacityRestBytes >= count * (Integer.SIZE / 8))) {
			for (int i=0;i < count;i++)
				elementArrayBuffer.putInt(v[i]);

			freeElementArrayBufferCapacityRestBytes -= count * (Integer.SIZE / 8);
			returnValue = count;
		}

		return returnValue;
	}

	final public int writeElementArrayBuffer2b(byte a,byte b) {
		int returnValue = 0;

		if ((elementArrayBuffer != null) && (freeElementArrayBufferCapacityRestBytes >= 2 * (Byte.SIZE / 8))) {
			elementArrayBuffer.put(a);
			elementArrayBuffer.put(b);

			freeElementArrayBufferCapacityRestBytes -= 2 * (Byte.SIZE / 8);
			returnValue = 2;
		}

		return returnValue;
	}

	final public int writeElementArrayBuffer3b(byte a,byte b,byte c) {
		int returnValue = 0;

		if ((elementArrayBuffer != null) && (freeElementArrayBufferCapacityRestBytes >= 3 * (Byte.SIZE / 8))) {
			elementArrayBuffer.put(a);
			elementArrayBuffer.put(b);
			elementArrayBuffer.put(c);

			freeElementArrayBufferCapacityRestBytes -= 3 * (Byte.SIZE / 8);
			returnValue = 3;
		}

		return returnValue;
	}

	final public int writeElementArrayBuffer4b(byte a,byte b,byte c,byte d) {
		int returnValue = 0;

		if ((elementArrayBuffer != null) && (freeElementArrayBufferCapacityRestBytes >= 4 * (Byte.SIZE / 8))) {
			elementArrayBuffer.put(a);
			elementArrayBuffer.put(b);
			elementArrayBuffer.put(c);
			elementArrayBuffer.put(d);

			freeElementArrayBufferCapacityRestBytes -= 4 * (Byte.SIZE / 8);
			returnValue = 4;
		}

		return returnValue;
	}

	final public int writeElementArrayBufferbv(byte[] v,int count) {
		int returnValue = 0;

		if ((elementArrayBuffer != null) && (freeElementArrayBufferCapacityRestBytes >= count * (Byte.SIZE / 8))) {
			elementArrayBuffer.put(v,0,count);

			freeElementArrayBufferCapacityRestBytes -= count * (Byte.SIZE / 8);
			returnValue = count;
		}

		return returnValue;
	}
}