package net.mgsx.game.plugins.btree.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author mgsx
 *
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface TaskAlias 
{
	/** alias name */
	String value() default "";
	
}
