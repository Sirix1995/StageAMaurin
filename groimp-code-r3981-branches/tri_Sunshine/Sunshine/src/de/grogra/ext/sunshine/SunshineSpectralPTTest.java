package de.grogra.ext.sunshine;

import java.awt.EventQueue;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import java.nio.ByteBuffer;
import com.sun.opengl.util.BufferUtil;

import de.grogra.ext.sunshine.kernel.Kernel;
import de.grogra.ext.sunshine.kernel.Spectral2ColorConversionKernel;
import de.grogra.ext.sunshine.output.Image;

public class SunshineSpectralPTTest extends SunshinePathtracer {

	private Kernel test;
	
	private ByteBuffer colorMatch;
	private int[] colorMatchTex = new int[1];
	
	static final int STATE_TEST = 13;
	
	
	
	@Override
	void createKernels(GLAutoDrawable drawable) {
		
		test = new Spectral2ColorConversionKernel("SpectralTest", drawable, colorMatchTex);
		
		test.setDebug();
		
		test.setSource(drawable, new String[] {
			loadSource("extension.frag"),	
			"int imageWidth = " + imageWidth + ";\n",
			"int tileWidth = " + TILE_WIDTH + ";\n",
			loadSource("/spectral/spectralTestWalker.frag")	
		});
		
		
		armKernel(drawable, test);
		
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		
		outputMode = Image.OUTPUT_RAW;
		
		initColorMatch();
	}
	
	private void initColorMatch()
	{
		int numCol	= (int) Math.ceil( Math.sqrt( SPDConversion.NUM_OF_XYZ_ELEMENTS ) / 3d ) * 3;	
		int numRow	= (int) Math.ceil( SPDConversion.NUM_OF_XYZ_ELEMENTS / (3d * numCol));
		
		colorMatch 	= BufferUtil.newByteBuffer(numCol * numRow * ObjectHandler.RGB) ;
		
		SPDConversion.getScaledXYZMatchFunction(1.0f, colorMatch);
		
		// generate the scene texture
		generateTexture(drawable, numCol, numRow, 1, colorMatchTex, GL.GL_RGB);
		
		// fill the texture with content, get by the object handler
		transferToTexture(drawable, numCol, numRow, colorMatch, GL.GL_RGB);
		
		checkSentData(drawable, colorMatch.limit(), true, GL.GL_RGB);
	}
	
	@Override
	public void processNextKernel() {
		
		// obtain GL instance from the drawable
		GL gl = drawable.getGL();	
		
		// setting params for actual tile which has to be draw
		int drawXTile = currentTileX;
		int drawYTile = currentTileY;
		
		switch(state) 
		{
			case STATE_STARTUP: 
			{
				// store the window viewport dimensions
				gl.glGetIntegerv(GL.GL_VIEWPORT, vp, 0);
	
				// First texture destination for output during intersection computation
				activeTraceData = A1Tex;
				
				// The first destination for the final image
				activeImageTex = Image1Tex;
				
				// The first active texture set
				active = 0;
				
				// At first we have to activate the attachments.
				renderToTexture(drawable);
				
				// The first attach of the texture to the attachments... output texture is A1Tex
				attachTextureToFBO(drawable, 0, 4, ioTextures[active]);
				
				// Check the FBO whether everything is fine.
				checkFBO(drawable);
				
				lastRefreshTime = System.currentTimeMillis();
				
				// set initial rendering buffer	
				state = STATE_TEST;
				break;
			}
			case STATE_TEST: 		// Computation with: input A2Tex output A1Tex 
			{
				// set the viewport to the dimensions of the tile
				gl.glViewport(0, 0, TILE_WIDTH, TILE_HEIGHT);
				
				//setActiveTextures(A1Tex, A2Tex);
			
				test.setParameter("tileX", currentTileX);
				
				// Clearing the texture for trace data
				execute(test, 0, true);
				
				// draw the rendered tile to the ByteBuffer
				drawRenderedTile(drawable, drawXTile, drawYTile);	

				currentSample = 0;
				
				// render next tile
				currentTileX++;
				if (currentTileX >= partsWidth) 
				{
					currentTileX = 0;
					currentTileY++;
				}
				if (currentTileY >= partsHeight) 
				{
					state = STATE_FINAL;
				} 
				else 
				{
					//state = STATE_TEST;
				}							

				break;
			}
			case STATE_FINAL: 
			{				
				// Refresh the image one last time
				lastRefreshTime = 0;
				
				// destroy resources
				shutDown(drawable);
				setRenderReady(true);				
	
				// mark render job as finished and exit loop
				state = STATE_DONE;
				synchronized (sunshineAction) 
				{
					sunshineAction.notify();
				}
				return;
			}
			default:
				// error ?
				System.err.println("called state machine with unknown state");
		}
		
		
		// schedule sunshineAction for execution again
		EventQueue.invokeLater(sunshineAction);
	
	}
	
	
	
}
