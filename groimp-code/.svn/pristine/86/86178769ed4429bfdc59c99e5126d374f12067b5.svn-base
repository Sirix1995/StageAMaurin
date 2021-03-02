
package de.grogra.webgl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import javax.vecmath.Matrix4d;

import de.grogra.imp3d.View3D;
import de.grogra.imp3d.io.SceneGraphExport;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.LightNode;
import de.grogra.imp3d.objects.SceneTree;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTreeWithShader;
import de.grogra.pf.io.FileWriterSource;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.ui.Workbench;


/**
 * A simple exporter for WebGL using three.js (threejs.org).
 * 
 * TODO:
 *    - finish work on Polygonizable.java (e.g. ellipses)
 *    - finish Text export
 *    - optimize structure (e.g. use copies of equal NURBS objects, re-use of equal materials)
 *    - support textures shaders
 *    - support lines and polygons
 *    - support extrusion objects, point clouds
 *    - support SuperShape?
 * 
 * 
 * MH 2016-06-28
 */

public class WebGLExport extends SceneGraphExport implements FileWriterSource {

	public static final MetaDataKey<Float> FLATNESS = new MetaDataKey<Float> ("flatness");

	private Workbench workbench = null;

	protected PrintWriter out;

	final Stack<Matrix4d> matrixStack = new Stack<Matrix4d>();

	private int lights = 0;
	protected int primitives = 0;

	public WebGLExport(FilterItem item, FilterSource source) {
		super(item, source);
		setFlavor(item.getOutputFlavor()); // IOflavor retrieved from filter item each time
		
		// put an initial identity transform into the matrixStack
		Matrix4d m = new Matrix4d();
		m.setIdentity();
		matrixStack.push(m);
		
		workbench = Workbench.current();
	}


	@Override
	protected void beginGroup (InnerNode group) throws IOException {
		// push new transformation matrix onto matrix stack
		Matrix4d m = matrixStack.peek();
		Matrix4d n = new Matrix4d();
		group.transform(m, n);
		matrixStack.push(n);
	}

	@Override
	protected SceneTree createSceneTree (View3D scene) {
		SceneTree t = new SceneTreeWithShader (scene) {

			@Override
			protected boolean acceptLeaf(Object object, boolean asNode) {
				return getExportFor(object, asNode) != null;
			}

			@Override
			protected Leaf createLeaf(Object object, boolean asNode, long id) {
				Leaf l = new Leaf (object, asNode, id);
				init(l);
				return l;
			}
		};
		t.createTree(true);
		return t;
	}

	@Override
	protected void endGroup (InnerNode group) throws IOException {
		// remove transformation matrix from matrix stack
		matrixStack.pop();
	}

	@Override
	public void write (File file) throws IOException {
		workbench.beginStatus(this);
		workbench.setStatus(this, "Export WebGL (three.js)", -1);
		
		FileWriter fw = new FileWriter (file);
		BufferedWriter br = new BufferedWriter (fw);
		out = new PrintWriter(br);

		// write header
		writeHeader();
		
		// write contents of the graph
		write ();
		
		// write footer
		writeFooter();

		out.close ();
		br.close ();
		fw.close ();
		
		workbench.clearStatusAndProgress(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public NodeExport getExportFor (Object object, boolean asNode)
	{
		Object s = getGraphState().getObjectDefault(object, asNode, Attributes.SHAPE, null);
		if (s == null) return null;
		
		if(s instanceof LightNode) {
			lights++;
			return new LightExport((LightNode)s);
		}
		NodeExport ex = super.getExportFor (s, asNode);
		if (ex != null) return ex;
		return null;
	}
	


	public void increaseProgress() {
		workbench.setIndeterminateProgress(this);
	}
	
	private void writeHeader() {
		out.println("<!DOCTYPE html>");
		out.println("<html lang=\"en\">");
		out.println("	<head>");
		out.println("		<title>GroIMP WebGL Export</title>");
		out.println("		<meta charset=\"utf-8\">");
		out.println("		<meta name=\"viewport\" content=\"width=device-width, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0\">");
		out.println("		<!-- <link rel=stylesheet href=\"css/base.css\"/> -->");
		out.println("		<style>");
		out.println("			<!-- body {");
		out.println("				font-family: Monospace;");
		out.println("				font-weight: bold;");
		out.println("				background-color: #ccccff;");
		out.println("				margin: 0px;");
		out.println("				overflow: hidden;");
		out.println("			} -->");
		out.println("			#info {");
		out.println("				position: absolute;");
		out.println("				top: 0px; width: 100%;");
		out.println("				color: #00000;");
		out.println("				padding: 5px;");
		out.println("				font-family: Monospace;");
		out.println("				font-size: 13px;");
		out.println("				text-align: center;");
		out.println("			}");
		out.println("			a {");
		out.println("				color: #ff0080;");
		out.println("				text-decoration: none;");
		out.println("			}");
		out.println("			a:hover {");
		out.println("				color: #0080ff;");
		out.println("			}");
		out.println("		</style>");
		out.println("	</head>");
		out.println("	<body>");
		out.println("");
		out.println("		<script src=\"build/three.js\"></script>");
		out.println("		<script src=\"js/curves/NURBSCurve.js\"></script>");
		out.println("		<script src=\"js/curves/NURBSSurface.js\"></script>");
		out.println("		<script src=\"js/curves/NURBSUtils.js\"></script>");
		out.println("		<script src=\"js/controls/OrbitControls.js\"></script>");
		out.println("		<script src=\"js/libs/stats.min.js\"></script>");
		out.println("		<!-- Function parser: https://github.com/silentmatt/js-expression-eval/tree/master -->");
		out.println("		<script src=\"js/parser.js\"></script>");
		out.println("		<!-- Bézier Curves and Surfaces: http://cs.wellesley.edu/~cs307/readings/10-bezier.shtml -->");
		out.println("		<script src=\"js/libs/tw.js\"></script>");
		out.println("");
		out.println("		<script>");
		out.println("			var container, stats;");
		out.println("			var camera, controls, scene, renderer;");
		out.println("");
		out.println("			// start scene");
		out.println("			var loader = new THREE.FontLoader();");
		out.println("			loader.load( 'fonts/helvetiker_regular.typeface.json', function ( font ) {");
		out.println("				init( font );");
		out.println("				animate();");
		out.println("			} );");
		out.println("");
//		out.println("var extrudeSettings = { amount: 8, bevelEnabled: true, bevelSegments: 2, steps: 2, bevelSize: 1, bevelThickness: 1 };");
//		out.println("\n");
		out.println("			function init( font ) {");
		out.println("				container = document.createElement( 'div' );");
		out.println("				document.body.appendChild( container );");
		out.println("");
		out.println("				scene = new THREE.Scene();");
		out.println("				group = new THREE.Group();");
		out.println("				group.position.y = 0;");
		out.println("				scene.add( group );");
		out.println("");
		out.println("				camera = new THREE.PerspectiveCamera( 45, window.innerWidth / window.innerHeight, 0.1, 1500 );");
		out.println("				scene.add(camera);");
		out.println("				camera.position.set(0,-20,20);");
		out.println("				//camera.lookAt(new THREE.Vector3(0, 15, 0));");
		out.println("				camera.lookAt(scene.position);");
		out.println("");
		out.println("				// SKYBOX/FOG");
		out.println("				scene.fog = new THREE.FogExp2( 0x888888, 0.0025 );");
		out.println("");
		out.println("				// AXIS");
		out.println("				scene.add( new THREE.AxisHelper() );");
		out.println("				// wireframe for xy-plane");
		out.println("				var wireframeMaterial = new THREE.MeshBasicMaterial( { color: 0x000088, wireframe: true, side:THREE.DoubleSide } );"); 
		out.println("				var floorGeometry = new THREE.PlaneGeometry(100,100,10,10);");
		out.println("				var floor = new THREE.Mesh(floorGeometry, wireframeMaterial);");
		//out.println("				floor.position.z = -0.01;");
		//out.println("				rotate to lie in x-y plane");
		//out.println("				floor.rotation.x = Math.PI / 2;");
		out.println("				scene.add(floor);");
		
		out.println("");
		out.println("				// HELPER FUNCTINS");
		out.println("				var meshFunction = function(u0, v0) {");
		out.println("					var u = uRange * u0 + uMin;");
		out.println("					var v = vRange * v0 + vMin;");
		out.println("					var x = xFunc(u,v);");
		out.println("					var y = yFunc(u,v);");
		out.println("					var z = zFunc(u,v);");
		out.println("					if ( isNaN(x) || isNaN(y) || isNaN(z) )");
		out.println("						return new THREE.Vector3(0,0,0); // TODO: better fix");
		out.println("					else");
		out.println("						return new THREE.Vector3(x, y, z);");
		out.println("				};");
//		out.println("");
//		out.println("				var pivot = new THREE.Object3D();");
//		out.println("				pivot.add(group);");
//		out.println("				scene.add(pivot);");
//		out.println("				pivot.rotation.x-=1.57;");
		out.println("\n");
		out.println("// SCENE");
	}
	
	private void writeFooter() {
		out.println("\n");
		if(lights==0) {
			out.println("				var light = new THREE.PointLight( 0xffffff, 1.5 );");
			out.println("				light.position.set( 0, 5, 0 );");
			out.println("				scene.add( light );");
			out.println("");
		}
		
		out.println("				// RENDERER");
		out.println("				renderer = new THREE.WebGLRenderer( { antialias: true } );");
		out.println("				renderer.setClearColor( 0xf0f0f0 );");
		//out.println("				renderer.setPixelRatio( window.devicePixelRatio );");
		out.println("				renderer.setSize( window.innerWidth, window.innerHeight );");
		out.println("				container.appendChild( renderer.domElement );");
		out.println("");
		out.println("				// STATS");
		out.println("				stats = new Stats();");
		out.println("				container.appendChild( stats.dom );");
		out.println("");
		out.println("				// CONTROLS");
		out.println("				controls = new THREE.OrbitControls( camera, renderer.domElement );");
		out.println("				window.addEventListener( 'resize', onWindowResize, false );");
		out.println("			}");
		out.println("");
		out.println("			function onWindowResize() {");
		out.println("				camera.aspect = window.innerWidth / window.innerHeight;");
		out.println("				camera.updateProjectionMatrix();");
		out.println("				renderer.setSize( window.innerWidth, window.innerHeight );");
		out.println("			}");
		out.println("");
		out.println("			");
		out.println("			function animate() {");
		out.println("				requestAnimationFrame( animate );");
		out.println("				controls.update();");
		out.println("				render();");
		out.println("				stats.update();");
		out.println("			}");
		out.println("");
		out.println("			function render() {");
		out.println("				renderer.render( scene, camera );");
		out.println("			}");
		out.println("");
		out.println("		</script>");
		out.println("");
		out.println("		<div id=\"info\">");
		out.println("			<a href=\"http://grogra.de\" target=\"_blank\">GroIMP</a> WebGL Export<br/>");
		out.println("			("+ lights+" light, "+primitives+" objects)");
		out.println("		</div>");
		out.println("");
		out.println("	</body>");
		out.println("</html>");
	}

}