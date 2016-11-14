package net.mgsx.game.core.annotations;

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
@Target(TYPE)
public @interface EditableSystem 
{
	boolean allowSetProcessing() default true;

	/** name in the editor */
	String value() default "";
}
