package de.grogra.imp3d.glsl.material.channel;

/**
 * Result of all GLSLChanelMap Shaders. Holds String representation and type of
 * a Shaders result.
 * 
 * @author Konni Hartmann
 */
public class Result {
	public final static int ET_UNKNOWN = -1;
	public final static int ET_FLOAT = 0;
	public final static int ET_VEC2 = ET_FLOAT + 1;
	public final static int ET_VEC3 = ET_VEC2 + 1;
	public final static int ET_VEC4 = ET_VEC3 + 1;
	// Not used up to now
	public final static int ET_BOOL = ET_VEC4 + 1;

	String term;
	int returnType;

	public int getReturnType() {
		return returnType;
	}

	public Result(String value, int rtType) {
		term = value;
		returnType = rtType;
	}

	private static final String typeNames[] = {"float", "vec2", "vec3", "vec4", "bool"};
	public static String getTypeString(int TYPE) {
		return typeNames[TYPE];
	}
	
	/**
	 * Defines conversations between different result types from (float to vec4)
	 * to (float to vec4)
	 */
	private static final String convMat[][][] = {
			{ { "", "" },                     { "vec2(", ")" }, { "vec3(", ")" },             { "vec4(vec3(", "),1.0)" } },
			{ { "dot(", ",vec2(.5))" },       { "", "" },       { "vec3(", ",0.0)" },         { "vec4(", ", 0.0, 1.0)" } },
			{ { "dot(", ",vec3(1./3.))" },    { "(", ").xy" },  { "", "" },                   { "vec4(", ",1.0)" } },
			{ { "dot(", ",vec4(.25))" },      { "(", ").xy" },  { "(", ").xyz" },             { "", "" } } };

	/**
	 * Defines alternative conversations between different result types from (float to vec4)
	 * to (float to vec4)
	 */
	private static final String reduceMat[][][] = {
			{ { "", "" },     { "vec2(", ",0.)" }, { "vec3(", ",0.,0.)" },  { "vec4(","0.,1.,0.)" } },
			{ { "(", ").x" }, { "", "" },          { "vec3(", ",0.)" },     { "vec4(", ", 1.,0.)" } },
			{ { "(", ").x" }, { "(", ").xy" },     { "", "" },              { "vec4(", ",0.)" } },
			{ { "(", ").x" }, { "(", ").xy" },     { "(", ").xyz" },        { "", "" } } };

	public String convert(int tarType) {
		if (returnType > ET_VEC4 || tarType > ET_VEC4)
			return term;
		return convMat[returnType][tarType][0] + term
				+ convMat[returnType][tarType][1];
	}

	public String reduce(int tarType) {
		if (returnType > ET_VEC4 || tarType > ET_VEC4)
			return term;
		return reduceMat[returnType][tarType][0] + term
				+ reduceMat[returnType][tarType][1];
	}

	
	@Override
	public String toString() {
		return term;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + returnType;
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Result other = (Result) obj;
		if (returnType != other.returnType)
			return false;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		return true;
	}
}
