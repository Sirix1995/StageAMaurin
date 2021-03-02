package de.grogra.ray.intersection;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import de.grogra.ray.shader.RTShader;
import de.grogra.ray.util.Ray;


/**
 * This is a helpful utility class for intersection calculations. It offers 
 * static mehtods to calculate intersections between a ray and a sphere, a box,
 * a cylinder, a cone, a cone frustum, a plane or a parallelogram. For each of 
 * this cases there are two methods that can be used:
 *   get[object]_T Calculates only the distance of the ray origin to the 
 *                 intersection point.
 *   get[object]_IntersectionDescription Calculates diffenrent additional 
 *                                       values of the intersection
 *                                       point.
 *                                       
 *   !!! It is important that you call get[object]_T first and with the same 
 *   instance of the local varialbles object and after this second the
 *   method get[object]_IntersectionDescription. !!!
 * 
 * @author Micha
 *
 */
public class Intersections {

	public final static int EVALUATE_POINT          = 1 << 1; // 2
	public final static int EVALUATE_NORMAL         = 1 << 2; // 4
	public final static int EVALUATE_TANGET_VECTORS = 1 << 3; // 8
	public final static int EVALUATE_UV_COORDINATES = 1 << 4; // 16
	public final static int EVALUATE_TRANSPARENCY   = 1 << 5; // 32
	public final static int EVALUATE_SHADER         = 1 << 6; // 64
	
//	private final static double MIN_T_VALUE = 0.004;
	
	private final static float FLOAT_PI = (float)Math.PI;
	
	public final static int UNDEFINED_PART = 0;
	public final static int BOX_FRONT      = 1;
	public final static int BOX_BACK       = 2;
	public final static int BOX_TOP        = 3;
	public final static int BOX_BOTTOM     = 4;
	public final static int BOX_LEFT       = 5;
	public final static int BOX_RIGHT      = 6;
	
	
	public static boolean isPointInsideBox(Point3d point, Vector3f minValues, Vector3f maxValues) {
		if ((point.x<minValues.x) || (point.x>maxValues.x)) { return false; }
		if ((point.y<minValues.y) || (point.y>maxValues.y)) { return false; }
		if ((point.z<minValues.z) || (point.z>maxValues.z)) { return false; }
		return true;
	}
	
	
	public static boolean isPointInsideBox(Point3f point, Vector3f minValues, Vector3f maxValues) {
		if ((point.x<minValues.x) || (point.x>maxValues.x)) { return false; }
		if ((point.y<minValues.y) || (point.y>maxValues.y)) { return false; }
		if ((point.z<minValues.z) || (point.z>maxValues.z)) { return false; }
		return true;
	}
	
	
	public static boolean getSphere_hasIntersection(
			SphereIntersectionInput input,
			SphereIntersectionLocalVariables local) {
		
		input.ray.transform(input.invers_transformation,local.invers_ray);
		
		local.dg.set(local.invers_ray.getDirection());
		local.g.set(local.invers_ray.getOrigin());
		// t*a + t*b + c = 0
		local.a = local.dg.dot(local.dg);
		local.b = 2*local.g.dot(local.dg);
		local.c = local.g.dot(local.g)-input.squareRadius;

		local.D = local.b*local.b - 4.0f*local.a*local.c;
		
		if (local.D<0.0) { 
			return false;
		} else  {
			local.invers_t = (-local.b-(float)Math.sqrt(local.D))*0.5f/local.a;
			if (local.invers_t>0.0) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	
	public static boolean getBox_hasIntersection(
			BoxIntersectionInput input,
			BoxIntersectionLocalVariables local) {
		
		if (input.ray.getDirection().x!=0.0f) {
			local.t = (input.minValues.x-input.ray.getOrigin().x)/input.ray.getDirection().x;
			if (local.t>0.0f) {
				
				local.point.scaleAdd(local.t,input.ray.getDirection(),input.ray.getOrigin());
				
				if ((local.point.y>=input.minValues.y) && (local.point.y<=input.maxValues.y)) {
					if ((local.point.z>=input.minValues.z) && (local.point.z<=input.maxValues.z)) {
						return true;
					}
				}
			}
			local.t = (input.maxValues.x-input.ray.getOrigin().x)/input.ray.getDirection().x;
			if (local.t>0.0f) {
				local.point.scaleAdd(local.t,input.ray.getDirection(),input.ray.getOrigin());
				if ((local.point.y>=input.minValues.y) && (local.point.y<=input.maxValues.y)) {
					if ((local.point.z>=input.minValues.z) && (local.point.z<=input.maxValues.z)) {
						return true;
					}
				}
			}
		}
		if (input.ray.getDirection().y!=0.0f) {
			local.t = (input.minValues.y-input.ray.getOrigin().y)/input.ray.getDirection().y;
			if (local.t>0.0f) {
				local.point.scaleAdd(local.t,input.ray.getDirection(),input.ray.getOrigin());
				if ((local.point.x>=input.minValues.x) && (local.point.x<=input.maxValues.x)) {
					if ((local.point.z>=input.minValues.z) && (local.point.z<=input.maxValues.z)) {
						return true;
					}
				}
			}
			local.t = (input.maxValues.y-input.ray.getOrigin().y)/input.ray.getDirection().y;
			if (local.t>0.0f) {
				local.point.scaleAdd(local.t,input.ray.getDirection(),input.ray.getOrigin());
				if ((local.point.x>=input.minValues.x) && (local.point.x<=input.maxValues.x)) {
					if ((local.point.z>=input.minValues.z) && (local.point.z<=input.maxValues.z)) {
						return true;
					}
				}
			}
		}
		if (input.ray.getDirection().z!=0.0f) {
			local.t = (input.minValues.z-input.ray.getOrigin().z)/input.ray.getDirection().z;
			if (local.t>0.0f) {
				local.point.scaleAdd(local.t,input.ray.getDirection(),input.ray.getOrigin());
				//System.out.println("point:"+local.point);
				if ((local.point.x>=input.minValues.x) && (local.point.x<=input.maxValues.x)) {
					if ((local.point.y>=input.minValues.y) && (local.point.y<=input.maxValues.y)) {
						return true;
					}
				}
			}
			local.t = (input.maxValues.z-input.ray.getOrigin().z)/input.ray.getDirection().z;
			if (local.t>0.0f) {
				local.point.scaleAdd(local.t,input.ray.getDirection(),input.ray.getOrigin());
				if ((local.point.x>=input.minValues.x) && (local.point.x<=input.maxValues.x)) {
					if ((local.point.y>=input.minValues.y) && (local.point.y<=input.maxValues.y)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	public static boolean getSphere_T(
			SphereInput input, 
			ObjectOutput output, 
			SphereLocalVariables local) {
		
		if (input.minIndex>1) {	return false; }
		
		output.hasIntersection = false;
				
		input.ray.transform(input.invers_transformation,local.invers_ray);
		
		local.dg.set(local.invers_ray.getDirection());
		local.g.set(local.invers_ray.getOrigin());
		// t*a + t*b + c = 0
		local.a = local.dg.dot(local.dg);
		local.b = 2*local.g.dot(local.dg);
		local.c = local.g.dot(local.g)-input.squareRadius;

		local.D = local.b*local.b - 4.0f*local.a*local.c;
		
		if (local.D<=0.0) { 
			output.hasIntersection = false;
		} else  {
			if (input.minIndex==0) {
				local.t_index  = 0;
				local.invers_t = (-local.b-(float)Math.sqrt(local.D))*0.5f/local.a;
			} else if (input.minIndex==1) {
				local.t_index  = 1;
				local.invers_t = (-local.b+(float)Math.sqrt(local.D))*0.5f/local.a;
			}
			if (local.invers_t<0.0f) {
				output.hasIntersection = false;
			} else {		
				local.t = local.invers_t/input.ray.getDirection().length();			
				output.hasIntersection = true;
				output.t = local.t;
			}
		}
		return output.hasIntersection;
	}
	
	
	/**
	 * This method calculates different intersection values. 
	 * 
	 * !IMPORTANT! 
	 * It is required that the method getSphere_T was executed previously. 
	 */
	public static void getSphere_IntersectionDescription(
			SphereInput input,
			int params,
			IntersectionDescription desc,
			SphereLocalVariables local) {
		
		if ((local.D<0.0) || (local.invers_t<=0.0f)) { 
			desc.setIntersectionCount(0);
			return;
		} 
		
		desc.setIntersectionCount(2);
		desc.intersectionIndex = local.t_index;
		
		// evaluate intersection point
		local.invers_point.scaleAdd(local.invers_t,local.dg,local.g);
		local.point.scaleAdd(local.t,input.ray.getDirection(),input.ray.getOrigin());
		desc.setPoint(local.point,local.t);
		desc.setLocalPoint(local.invers_point);
		
		// evaluate intersection normal vector
		Vector3f n = local.normal;
		n.set (local.invers_point);
		n.normalize ();
		
		float cosv = n.z;
		float t = 1 - n.z * n.z;
		float sinv = (t <= 0) ? 0 : (float) Math.sqrt (t);
		
		float cosu;
		float sinu;
		
		if (sinv == 0)
		{
			cosu = 1;
			sinu = 0;
		}
		else
		{
			cosu = n.x / sinv;
			sinu = n.y / sinv;
		}
		
		input.transformation.transform (n);
		n.normalize();
		desc.setNormal(n);
		
		// evaluate intersection tangent vectors
		if ( ((params & Intersections.EVALUATE_TANGET_VECTORS)!=0)	) {
			
			desc.getTangenteU().set((-2 * FLOAT_PI) * local.invers_point.y,(2 * FLOAT_PI) * local.invers_point.x, 0);
			input.transformation.transform (desc.getTangenteU());
			desc.getTangenteV().set(-FLOAT_PI * input.radius * cosu * cosv, -FLOAT_PI * input.radius * sinu * cosv, FLOAT_PI * input.radius * sinv);
			input.transformation.transform (desc.getTangenteV());
		}
		
		// evaluate intersection uv coordinates
		if ( ((params & Intersections.EVALUATE_UV_COORDINATES)!=0)	) {
			
			local.u = (float) Math.atan2 (sinu, cosu) * (1 / (2 * FLOAT_PI));
			if (local.u < 0)
			{
				local.u += 1;
			}
			local.v = (float) Math.acos (-cosv) * (1 / FLOAT_PI);
			desc.getUVCoordinate().set(local.u,local.v);
			
		}
		
	}
	
	
	public static boolean getPlane_T(
			PlaneInput input, 
			ObjectOutput output, 
			PlaneLocalVariables local) {
		
		output.hasIntersection = false;
		
		input.ray.transform(input.invers_transformation,local.invers_ray);
				
		if (local.invers_ray.getDirection().z>=0.0f) {
			output.hasIntersection = false;
		} else {
			local.invers_t = -local.invers_ray.getOrigin().z/
				local.invers_ray.getDirection().z;

			if (input.transparencyShader!=null) {
				local.invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
				local.t = local.invers_t/input.ray.getDirection().length();	
				local.point.scaleAdd(local.t,input.ray.getDirection(),input.ray.getOrigin());
				local.transparencyInput.localPoint.set(local.invers_point);
				local.transparencyInput.point.set(local.point);
				local.transparencyInput.uv.set(local.invers_point.x,local.invers_point.y);
				if (!input.transparencyShader.isTransparent(local.transparencyInput)) {
					output.hasIntersection = true;
					output.t = local.t;
				} else {
					output.hasIntersection = false;
				}
			} else {
				local.t = local.invers_t/input.ray.getDirection().length();			
				output.hasIntersection = true;
				output.t = local.t;
			}				
		}
		
		local.hasIntersection = output.hasIntersection;
		return output.hasIntersection;
	}
	
	
	/**
	 * This method calculates different intersection values. 
	 * 
	 * !IMPORTANT! 
	 * It is required that the method getPlane_T was executed previously. 
	 */
	public static void getPlane_IntersectionDescription(
			PlaneInput input,
			int params,
			IntersectionDescription desc,
			PlaneLocalVariables local) {
		
		if (!local.hasIntersection) {
			desc.setIntersectionCount(0);
			return;
		} else {
			desc.setIntersectionCount(1);
		}
		
		// evaluate intersection point
		if ( ((params & Intersections.EVALUATE_POINT)!=0) ||
			 ((params & Intersections.EVALUATE_NORMAL)!=0) ||
			 ((params & Intersections.EVALUATE_TANGET_VECTORS)!=0) ) {
		
			local.invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			local.point.scaleAdd(local.t,input.ray.getDirection(),input.ray.getOrigin());
			desc.setPoint(local.point,local.t);
			desc.setLocalPoint(local.invers_point);
			
		}
		
		// evaluate intersection normal vector
		if ( ((params & Intersections.EVALUATE_NORMAL)!=0)	) {
			desc.getNormal().set(input.normal);
		}
		
		// evaluate intersection tangent vectors
		if ( ((params & Intersections.EVALUATE_TANGET_VECTORS)!=0)	) {
			
			desc.getTangenteU().set(input.tangenteU);
			desc.getTangenteU().set(input.tangenteV);
			//desc.getTangenteU().set(1.0f,0.0f,0.0f);
			//input.transformation.transform(desc.getTangenteU());
		}
		
		// evaluate intersection uv coordinates
		if ( ((params & Intersections.EVALUATE_UV_COORDINATES)!=0)	) {
			
			desc.getUVCoordinate().x = local.invers_point.x;
			desc.getUVCoordinate().y = local.invers_point.y;
		}
	}
	
	
	public static boolean getParallelogram_T(
			ParallelogramInput input,
			ObjectOutput output, 
			ParallelogramLocalVariables local) {
		
		output.hasIntersection = false;
		
		input.ray.transform(input.invers_transformation,local.invers_ray);
		
		if (local.invers_ray.getDirection().z!=0.0f) {
			local.invers_t = -local.invers_ray.getOrigin().z/local.invers_ray.getDirection().z;
			
			local.invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			output.hasIntersection = false;
			if (local.invers_point.x>=0.0f) {
				if (local.invers_point.x<=1.0f) {
					if (local.invers_point.y>=-1.0f) {//
						if (local.invers_point.y<=1.0f) {
							if (input.transparencyShader!=null) {
								local.t = local.invers_t/input.ray.getDirection().length();		
								local.point.scaleAdd(local.t,input.ray.getDirection(),input.ray.getOrigin());
								local.transparencyInput.localPoint.set(local.invers_point);
								local.transparencyInput.point.set(local.point);
								local.transparencyInput.uv.set(local.invers_point.y*0.5f-0.5f,local.invers_point.x);
								if (!input.transparencyShader.isTransparent(local.transparencyInput)) {	
									output.hasIntersection = true;
									output.t = local.t;	
								}
							} else {
								local.t = local.invers_t/input.ray.getDirection().length();			
								output.hasIntersection = true;
								output.t = local.t;	
							}
						}
					}
				}
			}

		} else {
			output.hasIntersection = false;
		}
		
		local.hasIntersection = output.hasIntersection;
		return output.hasIntersection;		
	}
	
	
	/**
	 * This method calculates different intersection values. 
	 * 
	 * !IMPORTANT! 
	 * It is required that the method getParallelogram_T was executed previously. 
	 */
	public static void getParallelogram_IntersectionDescription(
			ParallelogramInput input,
			int params,
			IntersectionDescription desc,
			ParallelogramLocalVariables local) {
		
		if (!local.hasIntersection) {
			desc.setIntersectionCount(0);
			return;
		} else {
			desc.setIntersectionCount(1);
		}
		
		// evaluate intersection point
		if ( ((params & Intersections.EVALUATE_POINT)!=0) ||
			 ((params & Intersections.EVALUATE_NORMAL)!=0) ||
			 ((params & Intersections.EVALUATE_TANGET_VECTORS)!=0) ) {
		
			local.point.scaleAdd(local.t,input.ray.getDirection(),input.ray.getOrigin());
			desc.setPoint(local.point,local.t);
			desc.setLocalPoint(local.invers_point);
		}
		
		// evaluate intersection normal vector
		if ( ((params & Intersections.EVALUATE_NORMAL)!=0)	) {
			desc.getNormal().set(input.normal);
		}
		
		// evaluate intersection tangent vectors
		if ( ((params & Intersections.EVALUATE_TANGET_VECTORS)!=0)	) {
			
			desc.getTangenteU().set(input.tangenteU);
			desc.getTangenteU().set(input.tangenteV);
		}
		
		// evaluate intersection uv coordinates
		if ( ((params & Intersections.EVALUATE_UV_COORDINATES)!=0)	) {
			
			desc.getUVCoordinate().x = local.invers_point.y*0.5f-0.5f;
			desc.getUVCoordinate().y = local.invers_point.x;
		}
	}
	
	
	public static boolean getBox_T(
			BoxInput input, 
			ObjectOutput output, 
			BoxLocalVariables local) {
		
		output.hasIntersection = false;
		
		input.ray.transform(input.invers_transformation,local.invers_ray);
		
		local.t = Float.MAX_VALUE;
		output.hasIntersection = false;
		
		// front
		if ((local.invers_ray.getDirection().y>0.0f)^(input.minIndex==1)) {
			local.invers_t = (-input.expansion_y-local.invers_ray.getOrigin().y)/
							 local.invers_ray.getDirection().y;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			
			if ((local.cur_invers_point.x>=-input.expansion_x) && (local.cur_invers_point.x<=input.expansion_x) &&
				(local.cur_invers_point.z>=-input.expansion_z) && (local.cur_invers_point.z<=input.expansion_z)) {
				local.cur_t = local.invers_t/input.ray.getDirection().length();
				if ((local.cur_t>0.0f) && (local.cur_t<local.t)) {

					local.intersection_part = BoxLocalVariables.FRONT;
					local.t = local.cur_t;
					local.invers_point.set(local.cur_invers_point);
					output.hasIntersection = true;
					output.t = local.t;

				}
			}
		}
		
		// back
		if ((local.invers_ray.getDirection().y<0.0f)^(input.minIndex==1)) {
			local.invers_t = (input.expansion_y-local.invers_ray.getOrigin().y)/
							 local.invers_ray.getDirection().y;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			
			if ((local.cur_invers_point.x>=-input.expansion_x) && (local.cur_invers_point.x<=input.expansion_x) &&
				(local.cur_invers_point.z>=-input.expansion_z) && (local.cur_invers_point.z<=input.expansion_z)) {
				local.cur_t = local.invers_t/input.ray.getDirection().length();
				if ((local.cur_t>0.0f) && (local.cur_t<local.t)) {

					local.intersection_part = BoxLocalVariables.BACK;
					local.t = local.cur_t;
					local.invers_point.set(local.cur_invers_point);
					output.hasIntersection = true;
					output.t = local.t;

				}
			}
		}
		
		// top
		if ((local.invers_ray.getDirection().z<0.0f)^(input.minIndex==1)) {
			local.invers_t = (input.expansion_z-local.invers_ray.getOrigin().z)/
							 local.invers_ray.getDirection().z;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			
			if ((local.cur_invers_point.x>=-input.expansion_x) && (local.cur_invers_point.x<=input.expansion_x) &&
				(local.cur_invers_point.y>=-input.expansion_y) && (local.cur_invers_point.y<=input.expansion_y)) {
				local.cur_t = local.invers_t/input.ray.getDirection().length();
				if ((local.cur_t>0.0f) && (local.cur_t<local.t)) {

					local.intersection_part = BoxLocalVariables.TOP;
					local.t = local.cur_t;
					local.invers_point.set(local.cur_invers_point);
					output.hasIntersection = true;
					output.t = local.t;

				}
			}
		}
		
		// bottom
		if ((local.invers_ray.getDirection().z>0.0f)^(input.minIndex==1)) {
			local.invers_t = (-input.expansion_z-local.invers_ray.getOrigin().z)/
							 local.invers_ray.getDirection().z;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			
			if ((local.cur_invers_point.x>=-input.expansion_x) && (local.cur_invers_point.x<=input.expansion_x) &&
				(local.cur_invers_point.y>=-input.expansion_y) && (local.cur_invers_point.y<=input.expansion_y)) {
				local.cur_t = local.invers_t/input.ray.getDirection().length();
				if ((local.cur_t>0.0f) && (local.cur_t<local.t)) {

					local.intersection_part = BoxLocalVariables.BOTTOM;
					local.t = local.cur_t;
					local.invers_point.set(local.cur_invers_point);
					output.hasIntersection = true;
					output.t = local.t;

				}
			}
		}
		// left
		if ((local.invers_ray.getDirection().x>0.0f)^(input.minIndex==1)) {
			local.invers_t = (-input.expansion_x-local.invers_ray.getOrigin().x)/
							 local.invers_ray.getDirection().x;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			
			if ((local.cur_invers_point.y>=-input.expansion_y) && (local.cur_invers_point.y<=input.expansion_y) &&
				(local.cur_invers_point.z>=-input.expansion_z) && (local.cur_invers_point.z<=input.expansion_z)) {
				local.cur_t = local.invers_t/input.ray.getDirection().length();
				if ((local.cur_t>0.0f) && (local.cur_t<local.t)) {

					local.intersection_part = BoxLocalVariables.LEFT;
					local.t = local.cur_t;
					local.invers_point.set(local.cur_invers_point);
					output.hasIntersection = true;
					output.t = local.t;

				}
			}
		}
		// right
		if ((local.invers_ray.getDirection().x<0.0f)^(input.minIndex==1)) {
			local.invers_t = (input.expansion_x-local.invers_ray.getOrigin().x)/
							 local.invers_ray.getDirection().x;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			
			if ((local.cur_invers_point.y>=-input.expansion_y) && (local.cur_invers_point.y<=input.expansion_y) &&
				(local.cur_invers_point.z>=-input.expansion_z) && (local.cur_invers_point.z<=input.expansion_z)) {
				local.cur_t = local.invers_t/input.ray.getDirection().length();
				if ((local.cur_t>0.0f) && (local.cur_t<local.t)) {

					local.intersection_part = BoxLocalVariables.RIGHT;
					local.t = local.cur_t;
					local.invers_point.set(local.cur_invers_point);
					output.hasIntersection = true;
					output.t = local.t;

				}
			}
		}
		
		local.hasIntersection = output.hasIntersection;
		return output.hasIntersection;
	}
	
	
	/**
	 * This method calculates different intersection values. 
	 * 
	 * !IMPORTANT! 
	 * It is required that the method getBox_T was executed previously. 
	 */
	public static void getBox_IntersectionDescription(
			BoxInput input,
			int params,
			IntersectionDescription desc,
			BoxLocalVariables local) {
				
		// evaluate intersection point
		if ( ((params & Intersections.EVALUATE_POINT)!=0) ||
			 ((params & Intersections.EVALUATE_NORMAL)!=0) ||
			 ((params & Intersections.EVALUATE_TANGET_VECTORS)!=0) ) {
		
			local.point.scaleAdd(local.t,input.ray.getDirection(),input.ray.getOrigin());
			desc.setPoint(local.point,local.t);
			desc.setLocalPoint(local.invers_point);
		}
		
		// evaluate intersection normal vector
		if ( ((params & Intersections.EVALUATE_NORMAL)!=0)	) {
			switch(local.intersection_part) {
			case BoxLocalVariables.TOP:
				desc.getNormal().set(input.top_normal);
				break;
			case BoxLocalVariables.BOTTOM:
				desc.getNormal().set(-input.top_normal.x,
						             -input.top_normal.y,
						             -input.top_normal.z);
				break;
			case BoxLocalVariables.FRONT:
				desc.getNormal().set(input.front_normal);
				break;
			case BoxLocalVariables.BACK:
				desc.getNormal().set(-input.front_normal.x,
						             -input.front_normal.y,
						             -input.front_normal.z);
				break;
			case BoxLocalVariables.RIGHT:
				desc.getNormal().set(input.right_normal);
				break;
			case BoxLocalVariables.LEFT:
				desc.getNormal().set(-input.right_normal.x,
						             -input.right_normal.y,
						             -input.right_normal.z);
				break;
			}
			
		}
		
		// evaluate intersection tangent vectors
		if ( ((params & Intersections.EVALUATE_TANGET_VECTORS)!=0)	) {
			switch(local.intersection_part) {
			case BoxLocalVariables.TOP:
				desc.getTangenteU().set(input.right_normal);
				desc.getTangenteV().set(-input.front_normal.x,
			            				-input.front_normal.y,
			            				-input.front_normal.z);
				break;
			case BoxLocalVariables.BOTTOM:
				desc.getTangenteU().set(input.right_normal);
				desc.getTangenteV().set(input.front_normal);
				break;
			case BoxLocalVariables.FRONT:
				desc.getTangenteU().set(input.right_normal);
				desc.getTangenteV().set(input.top_normal);
				break;
			case BoxLocalVariables.BACK:
				desc.getTangenteU().set(input.right_normal);
				desc.getTangenteV().set(-input.top_normal.x,
		                				-input.top_normal.y,
		                				-input.top_normal.z);
				break;
			case BoxLocalVariables.LEFT:
				desc.getTangenteU().set(input.front_normal);
				desc.getTangenteV().set(input.top_normal);
				break;
			case BoxLocalVariables.RIGHT:
				desc.getTangenteU().set(-input.front_normal.x,
			            	            -input.front_normal.y,
			            	            -input.front_normal.z);
				desc.getTangenteV().set(input.top_normal);
				break;
			}
		}
		
		// evaluate intersection uv coordinates
		if ( ((params & Intersections.EVALUATE_UV_COORDINATES)!=0)	) {

			switch(local.intersection_part) {
			case BoxLocalVariables.TOP:
				desc.getUVCoordinate().x = (1.0f+local.invers_point.x)/2.0f;
				desc.getUVCoordinate().y = (1.0f+local.invers_point.y)/2.0f;
				break;
			case BoxLocalVariables.BOTTOM:
				desc.getUVCoordinate().x = (1.0f+local.invers_point.x)/2.0f;
				desc.getUVCoordinate().y = (1.0f-local.invers_point.y)/2.0f;
				break;
			case BoxLocalVariables.FRONT:
				desc.getUVCoordinate().x = (1.0f+local.invers_point.x)/2.0f;
				desc.getUVCoordinate().y = (1.0f+local.invers_point.z)/2.0f;
				break;
			case BoxLocalVariables.BACK:
				desc.getUVCoordinate().x = (1.0f+local.invers_point.x)/2.0f;
				desc.getUVCoordinate().y = (1.0f-local.invers_point.z)/2.0f;
				break;
			case BoxLocalVariables.LEFT:
				desc.getUVCoordinate().x = (1.0f-local.invers_point.y)/2.0f;
				desc.getUVCoordinate().y = (1.0f+local.invers_point.z)/2.0f;
				break;
			case BoxLocalVariables.RIGHT:
				desc.getUVCoordinate().x = (1.0f+local.invers_point.y)/2.0f;
				desc.getUVCoordinate().y = (1.0f+local.invers_point.z)/2.0f;
				break;
			}

		}
	}
	
	
	public static boolean getSolidCylinder_T(
			CylinderInput input, 
			ObjectOutput output, 
			CylinderLocalVariables local) {
		
		output.hasIntersection = false;
		
		input.ray.transform(input.invers_transformation,local.invers_ray);

		if (input.minIndex==0) {
			local.t = Float.MAX_VALUE;
		} else {
			local.t = 0.0f;
		}		
		
		// body
		local.a = local.invers_ray.getDirection().x*local.invers_ray.getDirection().x +
				  local.invers_ray.getDirection().y*local.invers_ray.getDirection().y;
		local.b = 2.0f*local.invers_ray.getDirection().x*local.invers_ray.getOrigin().x +
			      2.0f*local.invers_ray.getDirection().y*local.invers_ray.getOrigin().y;
		local.c = local.invers_ray.getOrigin().x*local.invers_ray.getOrigin().x+
				  local.invers_ray.getOrigin().y*local.invers_ray.getOrigin().y-
				  CylinderLocalVariables.CYLINDER_RADIUS_SQ;
		local.D = local.b*local.b - 4.0f*local.a*local.c;
		if (local.D>=0.0f) {
			if (input.minIndex==0) {
				local.invers_t = (-local.b-(float)Math.sqrt(local.D))*0.5f/local.a;
			} else {
				local.invers_t = (-local.b+(float)Math.sqrt(local.D))*0.5f/local.a;
			}
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			if ((local.cur_invers_point.z<=CylinderLocalVariables.CYLINDER_HEIGHT) && 
				(local.cur_invers_point.z>=0.0f) &&
				(local.invers_t>0.0f)) {
				
				// -> intersection with body
				local.intersection_part = CylinderLocalVariables.BODY;
				local.t = local.invers_t/input.ray.getDirection().length();
				local.invers_point.set(local.cur_invers_point);
				output.hasIntersection = true;
				output.t = local.t;
				
			}
		}
		
		// top
		if (local.invers_ray.getDirection().z!=0.0f) {
			local.invers_t = (CylinderLocalVariables.CYLINDER_HEIGHT-
					         local.invers_ray.getOrigin().z)/
							 local.invers_ray.getDirection().z;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			if ((local.cur_invers_point.x*local.cur_invers_point.x+
				 local.cur_invers_point.y*local.cur_invers_point.y)<=CylinderLocalVariables.CYLINDER_RADIUS_SQ) {
				
				local.cur_t = local.invers_t/input.ray.getDirection().length();
			
				if (local.cur_t>=0.0f) {
					if (((local.cur_t<local.t)&&(input.minIndex==0))||
						((local.cur_t>local.t)&&(input.minIndex==1))) {
						
						// -> intersection with cap
						local.intersection_part = CylinderLocalVariables.TOP;
						local.t = local.cur_t;
						local.invers_point.set(local.cur_invers_point);
						output.hasIntersection = true;
						output.t = local.t;
						
					}
				}
			}
		}	
		
		// bottom
		if (local.invers_ray.getDirection().z!=0.0f) {
			local.invers_t = (-local.invers_ray.getOrigin().z)/
			 				 local.invers_ray.getDirection().z;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			if ((local.cur_invers_point.x*local.cur_invers_point.x+
				 local.cur_invers_point.y*local.cur_invers_point.y)<=CylinderLocalVariables.CYLINDER_RADIUS_SQ) {
				
				local.cur_t = local.invers_t/input.ray.getDirection().length();
				
				if (local.cur_t>=0.0f) {
					if (((local.cur_t<local.t)&&(input.minIndex==0))||
						((local.cur_t>local.t)&&(input.minIndex==1))) {
						
						// -> intersection with base
						local.intersection_part = CylinderLocalVariables.BOTTOM;
						local.t = local.cur_t;
						local.invers_point.set(local.cur_invers_point);
						output.hasIntersection = true;
						output.t = local.t;
						
					}
				}
			}
		}	
		
		
		local.hasIntersection = output.hasIntersection;
		return output.hasIntersection;
	}
	
	
	public static boolean getCylinder_T(
			CylinderInput input, 
			ObjectOutput output, 
			CylinderLocalVariables local) {
		
		output.hasIntersection = false;
		
		input.ray.transform(input.invers_transformation,local.invers_ray);

		local.t = Float.MAX_VALUE;
		
		// body
		local.a = local.invers_ray.getDirection().x*local.invers_ray.getDirection().x +
				  local.invers_ray.getDirection().y*local.invers_ray.getDirection().y;
		local.b = 2.0f*local.invers_ray.getDirection().x*local.invers_ray.getOrigin().x +
			      2.0f*local.invers_ray.getDirection().y*local.invers_ray.getOrigin().y;
		local.c = local.invers_ray.getOrigin().x*local.invers_ray.getOrigin().x+
				  local.invers_ray.getOrigin().y*local.invers_ray.getOrigin().y-
				  CylinderLocalVariables.CYLINDER_RADIUS_SQ;
		local.D = local.b*local.b - 4.0f*local.a*local.c;
		if (local.D>=0.0f) {
			local.invers_t = (-local.b-(float)Math.sqrt(local.D))*0.5f/local.a;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			if ((local.cur_invers_point.z<=CylinderLocalVariables.CYLINDER_HEIGHT) && 
				(local.cur_invers_point.z>=0.0f) &&
				(local.invers_t>0.0f)) {
								
				local.intersection_part = CylinderLocalVariables.BODY;
				local.t = local.invers_t/input.ray.getDirection().length();
				local.invers_point.set(local.cur_invers_point);
				output.hasIntersection = true;
				output.t = local.t;
								
			}
			if (!output.hasIntersection) {	
				// ... try second intersection point 
				local.invers_t = (-local.b+(float)Math.sqrt(local.D))*0.5f/local.a;
				local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
				if ((local.cur_invers_point.z<=CylinderLocalVariables.CYLINDER_HEIGHT) && 
					(local.cur_invers_point.z>=0.0f) &&
					(local.invers_t>0.0f)) {
					
					local.intersection_part = CylinderLocalVariables.BODY;
					local.t = local.invers_t/input.ray.getDirection().length();
					local.invers_point.set(local.cur_invers_point);
					output.hasIntersection = true;
					output.t = local.t;
										
				} else {
					// no intersection
					output.hasIntersection = false;
				}
			}
		} else {
			// no intersection
			output.hasIntersection = false;
		}
		
			
		// top
		if (!input.open_top && (local.invers_ray.getDirection().z!=0.0f)) {
			local.invers_t = (CylinderLocalVariables.CYLINDER_HEIGHT-
					          	  local.invers_ray.getOrigin().z)/
							 local.invers_ray.getDirection().z;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			if ((local.cur_invers_point.x*local.cur_invers_point.x+
				 local.cur_invers_point.y*local.cur_invers_point.y)<=CylinderLocalVariables.CYLINDER_RADIUS_SQ) {
				
				local.cur_t = local.invers_t/input.ray.getDirection().length();
				if ((local.cur_t>0.0f) && (local.cur_t<local.t)) {

					local.intersection_part = CylinderLocalVariables.TOP;
					local.t = local.cur_t;
					local.invers_point.set(local.cur_invers_point);
					output.hasIntersection = true;
					output.t = local.t;

				}
			}
		}
		
		
		// bottom
		if (!input.open_bottom && (local.invers_ray.getDirection().z!=0.0f)) {
			local.invers_t = (-local.invers_ray.getOrigin().z)/
							 local.invers_ray.getDirection().z;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			
			if ((local.cur_invers_point.x*local.cur_invers_point.x+
				 local.cur_invers_point.y*local.cur_invers_point.y)<=CylinderLocalVariables.CYLINDER_RADIUS_SQ) {
				local.cur_t = local.invers_t/input.ray.getDirection().length();
				if ((local.cur_t>0.0f) && (local.cur_t<local.t)) {

					local.intersection_part = CylinderLocalVariables.BOTTOM;
					local.t = local.cur_t;
					local.invers_point.set(local.cur_invers_point);
					output.hasIntersection = true;
					output.t = local.t;

				}
			}
		}
		
		local.hasIntersection = output.hasIntersection;
		return output.hasIntersection;
	}
	
	
	
	/**
	 * This method calculates different intersection values. 
	 * 
	 * !IMPORTANT! 
	 * It is required that the method getCylinder_T was executed previously. 
	 */
	public static void getCylinder_IntersectionDescription(
			CylinderInput input,
			int params,
			IntersectionDescription desc,
			CylinderLocalVariables local) {
		
		if (!local.hasIntersection) {
			desc.setIntersectionCount(0);
			return;
		} else {
			desc.setIntersectionCount(1);
		}
		
		// evaluate intersection point
		if ( ((params & Intersections.EVALUATE_POINT)!=0) ||
			 ((params & Intersections.EVALUATE_NORMAL)!=0) ||
			 ((params & Intersections.EVALUATE_TANGET_VECTORS)!=0) ) {
		
			local.point.scaleAdd(local.t,input.ray.getDirection(),input.ray.getOrigin());
			desc.setPoint(local.point,local.t);
			desc.setLocalPoint(local.invers_point);
		}
		
		// evaluate intersection normal vector
		if ( ((params & Intersections.EVALUATE_NORMAL)!=0) ||
			 ((params & Intersections.EVALUATE_TANGET_VECTORS)!=0) ) {
			switch(local.intersection_part) {
			case CylinderLocalVariables.TOP:
				desc.getNormal().set(input.top_normal);
				break;
			case CylinderLocalVariables.BOTTOM:
				desc.getNormal().set(-input.top_normal.x,
						             -input.top_normal.y,
						             -input.top_normal.z);
				break;
			case CylinderLocalVariables.BODY:
				desc.getNormal().set(local.invers_point.x,local.invers_point.y,0.0f);
				input.transformation.transform(desc.getNormal());
				desc.getNormal().normalize();
				break;
			}
			
		}
		
		// evaluate intersection tangent vectors
		if ( ((params & Intersections.EVALUATE_TANGET_VECTORS)!=0)	) {
			switch(local.intersection_part) {
			case CylinderLocalVariables.TOP:
				desc.getTangenteU().set(input.top_tangenteU);
				desc.getTangenteV().set(input.top_tangenteV);
				break;
			case CylinderLocalVariables.BOTTOM:
				desc.getTangenteU().set(input.top_tangenteU);
				desc.getTangenteV().set(-input.top_tangenteV.x,
						                -input.top_tangenteV.y,
						                -input.top_tangenteV.z);
				break;
			case CylinderLocalVariables.BODY:
				desc.getTangenteU().set(input.top_normal);
				desc.getTangenteV().cross(desc.getTangenteU(),desc.getNormal());
				break;
			}
		}
		
		// evaluate intersection uv coordinates
		if ( ((params & Intersections.EVALUATE_UV_COORDINATES)!=0)	) {

			switch(local.intersection_part) {
			case CylinderLocalVariables.TOP:
				local.u = (1.0f+local.invers_point.x)/2.0f;
				local.v = (1.0f+local.invers_point.y)/2.0f;
				break;
			case CylinderLocalVariables.BOTTOM:
				local.u = (1.0f+local.invers_point.x)/2.0f;
				local.v = (1.0f-local.invers_point.y)/2.0f;
				break;
			case CylinderLocalVariables.BODY:
				if (local.invers_point.y>=0.0) {
					local.beta = (float)Math.acos(local.invers_point.x);
					local.u = local.beta/FLOAT_PI*0.5f;
				} else {
					local.beta = (float)Math.acos(-local.invers_point.x);
					local.u = 0.5f+local.beta/FLOAT_PI*0.5f;
				}
				local.v = local.invers_point.z;
				break;
			}

			desc.getUVCoordinate().set(local.u,local.v);			
		}
			
	}
	
	
	public static boolean getSolidCone_T(
			ConeInput input, 
			ObjectOutput output, 
			ConeLocalVariables local) {
		
		output.hasIntersection = false;
		
		input.ray.transform(input.invers_transformation,local.invers_ray);
		
		if (input.minIndex==0) {
			local.t = Float.MAX_VALUE;
		} else {
			local.t = 0.0f;
		}
				
		// body
		local.a = local.invers_ray.getDirection().x*local.invers_ray.getDirection().x +
				  local.invers_ray.getDirection().y*local.invers_ray.getDirection().y -
				  local.invers_ray.getDirection().z*local.invers_ray.getDirection().z;
		local.b = 2.0f*local.invers_ray.getDirection().x*local.invers_ray.getOrigin().x +
			      2.0f*local.invers_ray.getDirection().y*local.invers_ray.getOrigin().y +
			      2.0f*local.invers_ray.getDirection().z*
			      	  (1.0f-local.invers_ray.getOrigin().z);
		local.c = local.invers_ray.getOrigin().x*local.invers_ray.getOrigin().x+
				  local.invers_ray.getOrigin().y*local.invers_ray.getOrigin().y-
				  (1.0f-local.invers_ray.getOrigin().z)*(1.0f-local.invers_ray.getOrigin().z);
		local.D = local.b*local.b - 4.0f*local.a*local.c;
		if (local.D>=0.0f) {
			if (input.minIndex==0) {
				local.invers_t = (-local.b-(float)Math.sqrt(local.D))*0.5f/local.a;
			} else {
				local.invers_t = (-local.b+(float)Math.sqrt(local.D))*0.5f/local.a;
			}
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			if ((local.cur_invers_point.z<ConeLocalVariables.CONE_HEIGHT) && 
				(local.cur_invers_point.z>=0.0f) &&
				(local.invers_t>0.0f)) {
				
				// -> intersection with body
				local.intersection_part = ConeLocalVariables.BODY;
				local.t = local.invers_t/input.ray.getDirection().length();
				local.invers_point.set(local.cur_invers_point);
				output.hasIntersection = true;
				output.t = local.t;
				
			}
		}
		
		// bottom
		if (local.invers_ray.getDirection().z!=0.0f) {
			local.invers_t = (-local.invers_ray.getOrigin().z)/
							 local.invers_ray.getDirection().z;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			if ((local.cur_invers_point.x*local.cur_invers_point.x+
				 local.cur_invers_point.y*local.cur_invers_point.y)<=ConeLocalVariables.CONE_RADIUS_SQ) {
				local.cur_t = local.invers_t/input.ray.getDirection().length();
				if (local.cur_t>0.0f) {
					if (((local.cur_t<local.t)&&(input.minIndex==0))||
						((local.cur_t>local.t)&&(input.minIndex==1))) {
						
						// -> intersection with basis
						local.intersection_part = ConeLocalVariables.BOTTOM;
						local.t = local.cur_t;
						local.invers_point.set(local.cur_invers_point);
						output.hasIntersection = true;
						output.t = local.t;
						
					}
				}
			}
		}
		
		local.hasIntersection = output.hasIntersection;
		return output.hasIntersection;
	}
	
	
	public static boolean getCone_T(
			ConeInput input, 
			ObjectOutput output, 
			ConeLocalVariables local) {
		
		output.hasIntersection = false;

		input.ray.transform(input.invers_transformation,local.invers_ray);
		
		local.t = Float.MAX_VALUE;
	
		// body
		local.a = local.invers_ray.getDirection().x*local.invers_ray.getDirection().x +
				  local.invers_ray.getDirection().y*local.invers_ray.getDirection().y -
				  local.invers_ray.getDirection().z*local.invers_ray.getDirection().z;
		local.b = 2.0f*local.invers_ray.getDirection().x*local.invers_ray.getOrigin().x +
			      2.0f*local.invers_ray.getDirection().y*local.invers_ray.getOrigin().y +
			      2.0f*local.invers_ray.getDirection().z*
			      	  (1.0f-local.invers_ray.getOrigin().z);
		local.c = local.invers_ray.getOrigin().x*local.invers_ray.getOrigin().x+
				  local.invers_ray.getOrigin().y*local.invers_ray.getOrigin().y-
				  (1.0f-local.invers_ray.getOrigin().z)*(1.0f-local.invers_ray.getOrigin().z);
		local.D = local.b*local.b - 4.0f*local.a*local.c;
		if (local.D>=0.0f) {
			local.invers_t = (-local.b-(float)Math.sqrt(local.D))*0.5f/local.a;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			if ((local.cur_invers_point.z<ConeLocalVariables.CONE_HEIGHT) && 
				(local.cur_invers_point.z>=0.0f) &&
				(local.invers_t>0.0f)) {
				
				local.intersection_part = ConeLocalVariables.BODY;
				local.t = local.invers_t/input.ray.getDirection().length();
				local.invers_point.set(local.cur_invers_point);
				output.hasIntersection = true;
				output.t = local.t;
				
			}
			if (!output.hasIntersection) {	
				// ... try second intersection point 
				local.invers_t = (-local.b+(float)Math.sqrt(local.D))*0.5f/local.a;
				local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
				if ((local.cur_invers_point.z<ConeLocalVariables.CONE_HEIGHT) && 
					(local.cur_invers_point.z>=0.0f) &&
					(local.invers_t>0.0f)) {
					
					local.intersection_part = ConeLocalVariables.BODY;
					local.t = local.invers_t/input.ray.getDirection().length();
					local.invers_point.set(local.cur_invers_point);
					output.hasIntersection = true;
					output.t = local.t;
					
				} else {
					// no intersection
					output.hasIntersection = false;
				}
			}
		} else {
			// no intersection
			output.hasIntersection = false;
		}
		
		
		// bottom
		// >
		if (!input.open_bottom && (local.invers_ray.getDirection().z!=0.0f)) {
			local.invers_t = (-local.invers_ray.getOrigin().z)/
							 local.invers_ray.getDirection().z;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			if ((local.cur_invers_point.x*local.cur_invers_point.x+
				 local.cur_invers_point.y*local.cur_invers_point.y)<=ConeLocalVariables.CONE_RADIUS_SQ) {
				local.cur_t = local.invers_t/input.ray.getDirection().length();
				if ((local.cur_t>0.0f) && (local.cur_t<local.t)) {
					
					local.intersection_part = ConeLocalVariables.BOTTOM;
					local.t = local.cur_t;
					local.invers_point.set(local.cur_invers_point);
					output.hasIntersection = true;
					output.t = local.t;				
					
				}
			}
		}
		
		local.hasIntersection = output.hasIntersection;
		return output.hasIntersection;
	}
	
	
	/**
	 * This method calculates different intersection values. 
	 * 
	 * !IMPORTANT! 
	 * It is required that the method getCone_T was executed previously. 
	 */
	public static void getCone_IntersectionDescription(
			ConeInput input,
			int params,
			IntersectionDescription desc,
			ConeLocalVariables local) {
		
		if (!local.hasIntersection) {
			desc.setIntersectionCount(0);
			return;
		} else {
			desc.setIntersectionCount(1);
		}
		
		// evaluate intersection point
		if ( ((params & Intersections.EVALUATE_POINT)!=0) ||
			 ((params & Intersections.EVALUATE_NORMAL)!=0) ||
			 ((params & Intersections.EVALUATE_TANGET_VECTORS)!=0) ) {
		
			local.point.scaleAdd(local.t,input.ray.getDirection(),input.ray.getOrigin());
			desc.setPoint(local.point,local.t);
			desc.setLocalPoint(local.invers_point);
		}
		
		// evaluate intersection normal vector
		if ( ((params & Intersections.EVALUATE_NORMAL)!=0) ||
			 ((params & Intersections.EVALUATE_TANGET_VECTORS)!=0) ||
			 ((params & Intersections.EVALUATE_UV_COORDINATES)!=0) ) {
			switch(local.intersection_part) {
			case ConeLocalVariables.BOTTOM:
				desc.getNormal().set(input.bottom_normal);
				break;
			case ConeLocalVariables.BODY:
				desc.getNormal().set(local.invers_point.x,local.invers_point.y,0.0f);
				desc.getNormal().normalize();
				desc.getNormal().z = 1.0f;
				input.transformation.transform(desc.getNormal());
				desc.getNormal().normalize();
				break;
			}
		}
		
		// evaluate intersection tangent vectors
		if ( ((params & Intersections.EVALUATE_TANGET_VECTORS)!=0)	) {
			switch(local.intersection_part) {
			case ConeLocalVariables.BOTTOM:
				desc.getTangenteU().set(input.bottom_tangenteU);
				desc.getTangenteV().set(input.bottom_tangenteV);
				break;
			case ConeLocalVariables.BODY:
				desc.getTangenteV().set(0.0f,0.0f,1.0f);
				desc.getTangenteV().sub(desc.getPoint());
				desc.getTangenteV().normalize();
				desc.getTangenteU().cross(desc.getNormal(),desc.getTangenteV());
				break;
			}
		}
		
		// evaluate intersection uv coordinates
		if ( ((params & Intersections.EVALUATE_UV_COORDINATES)!=0)	) {

			switch(local.intersection_part) {
			case ConeLocalVariables.BOTTOM:
				local.u = (1.0f+local.invers_point.x)/2.0f;
				local.v = (1.0f-local.invers_point.y)/2.0f;
				break;
			case ConeLocalVariables.BODY:
				if (local.invers_point.y>=0.0) {
					local.beta = (float)Math.acos(local.invers_point.x/(1.0f-local.invers_point.z));
					local.u = local.beta/FLOAT_PI*0.5f;
				} else {
					local.beta = (float)Math.acos(-local.invers_point.x/(1.0f-local.invers_point.z));
					local.u = 0.5f+local.beta/FLOAT_PI*0.5f;
				}
				local.v = local.invers_point.z;
				break;
			}

			desc.getUVCoordinate().set(local.u,local.v);
		}
	}
	
	
	public static boolean getSolidFrustum_T(
			FrustumInput input, 
			ObjectOutput output, 
			FrustumLocalVariables local) {
		
		output.hasIntersection = false;
		
		input.ray.transform(input.invers_transformation,local.invers_ray);
		
		if (input.minIndex==0) {
			local.t = Float.MAX_VALUE;
		} else {
			local.t = 0.0f;
		}
					
		// body
		if (input.radius_ratio==1.0f) {
			local.a = local.invers_ray.getDirection().x*local.invers_ray.getDirection().x +
					  local.invers_ray.getDirection().y*local.invers_ray.getDirection().y;
			local.b = 2.0f*local.invers_ray.getDirection().x*local.invers_ray.getOrigin().x +
			          2.0f*local.invers_ray.getDirection().y*local.invers_ray.getOrigin().y;
			local.c = local.invers_ray.getOrigin().x*local.invers_ray.getOrigin().x+
					  local.invers_ray.getOrigin().y*local.invers_ray.getOrigin().y-
					  CylinderLocalVariables.CYLINDER_RADIUS_SQ;
		} else {
			local.gz  = local.invers_ray.getOrigin().z*(1.0f-input.radius_ratio);
			local.dgz = local.invers_ray.getDirection().z*(1.0f-input.radius_ratio);
			local.a = local.invers_ray.getDirection().x*local.invers_ray.getDirection().x +
			  	      local.invers_ray.getDirection().y*local.invers_ray.getDirection().y -
			  	      local.dgz*local.dgz;
			local.b = 2.0f*local.invers_ray.getDirection().x*local.invers_ray.getOrigin().x +
		      		  2.0f*local.invers_ray.getDirection().y*local.invers_ray.getOrigin().y +
		      		  2.0f*local.dgz*
		      		  (1.0f-local.gz);
			local.c = local.invers_ray.getOrigin().x*local.invers_ray.getOrigin().x+
			  		  local.invers_ray.getOrigin().y*local.invers_ray.getOrigin().y-
			  		  (1.0f-local.gz)*(1.0f-local.gz);
		}
		local.D = local.b*local.b - 4.0f*local.a*local.c;
		if (local.D>=0.0f) {
			if (input.minIndex==0) {
				local.invers_t = (-local.b-(float)Math.sqrt(local.D))*0.5f/local.a;
			} else {
				local.invers_t = (-local.b+(float)Math.sqrt(local.D))*0.5f/local.a;
			}
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			if ((local.cur_invers_point.z<=FrustumLocalVariables.FRUSTUM_HEIGHT) && 
				(local.cur_invers_point.z>=0.0f) &&
				(local.invers_t>0.0f)) {
				
				// -> intersection with body
				local.intersection_part = FrustumLocalVariables.BODY;
				local.t = local.invers_t/input.ray.getDirection().length();
				local.invers_point.set(local.cur_invers_point);
				output.hasIntersection = true;
				output.t = local.t;
				
			}
		}
		
		
		// top
		if (local.invers_ray.getDirection().z!=0.0f) {
			local.invers_t = (FrustumLocalVariables.FRUSTUM_HEIGHT-
					         local.invers_ray.getOrigin().z)/
							 local.invers_ray.getDirection().z;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			if ((local.cur_invers_point.x*local.cur_invers_point.x+
				 local.cur_invers_point.y*local.cur_invers_point.y)<=input.radius_ratio_sq) {
				
				local.cur_t = local.invers_t/input.ray.getDirection().length();
				if (local.cur_t>0.0f) {
					if (((local.cur_t<local.t)&&(input.minIndex==0))||
						((local.cur_t>local.t)&&(input.minIndex==1))) {
						
						// -> intersection with cap
						local.intersection_part = FrustumLocalVariables.TOP;
						local.t = local.cur_t;
						local.invers_point.set(local.cur_invers_point);
						output.hasIntersection = true;
						output.t = local.t;
						
					}
				}
			}
		}
		
		
		// bottom
		if (local.invers_ray.getDirection().z!=0.0f) {
			local.invers_t = (-local.invers_ray.getOrigin().z)/
							 local.invers_ray.getDirection().z;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			
			if ((local.cur_invers_point.x*local.cur_invers_point.x+
				 local.cur_invers_point.y*local.cur_invers_point.y)<=FrustumLocalVariables.FRUSTUM_BOTTOM_RADIUS_SQ) {
				local.cur_t = local.invers_t/input.ray.getDirection().length();
				if (local.cur_t>0.0f) {
					if (((local.cur_t<local.t)&&(input.minIndex==0))||
						((local.cur_t>local.t)&&(input.minIndex==1))) {
					
						// -> intersection with basis
						local.intersection_part = FrustumLocalVariables.BOTTOM;
						local.t = local.cur_t;
						local.invers_point.set(local.cur_invers_point);
						output.hasIntersection = true;
						output.t = local.t;
						
					}
				}
			}
		}
		
		local.hasIntersection = output.hasIntersection;
		return output.hasIntersection;
	}
	
	
	public static boolean getFrustum_T(
			FrustumInput input, 
			ObjectOutput output, 
			FrustumLocalVariables local) {
		
		output.hasIntersection = false;
		
		input.ray.transform(input.invers_transformation,local.invers_ray);
		
		local.t = Float.MAX_VALUE;
		
		// body
		if (input.radius_ratio==1.0f) {
			local.a = local.invers_ray.getDirection().x*local.invers_ray.getDirection().x +
					  local.invers_ray.getDirection().y*local.invers_ray.getDirection().y;
			local.b = 2.0f*local.invers_ray.getDirection().x*local.invers_ray.getOrigin().x +
			          2.0f*local.invers_ray.getDirection().y*local.invers_ray.getOrigin().y;
			local.c = local.invers_ray.getOrigin().x*local.invers_ray.getOrigin().x+
					  local.invers_ray.getOrigin().y*local.invers_ray.getOrigin().y-
					  CylinderLocalVariables.CYLINDER_RADIUS_SQ;
		} else {
			local.gz  = local.invers_ray.getOrigin().z*(1.0f-input.radius_ratio);
			local.dgz = local.invers_ray.getDirection().z*(1.0f-input.radius_ratio);
			local.a = local.invers_ray.getDirection().x*local.invers_ray.getDirection().x +
			  	      local.invers_ray.getDirection().y*local.invers_ray.getDirection().y -
			  	      local.dgz*local.dgz;
			local.b = 2.0f*local.invers_ray.getDirection().x*local.invers_ray.getOrigin().x +
		      		  2.0f*local.invers_ray.getDirection().y*local.invers_ray.getOrigin().y +
		      		  2.0f*local.dgz*
		      		  (1.0f-local.gz);
			local.c = local.invers_ray.getOrigin().x*local.invers_ray.getOrigin().x+
			  		  local.invers_ray.getOrigin().y*local.invers_ray.getOrigin().y-
			  		  (1.0f-local.gz)*(1.0f-local.gz);
		}
		local.D = local.b*local.b - 4.0f*local.a*local.c;
		if (local.D>=0.0f) {
			local.invers_t = (-local.b-(float)Math.sqrt(local.D))*0.5f/local.a;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			if ((local.cur_invers_point.z<=FrustumLocalVariables.FRUSTUM_HEIGHT) && 
				(local.cur_invers_point.z>=0.0f) &&
				(local.invers_t>0.0f)) {
				
					local.intersection_part = FrustumLocalVariables.BODY;
					local.t = local.invers_t/input.ray.getDirection().length();
					local.invers_point.set(local.cur_invers_point);
					output.hasIntersection = true;
					output.t = local.t;

			}
			if (!output.hasIntersection) {	
				// ... try second intersection point 
				local.invers_t = (-local.b+(float)Math.sqrt(local.D))*0.5f/local.a;
				local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
				if ((local.cur_invers_point.z<=FrustumLocalVariables.FRUSTUM_HEIGHT) && 
					(local.cur_invers_point.z>=0.0f) &&
					(local.invers_t>0.0f)) {
					
					local.intersection_part = FrustumLocalVariables.BODY;
					local.t = local.invers_t/input.ray.getDirection().length();
					local.invers_point.set(local.cur_invers_point);
					output.hasIntersection = true;
					output.t = local.t;
			
				} else {
					// no intersection
					output.hasIntersection = false;
				}
			}
		} else {
			// no intersection
			output.hasIntersection = false;
		}
		
		
		// top
		if (!input.open_top && (local.invers_ray.getDirection().z!=0.0f)) {
			local.invers_t = (FrustumLocalVariables.FRUSTUM_HEIGHT-
					          	  local.invers_ray.getOrigin().z)/
							 local.invers_ray.getDirection().z;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			if ((local.cur_invers_point.x*local.cur_invers_point.x+
				 local.cur_invers_point.y*local.cur_invers_point.y)<=input.radius_ratio_sq) {
				
				local.cur_t = local.invers_t/input.ray.getDirection().length();
				if ((local.cur_t>0.0f) && (local.cur_t<local.t)) {
					
					local.intersection_part = FrustumLocalVariables.TOP;
					local.t = local.cur_t;
					local.invers_point.set(local.cur_invers_point);
					output.hasIntersection = true;
					output.t = local.t;

				}
			}
		}
		
		// bottom
		if (!input.open_bottom && (local.invers_ray.getDirection().z!=0.0f)) {
			local.invers_t = (-local.invers_ray.getOrigin().z)/
							 local.invers_ray.getDirection().z;
			local.cur_invers_point.scaleAdd(local.invers_t,local.invers_ray.getDirection(),local.invers_ray.getOrigin());
			
			if ((local.cur_invers_point.x*local.cur_invers_point.x+
				 local.cur_invers_point.y*local.cur_invers_point.y)<=FrustumLocalVariables.FRUSTUM_BOTTOM_RADIUS_SQ) {
				local.cur_t = local.invers_t/input.ray.getDirection().length();
				if ((local.cur_t>0.0f) && (local.cur_t<local.t)) {
					
					local.intersection_part = FrustumLocalVariables.BOTTOM;
					local.t = local.cur_t;
					local.invers_point.set(local.cur_invers_point);
					output.hasIntersection = true;
					output.t = local.t;

				}
			}
		}
		
		local.hasIntersection = output.hasIntersection;
		return output.hasIntersection;
	}
	
	
	/**
	 * This method calculates different intersection values. 
	 * 
	 * !IMPORTANT! 
	 * It is required that the method getFrustum_T was executed previously. 
	 */
	public static void getFrustum_IntersectionDescription(
			FrustumInput input,
			int params,
			IntersectionDescription desc,
			FrustumLocalVariables local) {
		
		if (!local.hasIntersection) {
			desc.setIntersectionCount(0);
			return;
		} else {
			desc.setIntersectionCount(1);
		}
		
		// evaluate intersection point
		if ( ((params & Intersections.EVALUATE_POINT)!=0) ||
			 ((params & Intersections.EVALUATE_NORMAL)!=0) ||
			 ((params & Intersections.EVALUATE_TANGET_VECTORS)!=0) ) {
		
			local.point.scaleAdd(local.t,input.ray.getDirection(),input.ray.getOrigin());
			desc.setPoint(local.point,local.t);
			desc.setLocalPoint(local.invers_point);
		}
		
		// evaluate intersection normal vector
		if ( ((params & Intersections.EVALUATE_NORMAL)!=0) ||
			 ((params & Intersections.EVALUATE_TANGET_VECTORS)!=0) ) {
			switch(local.intersection_part) {
			case FrustumLocalVariables.TOP:
				desc.getNormal().set(input.top_normal);
				break;
			case FrustumLocalVariables.BOTTOM:
				desc.getNormal().set(-input.top_normal.x,
						             -input.top_normal.y,
						             -input.top_normal.z);
				break;
			case FrustumLocalVariables.BODY:
				desc.getNormal().set(local.invers_point.x,local.invers_point.y,0.0f);
				desc.getNormal().normalize();
				desc.getNormal().z = (1.0f-input.radius_ratio);
				input.transformation.transform(desc.getNormal());
				desc.getNormal().normalize();
				break;
			}	
		}
		
		// evaluate intersection tangent vectors
		if ( ((params & Intersections.EVALUATE_TANGET_VECTORS)!=0)	) {
			switch(local.intersection_part) {
			case FrustumLocalVariables.TOP:
				desc.getTangenteU().set(input.top_tangenteU);
				desc.getTangenteV().set(input.top_tangenteV);
				break;
			case FrustumLocalVariables.BOTTOM:
				desc.getTangenteU().set(input.top_tangenteU);
				desc.getTangenteV().set(-input.top_tangenteV.x,
										-input.top_tangenteV.y,
										-input.top_tangenteV.z);
				break;
			case FrustumLocalVariables.BODY:
				if (input.radius_ratio==1.0f) {
					desc.getTangenteU().set(input.top_normal);
					desc.getTangenteV().cross(desc.getTangenteU(),desc.getNormal());
				} else {
					desc.getTangenteV().set(0.0f,0.0f,1.0f/(1.0f-input.radius_ratio));
					desc.getTangenteV().sub(desc.getPoint());
					desc.getTangenteV().normalize();
					desc.getTangenteU().cross(desc.getNormal(),desc.getTangenteV());
				}				
				break;
			}
		}
		
		// evaluate intersection uv coordinates
		if ( ((params & Intersections.EVALUATE_UV_COORDINATES)!=0)	) {
			switch(local.intersection_part) {
			case FrustumLocalVariables.TOP:
				desc.getUVCoordinate().x = (1.0f+local.invers_point.x)/2.0f;
				desc.getUVCoordinate().y = (1.0f+local.invers_point.y)/2.0f;
				break;
			case FrustumLocalVariables.BOTTOM:
				desc.getUVCoordinate().x = (1.0f+local.invers_point.x)/2.0f;
				desc.getUVCoordinate().y = (1.0f-local.invers_point.y)/2.0f;
				break;
			case FrustumLocalVariables.BODY:
				if (local.invers_point.y>=0.0) {
					local.beta = (float)Math.acos(local.invers_point.x/(1.0f-(1.0f-input.radius_ratio)*local.invers_point.z));
					desc.getUVCoordinate().x = local.beta/FLOAT_PI*0.5f;
				} else {
					local.beta = (float)Math.acos(-local.invers_point.x/(1.0f-(1.0f-input.radius_ratio)*local.invers_point.z));
					desc.getUVCoordinate().x = 0.5f+local.beta/FLOAT_PI*0.5f;
				}
				desc.getUVCoordinate().y = local.invers_point.z;
				break;
			}
		}
		
	}
	
	
	public static class SphereIntersectionInput {
		public Ray      ray = new Ray();
		public Matrix4f invers_transformation = new Matrix4f();
		public float    squareRadius;
	}
	
	
	public static class SphereIntersectionLocalVariables {
		
		public Ray      invers_ray = new Ray();
		public Vector3f dg = new Vector3f();
		public Vector3f g = new Vector3f();
		public float    a;
		public float    b;
		public float    c;
		public float    D;	
		public float    invers_t;
	}
	
	
	public static class BoxIntersectionInput {
		public final Ray      ray = new Ray();
		public final Vector3f minValues = new Vector3f();
		public final Vector3f maxValues = new Vector3f();
	}
	
	
	public static class BoxIntersectionLocalVariables {
		public float    t;
		public Point3f  point = new Point3f();
	}
		
	
	public static class ObjectOutput {
		public boolean hasIntersection;
		public float   t;
	}
	
	
	

	public static class SphereInput {
		public Ray      ray = new Ray();
		public Matrix4f transformation = new Matrix4f();
		public Matrix4f invers_transformation = new Matrix4f();
		public int      minIndex = 0;
		
		public float    radius;
		public float    squareRadius;
	}
	
	

	/**
	 * This class is used to speed up the sphere intersection calculation.
	 * An other advantage of it is to split the whole intersection calculation
	 * an only run the second part if necessary.
	 */
	public static class SphereLocalVariables {
		
		public final RTShader.TransparencyInput transparencyInput =
			new RTShader.TransparencyInput();
		public float    u;
		public float    v;
		
		public Ray      invers_ray = new Ray();
		public Vector3f dg = new Vector3f();
		public Vector3f g = new Vector3f();
		public float    a;
		public float    b;
		public float    c;
		public float    D;
		public float    invers_t;
		public float    t;
		public int      t_index;
		
		public Point3f  invers_point = new Point3f();
		public Point3f  point = new Point3f();
		public Point3f  center = new Point3f();
		public Vector3f normal = new Vector3f();
		public float    x = 0.0f;
		public float    z = 0.0f;
	}
	
	
	public static class PlaneInput {
		public Ray      ray = new Ray();
		public Matrix4f transformation = new Matrix4f();
		public Matrix4f invers_transformation = new Matrix4f();
		public RTShader transparencyShader;
		
		public Vector3f normal = new Vector3f();
		public Vector3f tangenteU = new Vector3f();
		public Vector3f tangenteV = new Vector3f();
	}
		
	
	public static class PlaneLocalVariables {
		
		public final RTShader.TransparencyInput transparencyInput =
			new RTShader.TransparencyInput();
		
		public Ray      invers_ray = new Ray();
		public float    invers_t;
		public float    t;
		public boolean  hasIntersection;
		
		public Point3f  invers_point = new Point3f();
		public Point3f  point = new Point3f();
	}
	
	
	public static class ParallelogramInput {
		public final Ray      ray = new Ray();
		public final Matrix4f transformation = new Matrix4f();
		public final Matrix4f invers_transformation = new Matrix4f(); 
		public RTShader       transparencyShader;
		
		public final Vector3f normal = new Vector3f();
		public final Vector3f tangenteU = new Vector3f();
		public final Vector3f tangenteV = new Vector3f();
	}
		
	
	public static class ParallelogramLocalVariables {
		
		public final RTShader.TransparencyInput transparencyInput =
			new RTShader.TransparencyInput();

		public final Ray invers_ray = new Ray();
		public float     invers_t;
		public float     t;
		public boolean   hasIntersection;
		public Point3f   invers_point = new Point3f();
		
		public Point3f  point = new Point3f();
	}
	
	
	public static class BoxInput {
		public final Ray      ray = new Ray();
		public final Matrix4f transformation = new Matrix4f();
		public final Matrix4f invers_transformation = new Matrix4f();
		public int            minIndex = 0;
		
		public final Vector3f top_normal = new Vector3f();
		public final Vector3f front_normal = new Vector3f();
		public final Vector3f right_normal = new Vector3f();
		public float          expansion_x;
		public float          expansion_y;
		public float          expansion_z;
	}
	
	
	public static class BoxLocalVariables {
		
		public final static short TOP    = 0;
		public final static short BOTTOM = 1;
		public final static short FRONT  = 2;
		public final static short BACK   = 3;
		public final static short LEFT   = 4;
		public final static short RIGHT  = 5;
		
		public final RTShader.TransparencyInput transparencyInput =
			new RTShader.TransparencyInput();
		public float     u;
		public float     v;
		
		public final Ray invers_ray = new Ray();
		public float     invers_t;
		public float     cur_t;
		public float     t;
		public boolean   hasIntersection;
		public short     intersection_part;
		public Point3f   invers_point = new Point3f();
		public Point3f   cur_invers_point = new Point3f();
		
		public Point3f  point = new Point3f();
	}
	
	
	public static class CylinderInput {
		public final Vector3f top_normal = new Vector3f();
		public final Vector3f top_tangenteU = new Vector3f();
		public final Vector3f top_tangenteV = new Vector3f();
		public boolean        open_top;
		public boolean        open_bottom;
		public final Ray      ray = new Ray();
		public final Matrix4f transformation = new Matrix4f();
		public final Matrix4f invers_transformation = new Matrix4f();
		public int            minIndex = 0;
	}
	
	
	public static class CylinderLocalVariables {
		
		public final static float CYLINDER_HEIGHT    = 1.0f;
		public final static float CYLINDER_RADIUS    = 1.0f;
		public final static float CYLINDER_RADIUS_SQ = 1.0f;
		public final static short TOP    = 0;
		public final static short BOTTOM = 1;
		public final static short BODY   = 2;
		
		public final RTShader.TransparencyInput transparencyInput =
			new RTShader.TransparencyInput();
		public float    u;
		public float    v;
		
		public final Ray invers_ray = new Ray();
		public float     invers_t;
		public float     cur_t;
		public float     t;
		public boolean   hasIntersection;
		public short     intersection_part;
		public Point3f   cur_invers_point = new Point3f();
		public Point3f   invers_point = new Point3f();
		public float     a;
		public float     b;
		public float     c;
		public float     D;
		
		public Point3f   point = new Point3f();
		public float     beta;
	}
	
	
	public static class ConeInput {
		public final Ray      ray = new Ray();
		public final Matrix4f transformation = new Matrix4f();
		public final Matrix4f invers_transformation = new Matrix4f();
		public int            minIndex = 0;
		
		public boolean        open_bottom;
		public final Vector3f bottom_normal = new Vector3f();
		public final Vector3f bottom_tangenteU = new Vector3f();
		public final Vector3f bottom_tangenteV = new Vector3f();
	}
	
	
	public static class ConeLocalVariables {
		
		public final static float CONE_HEIGHT    = 1.0f;
		public final static float CONE_RADIUS_SQ = 1.0f;
		public final static short BOTTOM = 0;
		public final static short BODY   = 1;
		
		public final RTShader.TransparencyInput transparencyInput =
			new RTShader.TransparencyInput();
		public float    u;
		public float    v;
		
		public final Ray invers_ray = new Ray();
		public float     invers_t;
		public float     t;
		public float     cur_t;
		public boolean   hasIntersection;
		public short     intersection_part;
		public Point3f   cur_invers_point = new Point3f();
		public Point3f   invers_point = new Point3f();
		public float     a;
		public float     b;
		public float     c;
		public float     D;
		
		public Point3f   point = new Point3f();
		public float     beta;
	}
	
	
	public static class FrustumInput {
		public final Ray      ray = new Ray();
		public final Matrix4f transformation = new Matrix4f();
		public final Matrix4f invers_transformation = new Matrix4f();
		public int            minIndex = 0;
		
		public float          radius_ratio;
		public float          radius_ratio_sq;
		public boolean        open_top;
		public boolean        open_bottom;
		public final Vector3f top_normal = new Vector3f();
		public final Vector3f top_tangenteU = new Vector3f();
		public final Vector3f top_tangenteV = new Vector3f();
		
	}
	
	
	public static class FrustumLocalVariables {
		
		public final static float FRUSTUM_BOTTOM_RADIUS_SQ = 1.0f;
		public final static float FRUSTUM_HEIGHT = 1.0f;
		public final static short TOP    = 0;
		public final static short BOTTOM = 1;
		public final static short BODY   = 2;
		
		public final RTShader.TransparencyInput transparencyInput =
			new RTShader.TransparencyInput();
		public float    u;
		public float    v;
		
		public final Ray invers_ray = new Ray();
		public float     invers_t;
		public float     t;
		public float     cur_t;
		public boolean   hasIntersection;
		public short     intersection_part;
		public Point3f   cur_invers_point = new Point3f();
		public Point3f   invers_point = new Point3f();
		public float     gz;
		public float     dgz;
		public float     a;
		public float     b;
		public float     c;
		public float     D;
		
		public Point3f   point = new Point3f();
		public float     beta;
	}
		
}
