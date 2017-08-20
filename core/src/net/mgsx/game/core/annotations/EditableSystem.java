package net.mgsx.game.core.annotations;

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
@Target(TYPE)
public @interface EditableSystem 
{
	boolean allowSetProcessing() default true;

	/** name in the editor */
	String value() default "";
	
	/** tag current system as debug only. This system won't process when editor is hidden. */
	boolean isDebug() default false;
}
