package de.grogra.numeric;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class is supposed to be used in conjunction with the ODE
 * framework to indicate allowed absolute and relative errors for
 * an integrated field.
 * 
 * To use this annotation, precede the declaration of a field
 * with it like in the following example:
 * <pre>
 *   @Tolerance(absolute = 1e-4, relative = 1e-4)
 *   double y;
 * </pre>
 * 
 * Both tolerances default to zero, indicating that selection of
 * an appropriate tolerance is up to the integrator. Note that if
 * a tolerance specification is really used is up to the actual
 * implementation of the integration method.
 * 
 * @author Reinhard Hemmerling
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Tolerance {
	double absolute() default 0;
	double relative() default 0;
}
