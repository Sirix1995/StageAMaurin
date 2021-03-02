package de.grogra.imp3d.glsl.material.channel;

import java.util.HashMap;

import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.math.Abs;
import de.grogra.math.Acos;
import de.grogra.math.Asin;
import de.grogra.math.Atan;
import de.grogra.math.Ceil;
import de.grogra.math.Cos;
import de.grogra.math.Cosh;
import de.grogra.math.Cubic;
import de.grogra.math.E;
import de.grogra.math.Exp;
import de.grogra.math.Exp2;
import de.grogra.math.Expm1;
import de.grogra.math.Floor;
import de.grogra.math.Log;
import de.grogra.math.Log10;
import de.grogra.math.Phi;
import de.grogra.math.Pi;
import de.grogra.math.Ramp;
import de.grogra.math.Scallop;
import de.grogra.math.Sin;
import de.grogra.math.Sin01;
import de.grogra.math.Sinh;
import de.grogra.math.Sqr;
import de.grogra.math.Sqrt;
import de.grogra.math.Tan;
import de.grogra.math.Tanh;
import de.grogra.math.Triangle;
import de.grogra.vecmath.Math2;
import de.grogra.xl.lang.FloatToFloat;

/**
 * Wrapper for FloatToFloat instances. Also implements some basic
 * GLSLFloatToFloat classes.
 * 
 * @author Konni Hartmann
 */
public class FloatToFloatCollection {
	static HashMap<Class, GLSLFloatToFloat> ftf = new HashMap<Class, GLSLFloatToFloat>();

	private static void insertIntoHashMap(GLSLFloatToFloat in) {
		ftf.put(in.instanceFor(), in);
	}

	public static void initMap() {
		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Abs.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result(
						"abs(" + input.convert(Result.ET_FLOAT) + ")",
						Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Acos.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result("acos(" + input.convert(Result.ET_FLOAT)
						+ ")", Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Asin.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result("asin(" + input.convert(Result.ET_FLOAT)
						+ ")", Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Atan.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result("atan(" + input.convert(Result.ET_FLOAT)
						+ ")", Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Ceil.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result("ceil(" + input.convert(Result.ET_FLOAT)
						+ ")", Result.ET_FLOAT);
			}
		});

		// Not as simple
		// ftf.put(Chain.class, new String[]{"ceil(", ")"} );

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Cos.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result(
						"cos(" + input.convert(Result.ET_FLOAT) + ")",
						Result.ET_FLOAT);
			}
		});

		// Cosh is defined wrongly in GroIMP
		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Cosh.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				String val = sc.registerNewTmpVar(Result.ET_FLOAT, input
						.convert(Result.ET_FLOAT));
				return new Result("((exp(" + val + ")+exp(-" + val + "))*0.5)",
						Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Cubic.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				String val = sc.registerNewTmpVar(Result.ET_FLOAT, input
						.convert(Result.ET_FLOAT));
				String offset = sc.registerNewTmpVar(Result.ET_FLOAT, val
						+ "-floor(" + val + ")");
				String eval = sc.registerNewTmpVar(Result.ET_FLOAT, offset
						+ "<0.?" + offset + "+1.<0.?0.:" + offset + "+1.:"
						+ offset + ">1.?1.:" + offset);
				return new Result("(" + eval + "*" + eval + "*(6.-4.*" + eval
						+ ")-1.)", Result.ET_FLOAT);
			}
		});

		// Custom Function not possible

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return E.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result("(2.7182818284590452354*"
						+ input.convert(Result.ET_FLOAT) + ")", Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Exp.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result(
						"exp(" + input.convert(Result.ET_FLOAT) + ")",
						Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Exp2.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result("exp2(" + input.convert(Result.ET_FLOAT)
						+ ")", Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Expm1.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result("exp(" + input.convert(Result.ET_FLOAT)
						+ "-1.)", Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Floor.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result("floor(" + input.convert(Result.ET_FLOAT)
						+ "-1.)", Result.ET_FLOAT);
			}
		});

		// Function is depricated

		// Id is handled internally

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Log.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result(
						"log(" + input.convert(Result.ET_FLOAT) + ")",
						Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Log10.class;
			}

			private final double M_1_LN10 = 1 / Math.log(10);

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result("log(" + input.convert(Result.ET_FLOAT)
						+ ")*" + M_1_LN10, Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Log.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result(
						"log(" + input.convert(Result.ET_FLOAT) + ")",
						Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Phi.class;
			}

			private final double factor = (Math.sqrt(5) + 1) / 2.0;

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result(
						input.convert(Result.ET_FLOAT) + "*" + factor,
						Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Pi.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result(input.convert(Result.ET_FLOAT) + "*"
						+ Math.PI, Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Phi.class;
			}

			private final double factor = Math.PI / 180.;

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result(
						input.convert(Result.ET_FLOAT) + "*" + factor,
						Result.ET_FLOAT);
			}
		});

		// may be calculated faster
		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Ramp.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				String val = sc.registerNewTmpVar(Result.ET_FLOAT, input
						.convert(Result.ET_FLOAT));
				String offset = sc.registerNewTmpVar(Result.ET_FLOAT, val
						+ "-floor(" + val + ")");
				return new Result("2.*(" + offset + "<0.?" + offset
						+ "+1.<0.?0.:" + offset + "+1.:" + offset + ">1.?1.:"
						+ offset + ")-1.", Result.ET_FLOAT);
			}
		});

		// rnd not possible

		// rndabs not possible

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Scallop.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result("2.*abs(sin("
						+ input.convert(Result.ET_FLOAT) + "*" + Math.PI
						+ "))-1.", Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Sin.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result(
						"sin(" + input.convert(Result.ET_FLOAT) + ")",
						Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Sin01.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result("sin(" + input.convert(Result.ET_FLOAT) + "*"
						+ Math2.M_2PI + ")", Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Sinh.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				String val = sc.registerNewTmpVar(Result.ET_FLOAT, input
						.convert(Result.ET_FLOAT));
				return new Result("((exp(" + val + ")-exp(-" + val + "))*0.5)",
						Result.ET_FLOAT);
			}
		});

		// spline function complex...

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Sqr.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				String val = sc.registerNewTmpVar(Result.ET_FLOAT, input
						.convert(Result.ET_FLOAT));
				return new Result(val + "*" + val, Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Sqrt.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result("sqrt(" + input.convert(Result.ET_FLOAT)
						+ ")", Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Tan.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				return new Result(
						"tan(" + input.convert(Result.ET_FLOAT) + ")",
						Result.ET_FLOAT);
			}
		});

		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Tanh.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				String val = sc.registerNewTmpVar(Result.ET_FLOAT, input
						.convert(Result.ET_FLOAT));
				String expVal = sc.registerNewTmpVar(Result.ET_FLOAT, "exp("
						+ val + ")");
				String expMVal = sc.registerNewTmpVar(Result.ET_FLOAT, "exp(-"
						+ val + ")");
				return new Result("(" + expVal + "-" + expMVal + ")/(" + expVal
						+ "+" + expMVal + ")", Result.ET_FLOAT);
			}
		});

		// (v < 0.25f) ? 4 * v : (v < 0.75f) ? -4 * v + 2 : 4 * v - 4;
		insertIntoHashMap(new GLSLFloatToFloat() {
			@Override
			public Class<?> instanceFor() {
				return Triangle.class;
			}

			@Override
			public Result process(Result input, FloatToFloat fkt,
					ShaderConfiguration sc) {
				String val = sc.registerNewTmpVar(Result.ET_FLOAT, input
						.convert(Result.ET_FLOAT));
				String offset = sc.registerNewTmpVar(Result.ET_FLOAT, val
						+ "-floor(" + val + ")");
				String v = sc.registerNewTmpVar(Result.ET_FLOAT, offset
						+ "<0.?" + offset + "+1.<0.?0.:" + offset + "+1.:"
						+ offset + ">1.?1.:" + offset);
				return new Result("(" + v + " < 0.25) ? 4. * " + v + " : (" + v
						+ " < 0.75) ? -4. * " + v + " + 2. : 4. * " + v
						+ " - 4.", Result.ET_FLOAT);
			}
		});
	}

	public static Result addFloatToFloat(Result in, FloatToFloat fkt,
			ShaderConfiguration sc) {
		GLSLFloatToFloat ftfw = fkt != null ? ftf.get(fkt.getClass()) : null;
		return ftfw != null ? ftfw.process(in, fkt, sc) : in;
	}
}
