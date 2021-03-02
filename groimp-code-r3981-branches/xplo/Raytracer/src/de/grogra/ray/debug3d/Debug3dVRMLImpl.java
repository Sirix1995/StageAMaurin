package de.grogra.ray.debug3d;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.ray.RTCamera;
import de.grogra.ray.RTObject;
import de.grogra.ray.RTScene;
import de.grogra.ray.RTSceneVisitor;
import de.grogra.ray.util.Ray;


public class Debug3dVRMLImpl implements Debug3dImpl {

//	private final static boolean EXPORT_RAY_WEIGHT = false;
	private final static boolean EXPORT_RAY_WEIGHT = true;
	
	private final static String DEBUG_VRML_FILE = "../Raytracer/debug/debug.wrl";
	private final static float SCREEN_SCALE       = 3.0f;
	private final static float CAMERA_SCALE       = 15.0f;
	private final static float INFINITE_RAY_SCALE = 30.0f;
	private final static float NORMAL_SCALE       = 0.1f;
	private final static float LIGHT_RAY_SCALE    = 1.0f;

	private File m_file = null;
	
	private RTCamera m_camera = null;
	private RTScene m_scene = null;
	private Vector m_infiniteRays = new Vector();
	private Vector m_finiteRays = new Vector();
	private Vector m_normals = new Vector();
	private Vector m_lightRays = new Vector();
	
	
	private File getVRMLFile() {
		if (m_file==null) {
			m_file = new File(DEBUG_VRML_FILE);
		}
		return m_file;
	}
	
	
	private void setCamera(RTCamera camera) {
		m_camera = camera;
	}
	
	
	private void setScene(RTScene scene) {
		m_scene = scene;
	}
	
	
	private void addInfiniteRay(Ray ray,float weight) {
		WeightedRay data = new WeightedRay();
		data.ray = new Ray(ray);
		data.weight = weight;
		m_infiniteRays.add(data);
	}
	
	
	private void addFiniteRay(Ray ray,float scale,float weight) {
		if (scale>1000.0f) {
			addInfiniteRay(ray,weight);
		} else {
			WeightedRay data = new WeightedRay();
			data.ray = new Ray(ray);
			data.ray.getDirection().scale(scale);
			data.weight = weight;
			m_finiteRays.add(data);
		}
	}
	
	
	private void addNormal(Normal normal) {
		m_normals.add(normal);
	}
	
	
	private void addLightRay(Ray ray) {
		Ray new_ray = new Ray(ray);
		m_lightRays.add(new_ray);
	}
	
	
	public void logCamera(RTCamera camera) {
		setCamera(camera);
//		flush();
	}
	
	
	public void logScene(RTScene scene) {
		setScene(scene);
//		flush();
	}
	
	
	public void logInfiniteRay(Ray ray,float weight) {
//		System.out.println("log infinite");
		addInfiniteRay(ray,weight);
	}
	
	
	public void logFiniteRay(Ray ray,float scale,float weight) {
		addFiniteRay(ray,scale,weight);
	}
	
	
	public void logNormal(Tuple3f point,Tuple3f direction) {
		addNormal(new Normal(point,direction));
	}
	
	
	public void logDirectLightRay(Ray ray) {
		addLightRay(ray);
	}
	
	
	public void flush() {
		exportAllToVRML(this.getVRMLFile());
	}
	
	
	public void clear() {
		m_infiniteRays.clear();
		m_finiteRays.clear();
		m_normals.clear();
		m_lightRays.clear();
	}
	
	
	private void exportAllToVRML(File file) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write("#VRML V2.0 utf8");writer.newLine();
			writer.write("");writer.newLine();
			writer.write("  Background { skyColor 1 1 1 }");writer.newLine();
			writer.write("");writer.newLine();
			
			exportScreen(writer);
			exportCamera(writer);
			exportScene(writer);
			exportInfiniteRays(writer);
			exportFiniteRays(writer);
			exportNormals(writer);
			exportLightRays(writer);
			
			writer.flush();
			writer.close();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	
	private void exportScreen(BufferedWriter writer) throws IOException {
		if (m_camera!=null) {
			
			Ray ray = new Ray();
			m_camera.getRayFromCoordinates(0,0,ray);
			Point3f eye = new Point3f(ray.getOrigin());
			Point3f p1  = new Point3f();
			Point3f p2  = new Point3f();
			Point3f p3  = new Point3f();
			Point3f p4  = new Point3f();
			m_camera.getRayFromCoordinates(-1,-1,ray);
			p1.scaleAdd(SCREEN_SCALE,ray.getDirection(),eye);
			m_camera.getRayFromCoordinates(-1, 1,ray);
			p2.scaleAdd(SCREEN_SCALE,ray.getDirection(),eye);
			m_camera.getRayFromCoordinates( 1,-1,ray);
			p3.scaleAdd(SCREEN_SCALE,ray.getDirection(),eye);
			m_camera.getRayFromCoordinates( 1, 1,ray);
			p4.scaleAdd(SCREEN_SCALE,ray.getDirection(),eye);
			
//			try {
				
				writer.write("  Shape {");writer.newLine();
				writer.write("    appearance Appearance {");writer.newLine();
				writer.write("      material Material {");writer.newLine();
				writer.write("        transparency 0.2");writer.newLine();
				writer.write("      }");writer.newLine();
				writer.write("      texture   ImageTexture {");writer.newLine();
				writer.write("        url     \"screen.png\"");writer.newLine();
				writer.write("      }");writer.newLine();
				writer.write("    }");writer.newLine();
				writer.write("    geometry IndexedFaceSet {");writer.newLine();
				writer.write("      solid FALSE");writer.newLine();
				writer.write("      coord Coordinate {");writer.newLine();
				writer.write("        point [");writer.newLine();
				writer.write("          "+p1.x+" "+p1.y+" "+p1.z+" #top left corner");writer.newLine();
				writer.write("          "+p2.x+" "+p2.y+" "+p2.z+" #top right corner");writer.newLine();	
				writer.write("          "+p4.x+" "+p4.y+" "+p4.z+" #bottom right corner");writer.newLine();
				writer.write("          "+p3.x+" "+p3.y+" "+p3.z+" #bottom left corner");writer.newLine();
				writer.write("        ]");writer.newLine();
				writer.write("      }");writer.newLine();
				
				writer.write("      texCoord  TextureCoordinate {");writer.newLine();
				writer.write("        point [");writer.newLine();
				writer.write("          0 1,");writer.newLine();
				writer.write("          0 0,");writer.newLine();
				writer.write("          1 0,");writer.newLine();
				writer.write("          1 1");writer.newLine();
				writer.write("        ]");writer.newLine();
				writer.write("      }");writer.newLine();
				
				writer.write("      coordIndex [");writer.newLine();
				writer.write("        0, 1, 2, 3");writer.newLine();
				writer.write("      ]");writer.newLine();
				
				writer.write("      texCoordIndex [");writer.newLine();
				writer.write("        0, 1, 2, 3");writer.newLine();
				writer.write("      ]");writer.newLine();
				
				writer.write("    }");writer.newLine();
				writer.write("  }");writer.newLine();
				
				
//			} catch (Exception e) {
//				System.err.println(e);
//			}			
			
		}
	}
	
	private void exportCamera(BufferedWriter writer) {
		if (m_camera!=null) {
			
			Ray ray = new Ray();
			m_camera.getRayFromCoordinates(0,0,ray);
			Point3f eye = new Point3f(ray.getOrigin());
			Point3f p1  = new Point3f();
			Point3f p2  = new Point3f();
			Point3f p3  = new Point3f();
			Point3f p4  = new Point3f();
			m_camera.getRayFromCoordinates(-1,-1,ray);
			p1.scaleAdd(CAMERA_SCALE,ray.getDirection(),eye);
			m_camera.getRayFromCoordinates(-1, 1,ray);
			p2.scaleAdd(CAMERA_SCALE,ray.getDirection(),eye);
			m_camera.getRayFromCoordinates( 1,-1,ray);
			p3.scaleAdd(CAMERA_SCALE,ray.getDirection(),eye);
			m_camera.getRayFromCoordinates( 1, 1,ray);
			p4.scaleAdd(CAMERA_SCALE,ray.getDirection(),eye);
			
			try {
				
				writer.write("  Shape {");writer.newLine();
				writer.write("    appearance Appearance {");writer.newLine();
				writer.write("      material Material {");writer.newLine();
				writer.write("        transparency 0.7");writer.newLine();
				writer.write("        diffuseColor 0 0 0.6");writer.newLine();
				writer.write("        emissiveColor 0 0 0.6");writer.newLine();
				writer.write("      }");writer.newLine();
				writer.write("    }");writer.newLine();
				writer.write("    geometry IndexedFaceSet {");writer.newLine();
				writer.write("      solid FALSE");writer.newLine();
				writer.write("      coord Coordinate {");writer.newLine();
				writer.write("        point [");writer.newLine();
				writer.write("          "+eye.x+" "+eye.y+" "+eye.z+" #camera center point");writer.newLine();
				writer.write("          "+p1.x+" "+p1.y+" "+p1.z+" #top left corner");writer.newLine();
				writer.write("          "+p2.x+" "+p2.y+" "+p2.z+" #top right corner");writer.newLine();
				writer.write("          "+p3.x+" "+p3.y+" "+p3.z+" #bottom left corner");writer.newLine();
				writer.write("          "+p4.x+" "+p4.y+" "+p4.z+" #bottom right corner");writer.newLine();
				writer.write("        ]");writer.newLine();
				writer.write("      }");writer.newLine();
				writer.write("      coordIndex [");writer.newLine();
				writer.write("        0, 1, 2, 0, -1, #top triangle");writer.newLine();
				writer.write("        0, 2, 4, 0, -1, #right triangle");writer.newLine();
				writer.write("        0, 4, 3, 0, -1, #bottom triangle");writer.newLine();
				writer.write("        0, 3, 1, 0      #left triangle");writer.newLine();				
				writer.write("      ]");writer.newLine();
				writer.write("    }");writer.newLine();
				writer.write("  }");writer.newLine();
				
				writer.write("  Shape {");writer.newLine();
				writer.write("    appearance Appearance {");writer.newLine();
				writer.write("      material Material {");writer.newLine();
				writer.write("        transparency 0.5");writer.newLine();
				writer.write("        diffuseColor 0 0 0.6");writer.newLine();
				writer.write("        emissiveColor 0 0 0.6");writer.newLine();
				writer.write("      }");writer.newLine();
				writer.write("    }");writer.newLine();
				writer.write("    geometry IndexedLineSet {");writer.newLine();
				writer.write("      coord Coordinate {");writer.newLine();
				writer.write("        point [");writer.newLine();
				writer.write("          "+eye.x+" "+eye.y+" "+eye.z+" #camera center point");writer.newLine();
				writer.write("          "+p1.x+" "+p1.y+" "+p1.z+" #top left corner");writer.newLine();
				writer.write("          "+p2.x+" "+p2.y+" "+p2.z+" #top right corner");writer.newLine();
				writer.write("          "+p3.x+" "+p3.y+" "+p3.z+" #bottom left corner");writer.newLine();
				writer.write("          "+p4.x+" "+p4.y+" "+p4.z+" #bottom right corner");writer.newLine();
				writer.write("        ]");writer.newLine();
				writer.write("      }");writer.newLine();
				writer.write("      coordIndex [");writer.newLine();
				writer.write("        0, 1, -1, #top left corner");writer.newLine();
				writer.write("        0, 2, -1, #top right corner");writer.newLine();
				writer.write("        0, 4, -1, #bottom left corner");writer.newLine();
				writer.write("        0, 3      #bottom right corner");writer.newLine();				
				writer.write("      ]");writer.newLine();
				writer.write("    }");writer.newLine();
				writer.write("  }");writer.newLine();
				
			} catch (Exception e) {
				System.err.println(e);
			}			
			
		}
	}
	
	
	private void exportScene(BufferedWriter writer) {
		if (m_scene!=null) {
			RTSceneVisitor visitor = new RTSceneExportVisitor(writer);
			m_scene.traversSceneObjects(visitor);
			m_scene.traversSceneLights(visitor);
			try {
				writer.newLine();
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}
	
	
	private void exportInfiniteRays(BufferedWriter writer) {
//		System.out.println("export infinite rays:"+m_infiniteRays.size());
		if (m_infiniteRays.size()>0) {
			try {
				
				WeightedRay cur_ray;
				Point3f to = new Point3f();
				
				for (int i=0;i<m_infiniteRays.size();i++) {
					
					cur_ray = (WeightedRay)m_infiniteRays.elementAt(i);
					to.scaleAdd(INFINITE_RAY_SCALE,cur_ray.ray.getDirection(),cur_ray.ray.getOrigin());
					
					writer.newLine();
					writer.write("  Shape {");writer.newLine();
					writer.write("    appearance Appearance {");writer.newLine();
					writer.write("      material Material {");writer.newLine();
					if (EXPORT_RAY_WEIGHT) {
						writer.write("        transparency "+(1.0f-cur_ray.weight));writer.newLine();
					}
					writer.write("        diffuseColor 0.4 0.4 0.4");writer.newLine();
//					writer.write("        emissiveColor 0 0 0.6");writer.newLine();
					writer.write("      }");writer.newLine();
					writer.write("    }");writer.newLine();
					writer.write("    geometry IndexedLineSet {");writer.newLine();
					writer.write("      coord Coordinate {");writer.newLine();
					writer.write("        point [");writer.newLine();
					writer.write("          "+
							cur_ray.ray.getOrigin().x+" "+
							cur_ray.ray.getOrigin().y+" "+
							cur_ray.ray.getOrigin().z);writer.newLine();
					writer.write("          "+to.x+" "+to.y+" "+to.z);writer.newLine();
					writer.write("        ]");writer.newLine();
					writer.write("      }");writer.newLine();
					writer.write("      coordIndex [");writer.newLine();
					writer.write("        0, 1");writer.newLine();			
					writer.write("      ]");writer.newLine();
					writer.write("    }");writer.newLine();
					writer.write("  }");writer.newLine();
					
				}
				writer.newLine();
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}
	
	
	private void exportFiniteRays(BufferedWriter writer) {
//		System.out.println("export finite rays:"+m_finiteRays.size());
		if (m_finiteRays.size()>0) {
			try {
				
				WeightedRay cur_ray;
				Point3f to = new Point3f();
				
				for (int i=0;i<m_finiteRays.size();i++) {
					
					cur_ray = (WeightedRay)m_finiteRays.elementAt(i);
					to.scaleAdd(1,cur_ray.ray.getDirection(),cur_ray.ray.getOrigin());
//					System.out.println("from:"+cur_ray.ray.getOrigin());
//					System.out.println("to:"+to);
					
					writer.newLine();
					writer.write("  Shape {");writer.newLine();
					writer.write("    appearance Appearance {");writer.newLine();
					writer.write("      material Material {");writer.newLine();
					if (EXPORT_RAY_WEIGHT) {
						writer.write("        transparency "+(1.0f-cur_ray.weight));writer.newLine();
					}
					writer.write("        diffuseColor 0.0 0.7 0.0");writer.newLine();
					writer.write("        emissiveColor 0.0 0.7 0.0");writer.newLine();
//					writer.write("        emissiveColor 0 0 0.6");writer.newLine();
					writer.write("      }");writer.newLine();
					writer.write("    }");writer.newLine();
					writer.write("    geometry IndexedLineSet {");writer.newLine();
					writer.write("      coord Coordinate {");writer.newLine();
					writer.write("        point [");writer.newLine();
					writer.write("          "+
							cur_ray.ray.getOrigin().x+" "+
							cur_ray.ray.getOrigin().y+" "+
							cur_ray.ray.getOrigin().z);writer.newLine();
					writer.write("          "+to.x+" "+to.y+" "+to.z);writer.newLine();
					writer.write("        ]");writer.newLine();
					writer.write("      }");writer.newLine();
					writer.write("      coordIndex [");writer.newLine();
					writer.write("        0, 1");writer.newLine();			
					writer.write("      ]");writer.newLine();
					writer.write("    }");writer.newLine();
					writer.write("  }");writer.newLine();
					
				}
				writer.newLine();
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}
	
	
	private void exportNormals(BufferedWriter writer) {
//		System.out.println("export normals:"+m_normals.size());
		if (m_normals.size()>0) {
			try {
				
				Normal cur_normal;
				Point3f to = new Point3f();
				
				for (int i=0;i<m_normals.size();i++) {
					
					cur_normal = (Normal)m_normals.elementAt(i);
					to.scaleAdd(NORMAL_SCALE,cur_normal.direction,cur_normal.point);
//					System.out.println("from:"+cur_ray.getOrigin());
//					System.out.println("to:"+to);				
					writer.newLine();
					writer.write("  Shape {");writer.newLine();
					writer.write("    appearance Appearance {");writer.newLine();
					writer.write("      material Material {");writer.newLine();
					writer.write("        transparency 0.5");writer.newLine();
					writer.write("        diffuseColor 1 0 0");writer.newLine();
					writer.write("        emissiveColor 1 0 0");writer.newLine();
					writer.write("      }");writer.newLine();
					writer.write("    }");writer.newLine();
					writer.write("    geometry IndexedLineSet {");writer.newLine();
					writer.write("      coord Coordinate {");writer.newLine();
					writer.write("        point [");writer.newLine();
					writer.write("          "+
							cur_normal.point.x+" "+
							cur_normal.point.y+" "+
							cur_normal.point.z);writer.newLine();
					writer.write("          "+to.x+" "+to.y+" "+to.z);writer.newLine();
					writer.write("        ]");writer.newLine();
					writer.write("      }");writer.newLine();
					writer.write("      coordIndex [");writer.newLine();
					writer.write("        0, 1");writer.newLine();			
					writer.write("      ]");writer.newLine();
					writer.write("    }");writer.newLine();
					writer.write("  }");writer.newLine();
					
				}
				writer.newLine();
			} catch (Exception e) {
				System.err.println(e);
			}
		}		
	}
	
	
	private void exportLightRays(BufferedWriter writer) {
		if (m_lightRays.size()>0) {
			try {
				
				Ray cur_ray;
				Point3f to = new Point3f();
				
				for (int i=0;i<m_lightRays.size();i++) {
					
					cur_ray = (Ray)m_lightRays.elementAt(i);
					to.scaleAdd(LIGHT_RAY_SCALE,cur_ray.direction,cur_ray.origin);			
					writer.newLine();
					writer.write("  Shape {");writer.newLine();
					writer.write("    appearance Appearance {");writer.newLine();
					writer.write("      material Material {");writer.newLine();
					writer.write("        transparency 0.2");writer.newLine();
					writer.write("        diffuseColor 1 1 0");writer.newLine();
					writer.write("        emissiveColor 1 1 0");writer.newLine();
					writer.write("      }");writer.newLine();
					writer.write("    }");writer.newLine();
					writer.write("    geometry IndexedLineSet {");writer.newLine();
					writer.write("      coord Coordinate {");writer.newLine();
					writer.write("        point [");writer.newLine();
					writer.write("          "+
							cur_ray.origin.x+" "+
							cur_ray.origin.y+" "+
							cur_ray.origin.z);writer.newLine();
					writer.write("          "+to.x+" "+to.y+" "+to.z);writer.newLine();
					writer.write("        ]");writer.newLine();
					writer.write("      }");writer.newLine();
					writer.write("      coordIndex [");writer.newLine();
					writer.write("        0, 1");writer.newLine();			
					writer.write("      ]");writer.newLine();
					writer.write("    }");writer.newLine();
					writer.write("  }");writer.newLine();
					
				}
				writer.newLine();
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}
	
	
	private class RTSceneExportVisitor implements RTSceneVisitor {
		
		private BufferedWriter m_writer;
		
		public RTSceneExportVisitor(BufferedWriter writer) {
			m_writer = writer;
		}
	
		public void visitObject(RTObject object) {
			if (object instanceof ExportableToVRML) {
				try {
					m_writer.newLine();
					((ExportableToVRML)object).exportToVRML(m_writer);
				} catch (Exception e) {
					System.err.println(e);
				}
			}
		}
	}

	
	private class WeightedRay {
		public Ray ray = null;
		public float weight;
	}
	
	
	private class Normal {
		public Tuple3f point = new Point3f();
		public Tuple3f direction = new Vector3f();
		
		public Normal(Tuple3f point,Tuple3f direction) {
			this.point.set(point);
			this.direction.set(direction);
		}
	}


}
