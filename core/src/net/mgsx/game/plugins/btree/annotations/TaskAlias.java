package net.mgsx.game.plugins.btree.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

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
