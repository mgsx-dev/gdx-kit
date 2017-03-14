package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.badlogic.ashley.core.Component;

import net.mgsx.game.core.plugins.Plugin;

/**
 * 
 * @author mgsx
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface PluginDef 
{
	/** 
	 * Used by editor to group types (tools and systems) in categories.
	 */
	String category() default "";
	
	/**
	 * plugin dependencies 
	 */
	Class<? extends Plugin>[] dependencies() default {};
	
	/**
	 * components registration 
	 */
	Class<? extends Component>[] components() default {};
	
	/**
	 * Fully Qualified Java type name(s) required to be activated.
	 * Some plugins use optional dependencies (known as "compileOnly" in Gradle or "provided" in Maven)
	 * and should only be loaded when dependencies are available.
	 */
	String [] requires() default {};
}
