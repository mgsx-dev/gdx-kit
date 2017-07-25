package net.mgsx.game.core.helpers.shaders;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Uniform {

	/** uniform variable name in GLSL code. Leave it blank for injected uniforms 
	 * @see ShaderInfo#inject()
	 * */
	public String value() default "";
	
	/**
	 * list all configuration when this uniform is active.
	 * all configurations for the same {@link ShaderProgramManaged} will be used to
	 * inject macro (#define) in GLSL code.
	 * name in camel case will be converted to constant like string.
	 * eg. highQuality will inject #define HIGH_QUALITY in GLSL code.
	 */
	public String [] only() default {};
}
