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
}
