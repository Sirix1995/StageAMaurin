package de.grogra.imp3d.glsl;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import de.grogra.graph.GraphState;
import de.grogra.graph.Path;
import de.grogra.imp.objects.Attributes;
import de.grogra.imp3d.LineSegmentizable;
import de.grogra.imp3d.LineSegmentizationCache;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.glsl.GLDisplay.GLVisitor;
import de.grogra.imp3d.glsl.light.LightPos;
import de.grogra.imp3d.glsl.material.GLSLMaterial;
import de.grogra.imp3d.glsl.renderable.GLSLRenderable;
import de.grogra.imp3d.objects.Label;
import de.grogra.imp3d.objects.LightNode;
import de.grogra.imp3d.objects.Line;
import de.grogra.imp3d.objects.Point;
import de.grogra.imp3d.objects.SensorNode;
import de.grogra.imp3d.objects.Sky;
import de.grogra.imp3d.objects.TextLabelBase;
import de.grogra.imp3d.shading.Light;
import de.grogra.imp3d.shading.Shader;
import de.grogra.imp3d.shading.SunSkyLight;
import de.grogra.vecmath.Math2;

/**
 * GLSLUpdateCache is an Implementation of {@link GLVisitor}. 
 * This visitor is used to traverse a Scene Graph. For each node
 * it visits a reference is saved effectively transforming the graph
 * into multiple arrays of graph-nodes. The nodes are sorted 
 * by their type into transparent material, solid material, tools labels
 * and lightsources. 
 * 
 * @author Konni Hartmann
 */
public class GLSLUpdateCache extends GLVisitor {
	private int minPathLength;
	private LineSegmentizationCache lineCache;

	Matrix4d worldToViewInv = new Matrix4d();
	GLSLDisplay disp;
	OpenGLState glState;
	Matrix4d mat = new Matrix4d();

	/**
	 * Constructor for GLSLUpdateCache. This is used to store a reference
	 * to the {@link GLSLDisplay} using this visitor. A reference is needed
	 * to gain access to the OpenGLState and the current Rendergraph-State
	 * @param disp The Display using this Visitor
	 */
	public GLSLUpdateCache(GLSLDisplay disp) {
		disp.super();
		this.disp = disp;
		this.glState = disp.getCurrentGLState();
	}

	private Matrix4d generateLightMatrix(Vector3d lightDirection) {
		Vector3d f = new Vector3d();
		f.normalize(lightDirection);
		f.negate();
		Matrix3d mat = new Matrix3d();
		Math2.getOrthogonalBasis(f, mat, false);

		Matrix4d lightTransf = new Matrix4d();
		lightTransf.set(mat);
		lightTransf.m33 = 1;

		return lightTransf;
	}

	/**
	 * Initializes the visitor for visiting a graph. This stores the inverse
	 * of the current WorldToView matrix.
	 * @param gs
	 * @param t
	 * @param minPathLength
	 * @see #init(GraphState, Matrix4d, de.grogra.imp3d.ViewConfig3D, boolean)
	 */
	public void init(GraphState gs, Matrix4d t, int minPathLength) {
		init(gs, t, disp.getView3D(), false);

		worldToViewInv.invert(t);
		this.minPathLength = minPathLength;

		if (lineCache == null) {
			lineCache = new LineSegmentizationCache(gs, 1);
		}

		rend = 0;
		all = 0;
	}

	/**
	 * @param t
	 */
	public void setCurrentTransformation(Matrix4d t) {
		transformation = t;
	}

	/**
	 * @return The number of renderable nodes found while traversing the graph
	 */
	public int getRend() {
		return rend;
	}

	/**
	 * @return The number of all nodes visited during graph traversal.
	 */
	public int getAll() {
		return all;
	}


	private int rend = 0;
	private int all = 0;

	@SuppressWarnings("unchecked")
	@Override
	protected void visitImpl(Object object, boolean asNode, Shader s, Path path) {
		if ((minPathLength > 0)
				&& (path.getNodeAndEdgeCount() - (asNode ? 0 : 1) < minPathLength)) {
			return;
		}

		all++;

		int layer = state.getIntDefault(object, asNode, Attributes.LAYER, 0);

		Object shape = state.getObjectDefault(object, asNode,
				de.grogra.imp3d.objects.Attributes.SHAPE, null);
		// check if there is an shape object
		if (shape != null) {
			// // checkRepaintWrapException ();
			// // XXX: Setup right BG shader here

			// System.err.println(object + "/" + shape + "/"+path);

			// draw objects like sphere, box, cone
			if (shape instanceof Renderable) {
				rend++;
				Renderable ren = (Renderable) shape;

				if (GLSLDisplay.DEBUG) {
					System.out.println("Adding Drawable!:");
					System.out.println("OBJECT: " + object);
					System.out.println("SHAPE: " + ren);
					System.out.println("PATH: " + path);
				}

				GLSLRenderable glslShape = glState.getShapeManager()
						.getInstance(ren, object, asNode,
								disp.getRenderGraphState());

				if (GLSLDisplay.DEBUG)
					System.out
							.println("-------------------------------------------");

				mat.mul(worldToViewInv, getCurrentTransformation());

				if ((shape instanceof Point) || (shape instanceof Line)
						|| (shape instanceof Label)
						|| (shape instanceof LightNode)
						|| (shape instanceof Sky)
						|| (shape instanceof TextLabelBase)
						|| (shape instanceof SensorNode)) {
					// dr = new Drawable(shape, object, null, null, asNode, mat,
					// getCurrentTransformation());
					if (shape instanceof TextLabelBase)
						glState.deferredLabelRenderable.add(glslShape, null,
								null, asNode, layer, mat,
								getCurrentTransformation());
					else {
						if (shape instanceof Sky) {
							GLSLMaterial material = null;
							glState.setShaderConfSwitch(OpenGLState.SKY_PREVIEW_MATERIAL);
							if (disp.isOptionAltDrawing()
									&& glslShape.isShaderDependant(false)) {
								material = (GLSLMaterial) glslShape.findShader(
										glState, disp, s);
							} else
								material = (GLSLMaterial) disp.findShader(s);
							glState.deferredToolRenderable.add(glslShape, s,
									material, asNode, layer, mat,
									getCurrentTransformation());
							glState.setShaderConfSwitch(OpenGLState.DEFAULT_MATERIAL);
						} else
							glState.deferredToolRenderable.add(glslShape, null,
								null, asNode, layer, mat,
								getCurrentTransformation());
					}
					// glState.setActiveProgram(0);
					// glState.deferredToolRenderable.lastElement().draw(
					// disp, glState);

				} else {
					GLSLMaterial material = null;
					if (disp.isOptionAltDrawing()
							&& glslShape.isShaderDependant(false)) {
						material = (GLSLMaterial) glslShape.findShader(glState,
								disp, s);
					} else
						material = (GLSLMaterial) disp.findShader(s);
					// Shader not found.. skip!
					if (material == null)
						return;

					if (material.isOpaque(s)) {
						// dr = new Drawable(shape, object, s,
						// disp.getSh().getCurrentShader(), asNode, mat,
						// getCurrentTransformation());
						glState.deferredSolidRenderable.add(glslShape, s,
								material, asNode, layer, mat,
								getCurrentTransformation());
						// glState.deferredSolidRenderable.lastElement().activateGLSLShader(glState,
						// disp, glState.isAssumeTranspMaterials());
						// glState.deferredSolidRenderable.lastElement().draw(
						// disp, glState);
					} else {
						// dr = new Drawable(shape, object, s,
						// disp.getSh().getCurrentShader(), asNode, mat,
						// getCurrentTransformation());
						glState.deferredTranspRenderable.add(glslShape, s,
								material, asNode, layer, mat,
								getCurrentTransformation());
						// glState.deferredTranspRenderable.lastElement().rebuildVolumeData(glState);
					}
				}
			}
			// draw curves
			else if (shape instanceof LineSegmentizable) {
				rend++;
				mat.mul(worldToViewInv, getCurrentTransformation());
				GLSLRenderable glslShape = glState.getShapeManager()
						.getInstance(shape, object, asNode,
								disp.getRenderGraphState());
				glState.deferredToolRenderable.add(glslShape, null, null,
						asNode, layer, mat, getCurrentTransformation());

				// Add a new LineSegmentizableDrawable
				// query line color
				// glState.deferredToolRenderable.add(new
				// LineSegmentizableDrawable( (LineSegmentizable)shape, color),
				// object, null,
				// null, asNode, mat, getCurrentTransformation());
			}

		}

		// if (checkLightSources)
		{
			// handle light sources during visit
			Object lightObject = state.getObjectDefault(object, asNode,
					de.grogra.imp3d.objects.Attributes.LIGHT, null);

			if (shape instanceof Sky) {
				disp.setupBGShader((Sky) shape);

				if (lightObject instanceof Light) {
					if (((Light) lightObject).getLightType() == Light.NO_LIGHT)
						glState.defLight.add(new LightPos((Light) shape, null));
				}
			}

			if (lightObject instanceof Light) {
				// checkRepaintWrapException ();
				Light light = (Light) lightObject;
				LightPos pos;
				int lt = light.getLightType();

				if ((lt != Light.NO_LIGHT) /*
											 * && (lt != Light.SKY)
											 */) {
					// XXX: Add new lights.
					GLSLDisplay.printDebugInfoN("Found light: " + light);
					Matrix4d lightToWorld = new Matrix4d();
					lightToWorld
							.mul(worldToViewInv, getCurrentTransformation());
					if (lt == de.grogra.ray.physics.Light.DIRECTIONAL) {
						lightToWorld.setTranslation(new Vector3d(0, 0, 0));
					}
					// if(light instanceof Parallelogram)
					// return;
					if (light instanceof Sky) {
						SunSkyLight ssl = (SunSkyLight) (((Sky) light)
								.getShader());
						light = ssl;
						if (ssl.isDisableLight())
							return;
						glState.defLight.add(new LightPos((Light) shape, null));
						// if(ssl.isDisableSun())
						// return;
						lightToWorld = generateLightMatrix(ssl.getSun());
					}
					pos = new LightPos(light, lightToWorld);

					// disp.getView().getGraph().addAttributeChangeListener(object,
					// true, pos);
					glState.defLight.add(pos);
				}
			}
		}
	}
}