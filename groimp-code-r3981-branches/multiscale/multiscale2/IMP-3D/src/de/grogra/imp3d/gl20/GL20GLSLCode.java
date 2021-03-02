package de.grogra.imp3d.gl20;

import java.util.ArrayList;
import java.util.Stack;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

class GL20GLSLCode {
	/**
	 * indication bit for constants
	 */
	final public static int CONST_BIT = 0x8000;
	
	final public static String NORMAL_VARIABLE_NAME = new String("normal3_1");
	final public static String POSITION_WORLD_NAME = new String("vertexPosition3_World");
	final public static String POSITION_EYE_NAME = new String("vertexPosition3_Eye");
	final public static String WORLD_TO_VIEW_MATRIX_NAME = new String("worldToViewMatrix4");
	
	final public static int NORMAL_BIT = 0x1;
	final public static int POSITION_WORLD_BIT = 0x2;
	final public static int POSITION_EYE_BIT = 0x4;
	
	/**
	 * constant scalars
	 */
	private Stack<Float> constScalarStack = new Stack<Float>();
	
	/**
	 * constant vectors with 3 components
	 */
	private Stack<Vector3f> constVector3Stack = new Stack<Vector3f>();
	
	/**
	 * constant vectors with 4 components
	 */
	private Stack<Vector4f> constVector4Stack = new Stack<Vector4f>();
		
	/**
	 * temporary scalars
	 */
	private ArrayList<Boolean> tempScalars = new ArrayList<Boolean>();
	
	/**
	 * temporary vectors with 3 components
	 */
	private ArrayList<Boolean> tempVector3 = new ArrayList<Boolean>();
	
	/**
	 * temporary vectors with 4 components
	 */
	private ArrayList<Boolean> tempVector4 = new ArrayList<Boolean>();
	
	private int dependencies = 0;
	
	/**
	 * source code of the shader
	 */
	private String sourceCode = new String();
	
	public GL20GLSLCode() {
	}
	
	final public boolean isConstant(int index) {
		return ((index & CONST_BIT) != 0) ? true : false;
	}
	
	final public static String getDependencyName(int dependencyBit) {
		String returnValue = null;
		
		switch (dependencyBit) {
		case NORMAL_BIT:
			returnValue = NORMAL_VARIABLE_NAME;
			break;
		case POSITION_WORLD_BIT:
			returnValue = POSITION_WORLD_NAME;
			break;
		case POSITION_EYE_BIT:
			returnValue = POSITION_EYE_NAME;
			break;
		}
		
		return returnValue;
	}
	
	final public void setDependency(int dependencyBit) {
		dependencies |= dependencyBit;
	}
	
	final public int getDependency() {
		return dependencies;
	}
	
	final public String getConstScalarName(int index) {
		String returnValue = null;
		
		if (index < constScalarStack.size()) {
			returnValue = new String("constScalar_");
			returnValue += ((Integer)index).toString();
		}
		
		return returnValue;
	}
	
	final public int createConstScalar(float value) {
		// try to find this value
		final int constCount = constScalarStack.size();
		int index = 0;
		while (index < constCount) {
			if (constScalarStack.get(index) == value)
				break;
			else
				index++;
		}
		
		if (index >= constCount) {
			// this scalar doesn't exist; create it
			constScalarStack.add(new Float(value));
		}
		
		return index;
	}
	
	final public String getConstVector3Name(int index) {
		String returnValue = null;

		if (index < constVector3Stack.size()) {
			returnValue = new String("constVec3_");
			returnValue += ((Integer)index).toString();
		}
		
		return returnValue;
	}
	
	final public int createConstVector3(Vector3f vector3) {
		// try to find this vector
		final int constCount = constVector3Stack.size();
		int index = 0;
		while (index < constCount) {
			if (constVector3Stack.get(index).equals(vector3) == true)
				break;
			else
				index++;
		}
		
		if (index >= constCount) {
			// this vector3 doesn't exist; create it
			constVector3Stack.add(new Vector3f(vector3));
		}
		
		return index;
	}
	
	final public String getConstVector4Name(int index) {
		String returnValue = null;
		
		if (index < constVector4Stack.size()) {
			returnValue = new String("constVec4_");
			returnValue += ((Integer)index).toString();
		}
		
		return returnValue;
	}
	
	final public int createConstVector4(Vector4f vector4) {
		// try to find this vector
		final int constCount = constVector4Stack.size();
		int index = 0;
		while (index < constCount) {
			if (constVector4Stack.get(index).equals(vector4) == true)
				break;
			else
				index++;
		}
		
		if (index >= constCount) {
			// this vector4 doesn't exist; create it
			constVector4Stack.add(new Vector4f(vector4));
		}
		
		return index;
	}
	
	final public String getTemporaryScalarName(int index) {
		String returnValue = null;
		
		if (index < tempScalars.size()) {
			returnValue = new String("tempScalar_");
			returnValue += ((Integer)index).toString();
		}
		
		return returnValue;
	}
	
	final public int allocateTemporaryScalar() {
		final int tempCount = tempScalars.size();
		int index = 0;
		while (index < tempCount) {
			if (tempScalars.get(index).equals(Boolean.FALSE) == true)
				break;
			else
				index++;
		}
		
		if (index >= tempCount) {
			// no free temporary scalar found; create one
			tempScalars.add(new Boolean(Boolean.TRUE));
		}
		else {
			// free temporary scalar found; mark it as used
			Boolean tempScalar = tempScalars.get(index);
			tempScalar = Boolean.TRUE;
		}
		
		return index;
	}
	
	final public void freeTemporaryScalar(int index) {
		if ((index < tempScalars.size()) &&
			(tempScalars.get(index).equals(Boolean.TRUE) == true)) {
			// free temporary scalar
			Boolean tempScalar = tempScalars.get(index);
			tempScalar = Boolean.FALSE;
		}
	}
	
	final public String getTemporaryVector3Name(int index) {
		String returnValue = null;
		
		if (index < tempVector3.size()) {
			returnValue = new String("tempVector3_");
			returnValue += ((Integer)index).toString();
		}
		
		return returnValue;
	}
	
	final public int allocateTemporaryVector3() {
		final int tempCount = tempVector3.size();
		int index = 0;
		while (index < tempCount) {
			if (tempVector3.get(index).equals(Boolean.FALSE) == true)
				break;
			else
				index++;
		}
		
		if (index >= tempCount) {
			// no free temporary vector3 found; create one
			tempVector3.add(new Boolean(Boolean.TRUE));
		}
		else {
			// free temporary vector3 found; mark it as used
			Boolean tempVector = tempVector3.get(index);
			tempVector = Boolean.TRUE;
		}
		
		return index;
	}
	
	final public void freeTemporaryVector3(int index) {
		if ((index < tempVector3.size()) &&
			(tempVector3.get(index).equals(Boolean.TRUE) == true)) {
			// free temporary vector3
			Boolean tempVector = tempVector3.get(index);
			tempVector = Boolean.FALSE;
		}
	}
	
	final public String getTemporaryVector4Name(int index) {
		String returnValue = null;
		
		if (index < tempVector4.size()) {
			returnValue = new String("tempVector4_");
			returnValue += ((Integer)index).toString();
		}
		
		return returnValue;
	}
	
	final public int allocateTemporaryVector4() {
		final int tempCount = tempVector4.size();
		int index = 0;
		while (index < tempCount) {
			if (tempVector4.get(index).equals(Boolean.FALSE) == true)
				break;
			else
				index++;
		}
		
		if (index >= tempCount) {
			// no free temporary vector4 found; create one
			tempVector4.add(new Boolean(Boolean.TRUE));
		}
		else {
			// free temporary vector4 found; mark it as used
			Boolean tempVector = tempVector4.get(index);
			tempVector = Boolean.TRUE;
		}
		
		return index;
	}
	
	final public void freeTemporaryVector4(int index) {
		if ((index < tempVector4.size()) &&
				(tempVector4.get(index).equals(Boolean.TRUE) == true)) {
				// free temporary vector4
				Boolean tempVector = tempVector4.get(index);
				tempVector = Boolean.FALSE;
			}		
	}
	
	/**
	 * return the source code of the shader for this <code>GL20GLSLCode</code>
	 * 
	 * @param shadeless <code>true</code> disables all calculation that are not needed
	 * for shadesless depth calculation
	 * 
	 * @return
	 */
	final public String getShaderCodeVertex(boolean shadeless) {
		String returnValue = new String();

		if ((dependencies & NORMAL_BIT) != 0)
			returnValue += "varying vec3 normal;\n";
		if ((dependencies & POSITION_WORLD_BIT) != 0)
			returnValue += "varying vec3 " + POSITION_WORLD_NAME + ";\n";
		if ((dependencies & POSITION_EYE_BIT) != 0) {
			returnValue += "varying vec3 " + POSITION_EYE_NAME + ";\n";
			returnValue += "uniform mat4 " + WORLD_TO_VIEW_MATRIX_NAME + ";\n";
		}
		
		returnValue += "void main(void) {\n";
		
		if ((dependencies & NORMAL_BIT) != 0)
			returnValue += "normal = -vec3(gl_NormalMatrix * gl_Normal);\n";
		if ((dependencies & POSITION_WORLD_BIT) != 0)
			returnValue += POSITION_WORLD_NAME + " = vec3(gl_ModelViewMatrix * gl_Vertex);\n";
		if ((dependencies & POSITION_EYE_BIT) != 0)
			returnValue += POSITION_EYE_NAME + " = -vec3(" + WORLD_TO_VIEW_MATRIX_NAME + " * (gl_ModelViewMatrix * gl_Vertex));\n";
		
		returnValue += "gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n" +
			"}\n";
		
		return returnValue;
	}
	
	final public String getShaderCodeFragment() {
		String returnValue = new String();
		
		// create varying block
		if ((dependencies & NORMAL_BIT) != 0)
			returnValue += "varying vec3 normal;\n";
		if ((dependencies & POSITION_WORLD_BIT) != 0)
			returnValue += "varying vec3 " + getDependencyName(POSITION_WORLD_BIT) + ";\n";
		if ((dependencies & POSITION_EYE_BIT) != 0)
			returnValue += "varying vec3 " + getDependencyName(POSITION_EYE_BIT) + ";\n";
		
		// add body
		returnValue += "void main(void) {\n";		
		
		// create constant block
		// first constant scalars
		final int constScalarCount = constScalarStack.size();
		for (int i=0;i < constScalarCount;i++)
			returnValue += "const float " + getConstScalarName(i) + " = " + constScalarStack.get(i).toString() + ";\n";

		// second constant vectors with 3 components
		final int constVector3Count = constVector3Stack.size();
		for (int i=0;i < constVector3Count;i++) {
			final Vector3f tempVector = constVector3Stack.get(i); 
			returnValue += "const vec3 " + getConstVector3Name(i) + " = vec3(" + ((Float)tempVector.x).toString() + "," + ((Float)tempVector.y).toString() + "," + ((Float)tempVector.z).toString() + ");\n";
		}
		
		// third constant vectors with 4 components
		final int constVector4Count = constVector4Stack.size();
		for (int i=0;i < constVector4Count;i++) {
			final Vector4f tempVector = constVector4Stack.get(i);
			returnValue += "const vec4 " + getConstVector4Name(i) + " = vec4(" + ((Float)tempVector.x).toString() + "," + ((Float)tempVector.y).toString() + "," + ((Float)tempVector.z).toString() + "," + ((Float)tempVector.w).toString() + ");\n";
		}
		
		// create temporary block
		// first temporary scalars
		final int tempScalarCount = tempScalars.size();
		for (int i=0;i < tempScalarCount;i++)
			returnValue += "float " + getTemporaryScalarName(i) + ";\n";
		
		// second temporary vectors with 3 components
		final int tempVector3Count = tempVector3.size();
		for (int i=0;i < tempVector3Count;i++)
			returnValue += "vec3 " + getTemporaryVector3Name(i) + ";\n";
		
		// third temporary vectors with 4 components
		final int tempVector4Count = tempVector4.size();
		for (int i=0;i < tempVector4Count;i++)
			returnValue += "vec4 " + getTemporaryVector4Name(i) + ";\n";
				
		// calculate dependencies
		if ((dependencies & NORMAL_BIT) != 0)
			returnValue += "const vec3 " + getDependencyName(NORMAL_BIT) + " = normalize(normal);\n";
		
		// add code
		returnValue += sourceCode;
		
		// close body
		returnValue += "}";
		
		return returnValue;
	}
	
	final public void appendCode(String code) {
		if (code != null)
			sourceCode += code;
	}
	
	final public String getScalarName(int index) {
		if ((index & CONST_BIT) != 0)
			return getConstScalarName(index & ~CONST_BIT);
		else
			return getTemporaryScalarName(index);
	}
	
	final public String getVector3Name(int index) {
		if ((index & CONST_BIT) != 0)
			return getConstVector3Name(index & ~CONST_BIT);
		else
			return getTemporaryVector3Name(index);
	}
	
	final public String getVector4Name(int index) {
		if ((index & CONST_BIT) != 0)
			return getConstVector4Name(index & ~CONST_BIT);
		else
			return getTemporaryVector4Name(index);
	}
	
	final public void freeScalar(int index) {
		if ((index & CONST_BIT) == 0)
			freeTemporaryScalar(index);
	}
	
	final public void freeVector3(int index) {
		if ((index & CONST_BIT) == 0)
			freeTemporaryVector3(index);
	}
	
	final public void freeVector4(int index) {
		if ((index & CONST_BIT) == 0)
			freeTemporaryVector4(index);
	}
}