package net.mgsx.game.core.helpers.shaders;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface ShaderInfo {
	public String vs();
	public String fs();
	
	/**
	 * Whenether to inject uniform variable in shader code based on java uniforms.
	 * If false, shader code must have corresponding uniform variable with the same name
	 * prefixed with "u_" or have a proper {@link Uniform#value()}.
	 * 
	 * It is highly recommanded to use injection : less prone of error, enable freezing.
	 * 
	 * default is true.
	 * @return
	 */
	public boolean inject() default true;
}
