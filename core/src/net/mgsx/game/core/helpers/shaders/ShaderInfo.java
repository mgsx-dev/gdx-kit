package net.mgsx.game.core.helpers.shaders;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.mgsx.game.core.annotations.Incubation;

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

	/**
	 * Whenether to store shader files path. This allow to save shader path within systems
	 * configurations.
	 * Default is false.
	 * @return
	 */
	public boolean storable() default false;
	
	/**
	 * Whether to pre-compile all possible configurations based on {@link Uniform#only()}
	 * When true, different versions of this shader is compiled and switch at runtime.
	 * When false, shader is recompiled when configuration change.
	 * Default is false.
	 */
	@Incubation("not implements yet")
	public boolean preCompile() default false;
}
