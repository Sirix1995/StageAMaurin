package de.grogra.ext.sunshine;

import static javax.media.opengl.GL.GL_COLOR_ATTACHMENT0_EXT;
import static javax.media.opengl.GL.GL_COLOR_ATTACHMENT1_EXT;
import static javax.media.opengl.GL.GL_COLOR_ATTACHMENT2_EXT;
import static javax.media.opengl.GL.GL_COLOR_ATTACHMENT3_EXT;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DRAW_BUFFER;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_COMPLETE_EXT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_EXT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT_EXT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_UNSUPPORTED_EXT;
import static javax.media.opengl.GL.GL_PROJECTION;
import static javax.media.opengl.GL.GL_QUADS;
import static javax.media.opengl.GL.GL_RGBA;
import static javax.media.opengl.GL.GL_SMOOTH;
import static javax.media.opengl.GL.GL_TEXTURE5;
import static javax.media.opengl.GL.GL_TEXTURE_RECTANGLE_ARB;

import java.io.FileInputStream;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLPbuffer;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.StreamUtil;

import de.grogra.ext.sunshine.kernel.*;
import de.grogra.ext.sunshine.kdTree.*;
import de.grogra.imp3d.gl.TextureManager;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Resources;

/**
 * this class is the framework for the fragment shader
 * @author Thomas
 *
 */
public class SunshineRaytracer implements GLEventListener
{	

	// Data declaration
	private boolean renderReady = false;
	
	private GLU glu;
	
	// the two framebuffer object
	private int[] fbo = new int[2];
	private int FBO_A = 1;
	private int FBO_B = 0;
	private int FBO = 1;
	
	
	/**
	 * the textures for the ray origin and ray direction
	 */
	private int[] textureB = new int[4];
	private int[] textureA = new int[4];


	
	/**
	 * the scene texture
	 */
	private int[] sceneTexture = new int[1];
	
	/**
	 * the texture atlas
	 */
	private int[] texTexture = new int[1];
	
	/**
	 * the kd tree
	 */
	private int[] kdTexture = new int[1];
	
	/**
	 * GL_COLOR_ATTACHMENT0_EXT;
	 * GL_COLOR_ATTACHMENT1_EXT;
	 */
	private int[] attachmentpoints = new int[4];

	/**
	 * GL_TEXTURE_RECTANGLE_ARB
	 */
	private int texTarget = GL_TEXTURE_RECTANGLE_ARB;
	
	
	// the texture handler
	private ObjectHandler oh;
	
	
	/**
	 * manage textures stored in OpenGL server side memory
	 */
	final TextureManager textureManager = new TextureManager ();

	
	// declaration of the used kernel
	private Kernel initKernel;
	private Kernel genRayKernel;
	private Kernel traceKernel;
	protected MyKernel drawKernel;
	
	private KdTree tree; 
	
	
	// uniform variables
	protected int drawLoc;	

	protected int texWidth;
	protected int texHeight;
	
	protected int partsHeight;
	protected int partsWidth;
	
	// the offscreen render buffer
	private GLPbuffer pBuffer;
	
	private int tileWidth;
	private int tileHeight;
	private int superSample = 4;
	private int recursionDeep;
	private int grid = 3;
	private int sceneSize;
	private int textureSizeX;
	private int textureSizeY;
	private int[] objects;
	int x;
	int y;
	private ProgressMonitor monitor;
	private boolean pt; //pathtracer
	private boolean accelerate;
	
	public SunshineRaytracer() {}
	
	
	public SunshineRaytracer(ObjectHandler oh, int gridSize, int recDeep, 
			int width, int height, int sceneSize, int[] objects,
			ProgressMonitor progress, boolean pt, boolean ot) 
	{
		tree 			= new KdTree(100, ((SunshineSceneVisitor)progress).getOctree());
		monitor 		= progress;
		this.oh 		= oh;
		texWidth 		= width;
		texHeight 		= height;
		superSample		= gridSize*gridSize;
		grid			= gridSize;
		recursionDeep	= recDeep;
		this.sceneSize 	= sceneSize;
		this.pt 		= pt;
		accelerate		= ot;
		
		
		int counter = oh.getImageCount();
		x = counter;
		y = 1;
		
		// calculates the size of the texture atlas
		if(counter > 8)
		{
			y = counter % 8 == 0 ? counter / 8 : counter / 8 + 1;
			x = (int)Math.ceil(  (float)counter / (float)y );	
		}
		
		textureSizeX = x*512;
		textureSizeY = y*512;
		
		this.objects = objects;
		
		GLCapabilities glcaps = new GLCapabilities();
		
		
		// check if pbuffer support is available
		if( GLDrawableFactory.getFactory().canCreateGLPbuffer() ) 
		{
			pBuffer = GLDrawableFactory.getFactory().createGLPbuffer(glcaps, 
					null, texWidth, texHeight, null);
			
			pBuffer.addGLEventListener(this);
		} else
		{
			System.out.println("The graphic card has no pbuffer support.");
		}
			
	} //Constructor
	
	
	public void init(GLAutoDrawable drawable)
	{ 
		// the size of the tiles in which the picture is divided
		tileWidth 	= 256;
		tileHeight 	= 256;
		GL gl 		= drawable.getGL();
		glu 		= new GLU();
		
		// the color attachments for the framebuffer objects
		attachmentpoints[0] = GL_COLOR_ATTACHMENT0_EXT;
		attachmentpoints[1] = GL_COLOR_ATTACHMENT1_EXT;
		attachmentpoints[2] = GL_COLOR_ATTACHMENT2_EXT;
		attachmentpoints[3] = GL_COLOR_ATTACHMENT3_EXT;


		gl.glShadeModel(GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // clear the framebuffer
		gl.glViewport(0, 0, texWidth, texHeight); // set the size of the the viewport

		// Setup the FBOs
		gl.glGenFramebuffersEXT(2, fbo, 0);

		// generate the texture attachments
		oh.generateTexture(drawable, tileWidth, tileHeight, 4, textureB);
		oh.generateTexture(drawable, tileWidth, tileHeight, 4, textureA);		
		
		// generate the scene texture
		oh.generateTexture(drawable, sceneSize, sceneSize, 1, sceneTexture);
		// fill the texture with content, get by the texture handler
		transferToTexture(drawable, sceneSize, sceneSize, oh.getSceneTex());
		
		// generate the texture atlas
		oh.generateTexture(drawable, textureSizeX, textureSizeY, 1, texTexture);
		
		// fill the texture atlas with images, get by the texture handler
		if( oh.hasImages() )
		{
			for (int i = 0; i < oh.getImageCount(); i++)
			{
				gl.glTexSubImage2D(texTarget, 0, (i%x)*512, (i/x)*512, 512, 512, 
						GL.GL_BGRA,	GL.GL_UNSIGNED_BYTE, oh.getPixels(i) );
				
			} //for
		}
		
		// generate the kd tree texture
		oh.generateTexture(drawable, 100, 100, 1, kdTexture);
		
		
		// fill the texture with the tree cells
		transferToTexture(drawable, tree.getSize(), tree.getSize(), tree.getTreeTex());
		
		
		// calculate the size of the odd tiles
		partsWidth	= getParts(texWidth, tileWidth);
		partsHeight = getParts(texHeight, tileHeight);
		
		// create the kernels
		createKernels(drawable);
	} // init
	
		
	public void display(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL(); 
		
		// store the window viewport dimensions
		int[] vp = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, vp, 0);
		
		// attach texture B to the framebuffer object
		attachTextureToFBO(drawable, fbo[0], textureB);
		checkFBO(drawable);
		// attach texture A to the framebuffer object
		attachTextureToFBO(drawable, fbo[1], textureA);
		checkFBO(drawable);

		// render the odd tiles
		for(int px = 0; px < partsWidth; px++)
		{
			for(int py = 0; py < partsHeight; py++)
			{
				// set the viewport to the dimensions of the texture
				gl.glViewport( 0, 0, tileWidth, tileHeight);
				
				renderToTexture(drawable, FBO_A);
				// initialise texture A
				initKernel.execute(drawable, px, py, 0, textureA);

				
				//anti aliasing
				for(int i = 0; i < superSample; i++)
				{
					renderToTexture(drawable, FBO_B);
					genRayKernel.execute(drawable, px, py, i, textureA);
					
					
					renderToTexture(drawable, FBO_A);
					traceKernel.execute(drawable, px, py, i, textureB);
					
					
					// recursive raytracing
					for(int k = 0; k < recursionDeep; k++)
					{						
						renderToTexture(drawable, k%2);
						traceKernel.execute(drawable, px, py, i, k%2 == FBO_B ? textureA : textureB );
						swap(); //1,0,1
					} //for recursiv				
					
				} //for superSample
				
				
//				if (monitor != null)
//				{
//					monitor.setProgress (Resources.msg ("renderer.rendering",
//						new Float (step/partsWidth*partsHeight)), step/partsWidth*partsHeight);
//				}
//				step++;
				
				// draw the rendered tile to the pBuffer
				draw(drawable, vp, tileWidth, tileHeight, px, py);
				
			} //for py
		} //for px
		
		
		// get the complete image
		readFromPbuffer(drawable, texWidth, texHeight);
		
		
		
		// call the destructors
		shutDown(drawable);
		setRenderReady(true);

	} //display
	
	
	private void swap()
	{
		FBO = FBO^1;
		
		
	}
	
	// start the render pass
	public void startRender()
	{
		// status report
		if (monitor != null)
		{
			monitor.setProgress (Resources.msg ("renderer.rendering",
					new Float (0)), 0);
		}
		
		// call the display method 
		pBuffer.display();
		GL gl = pBuffer.getContext().getGL();
		
		gl.glFinish();
		pBuffer.getContext().destroy();

		if (monitor != null)
		{
			monitor.setProgress (Resources.msg ("renderer.done"),
					ProgressMonitor.DONE_PROGRESS);
		}
	} //starRender
	
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height)
	{
		GL gl = drawable.getGL();

		if (height == 0)
			height = 1;

		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
	} // reshape
	

	
	public void setRenderReady(boolean renderReady) {
		this.renderReady = renderReady;
	} //setRenderReady

	public boolean isRenderReady()
	{
		return renderReady;
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
	{
		
	} // displayChanged
	
	
	/**
	 * divide the texture in #parts  
	 * @param texSize 
	 * @param div the size of the part
	 * @return the count of the parts
	 */
	protected int getParts(int texSize, int div)
	{
		return texSize % div != 0 ? (texSize / div) + 1 : texSize / div;
	}
	
	
	/**
	 * attach the color attachments to the given FBO
	 * @param drawable
	 * @param fb
	 */
	private void renderToTexture(GLAutoDrawable drawable, int fb)
	{
		GL gl = drawable.getGL();
		
		// bind the FBO to render to it
		gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbo[fb]);
		// set render destination
		gl.glDrawBuffers(4, attachmentpoints, 0);
		
		// Then render as normal
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		// Clear Screen and Depth Buffer
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 
		gl.glLoadIdentity();
		
	} //renderToTexture

	
	// tidy up
	private void shutDown(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		
//		checkError(drawable);
		
		gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		
		gl.glDeleteTextures(4, textureA, 0);
		gl.glDeleteTextures(4, textureB, 0);
		gl.glDeleteTextures(1, sceneTexture, 0);
		gl.glDeleteTextures(1, texTexture, 0);
		
		gl.glDeleteFramebuffersEXT(2, fbo, 0);
	} //shutDown
	
	
	/**
	 * attach the given texture to the given FBO
	 * 
	 * @param drawable
	 * @param fbo
	 * @param attachment
	 */
	private void attachTextureToFBO(GLAutoDrawable drawable, int fbo, int[] attachment)
	{
		GL gl = drawable.getGL();
		
		gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbo);
		
		for(int i = 0; i < 4; i++)
		{
			// attach attachment0-attachment3 to the given FBO so I can render to them
			gl.glFramebufferTexture2DEXT
			(
				GL_FRAMEBUFFER_EXT, 
				attachmentpoints[i], 
				texTarget, attachment[i], 0
			);
		} //for
	} //attachTextureToFBOS
	
	
	/**
	 * read the pixels from the pBuffer to the result buffer
	 * @param drawable
	 * @param width
	 * @param height
	 */
	private void readFromPbuffer(GLAutoDrawable drawable, int width, int height)
	{
		GL gl = drawable.getGL();
		
		gl.glFinish();
		gl.glFlush();
		
		int[] buffer = new int[1];
		// get the right drawbuffer to read from
		gl.glGetIntegerv(GL_DRAW_BUFFER, buffer, 0);
		gl.glReadBuffer(buffer[0]);
		
		oh.getResult().rewind();
		gl.glReadPixels(0, 0, width, height, GL_RGBA, GL_FLOAT, oh.getResult());
		oh.getResult().rewind();
	} //readFromPbuffer

	
	// error checking
	private void checkFBO(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		int status = gl.glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT);
		if (status != GL_FRAMEBUFFER_COMPLETE_EXT)
		{
			System.out.println("Framebuffer initialization error! " + status);
//			shutDown(drawable);
		} // if
		
	
//		if (status == GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENTS_EXT)
//			System.out.println("INCOMPLETE_ATTACHMENT! " + status);
		
		if (status == GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT)
			System.out.println("INCOMPLETE_MISSING_ATTACHMENT! " + status);
		
		if (status == GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT_EXT) 
			System.out.println("INCOMPLETE_DUPLICATE_ATTACHMENT! " + status);
		
		if (status == GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT)
			System.out.println("INCOMPLETE_DIMENSIONS_EXT! " + status);
		
		if (status == GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT)
			System.out.println("INCOMPLETE_FORMATS_EXT! " + status);
		
		if (status == GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT)
			System.out.println("INCOMPLETE_DRAW_BUFFER_EXT! " + status);
		
		if (status == GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT) 
			System.out.println("INCOMPLETE_READ_BUFFER_EXT! " + status);
		
		if (status == GL_FRAMEBUFFER_UNSUPPORTED_EXT)
			System.out.println("FRAMEBUFFER_UNSUPPORTED! " + status);
		
//		if (status == GL_FRAMEBUFFER_STATUS_ERROR_EXT)
//			System.out.println("FRAMEBUFFER_STATUS_ERROR! " + status);
	} //checkFBO


	// create the kernels
	private void createKernels(GLAutoDrawable drawable)	
	{
		// Create the fragment programs
		initKernel		= new InitKernel("initKernel", drawable, tileWidth, tileHeight);
		genRayKernel	= new GenRayKernel("genRayKernel", drawable, tileWidth, tileHeight);
		traceKernel		= new TraceRayKernel("traceRayKernel", drawable, tileWidth, tileHeight, sceneTexture, texTexture, kdTexture, oh.hasImages());
		drawKernel		= new MyKernel("drawKernel", drawable, false);
				
		
		initKernel.setSource(drawable, new String[]
		{
				loadSource("../Sunshine/Shader/extension.frag"),
				loadSource("../Sunshine/Shader/random.fs"),
				loadSource("../Sunshine/Shader/init.frag") } 
		);
		
			
		
		// set the soucre for the ray generation
		// some values are immediates
		genRayKernel.setSource(drawable, new String[]
		{
				loadSource("../Sunshine/Shader/extension.frag"),
				"vec3 rayOrigin 	= vec3("+oh.getCamPosString()+");", // the camera parameter
				"vec3 up 			= vec3("+oh.getUpString()+");",
				"vec3 right 		= vec3("+oh.getRightString()+");",
				"vec3 dir 			= vec3("+oh.getDirString()+");",
				"int partsHeight 	= " + partsHeight +";",
				"int partsWidth 	= " + partsWidth +";",
				"int texWidth 		= " + texWidth + ";",
				"int texHeight 		= " + texHeight + ";",
				"int gridSize 		= " + grid + ";",
				loadSource("../Sunshine/Shader/random.fs"),
				loadSource("../Sunshine/Shader/genRaySource.frag")} 
		);
		

		// the source for the tracing kernel
		traceKernel.setSource(drawable, new String[] 
		{			
				loadSource("../Sunshine/Shader/extension.frag"),
				loadSource("../Sunshine/Shader/samplers.frag"),
				loadSource("../Sunshine/Shader/random.fs"),
				"\n const int sphereCount 	= " + objects[0] + ";\n",
				"const int boxCount			= " + objects[1] + ";\n",
				"const int cylinderCount	= " + objects[2] + ";\n",
				"const int planeCount 		= " + objects[3] + ";\n",
				"const int paraCount 		= " + objects[4] + ";\n",
				"const int lightCount 		= " + objects[5] + ";\n",
				"const int size 			= " + sceneSize +";\n",
				"const float superSample 	= " + superSample+".0;\n",
				"const int lightPart		= " + oh.getLightPart() +";\n",
				"bool pt					= " + pt +";\n",
				"bool accelerate			= " + accelerate +";\n",
				loadSource("../Sunshine/Shader/structs.frag"),
				loadSource("../Sunshine/Shader/getObjects.frag"),
				loadSource("../Sunshine/Shader/normals.frag"),
				loadSource("../Sunshine/Shader/intersections.frag"),
				((SunshineSceneVisitor)monitor).getPhong(),
				loadSource("../Sunshine/Shader/intersectionLoop.frag"),
				loadSource("../Sunshine/Shader/treeTraversal.frag"),
				loadSource("../Sunshine/Shader/sunny.frag") }
		);
		
		drawKernel.setSource(drawable, new String[] {loadSource("../Sunshine/Shader/draw.frag") } );
		
		
		// compile the kernels
		initKernel.compile(drawable);
		genRayKernel.compile(drawable);
		traceKernel.compile(drawable);
		drawKernel.compile(drawable);
	
		// Link the shader into a complete GLSL program.
		initKernel.LinkProgram(drawable);
		genRayKernel.LinkProgram(drawable);
		traceKernel.LinkProgram(drawable);
		drawKernel.LinkProgram(drawable);
		
		// get the uniform location for a texture
		drawLoc		= drawKernel.getUniformLocation("draw", drawable);
	} //createKernels

	
	// load a String out of the file given by the path s
	private String loadSource(String s)
	{	
		String source = null;
		try {
			source = new String(StreamUtil.readAll(new FileInputStream(s)));			
		} catch (Exception e)
		{
			System.out.println(e);			
		}		
		return source;
	} //loadShader
	
	
	/**
	 * draw a textured tile
	 * @param drawable
	 * @param width
	 * @param height
	 * @param px
	 * @param py
	 */
	private void drawTile(GLAutoDrawable drawable, int width, int height, int px, int py)
	{
		GL gl = drawable.getGL();
		
		// calculate the start psoition of the tile
		int w = Math.min(width, texWidth - px*width);
		int h = Math.min(height, texHeight - py*height);
		
		// draw the textured tile
		gl.glBegin(GL_QUADS);
			gl.glTexCoord2f(0, 0); 	gl.glVertex3i(width*px , height*py, 0);
			gl.glTexCoord2f(w, 0);	gl.glVertex3i(width*px + w, height*py, 0);
			gl.glTexCoord2f(w, h);	gl.glVertex3i(width*px + w, height*py + h, 0);
			gl.glTexCoord2f(0, h);	gl.glVertex3i(width*px, height*py + h, 0);
		gl.glEnd();
	} //drawTile


	private void draw(GLAutoDrawable drawable, int[] vp, int width, int height, int px, int py)
	{
		GL gl = drawable.getGL();
		
		// restore the stored viewport dimensions and set rendering back to default frame buffer
		gl.glViewport(vp[0], vp[1], vp[2], vp[3]);
		gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		gl.glMatrixMode (GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(0, texWidth, 0, texHeight);

		
		// activate the draw shader
		drawKernel.useProgram(drawable);		
			gl.glActiveTexture(GL_TEXTURE5);
			gl.glBindTexture(texTarget, textureA[0]);
			gl.glEnable(texTarget);
			gl.glUniform1iARB(drawLoc, 5);	
					
			drawTile(drawable, width, height, px, py);
		drawKernel.stopProgram(drawable);
		
		
		gl.glDisable(texTarget);
	} //draw

	
	// store the given bytebuffer into the active texture
	private void transferToTexture(GLAutoDrawable drawable,int width, int height, ByteBuffer data)
	{
		GL gl = drawable.getGL();
		data.rewind();
		gl.glTexSubImage2D(texTarget, 0, 0, 0, width, height, GL.GL_RGBA, GL.GL_FLOAT, data);
		data.rewind();
	} //transferToTexture
} // Class

