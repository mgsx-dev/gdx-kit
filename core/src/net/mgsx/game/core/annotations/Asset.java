package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * Mark field as asset. If {@link #value()} is not empty then it is used as fileName in
 * asset manager. 
 * 
 * Can be used in Systems, Tools and Components.
 * 
 * Can be used in conjonction with {@link Editable} in order to change default asset path.
 * Will be restored from files with storable objects.
 * 
 * Asset parameters can't be defined here. If special loading parameters are required then first
 * load asset in manager before dependency injection phase.
 * 
 * TODO maybe link to a type extends Parameters which contains configuration ...
 * 
 * @author mgsx
 *
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface Asset 
{
	/** asset filename */
	public String value() default "";
}
