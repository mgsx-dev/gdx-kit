package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

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
	String value() default "";
	Class<? extends Plugin>[] dependencies() default {};
	Class<? extends Component>[] components() default {};
}
