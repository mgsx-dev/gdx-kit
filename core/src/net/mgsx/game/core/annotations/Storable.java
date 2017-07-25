package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Set a type or field to be persisted.
 * 
 * By default, all non transient accessible fields are persisted (see {@link com.badlogic.gdx.utils.Json}).
 * 
 * This default behavior can be overriden by setting {@link #auto()} to true. 
 * 
 * 
 * @author mgsx
 *
 */
@Retention(RUNTIME)
@Target({FIELD,TYPE})
public @interface Storable 
{
	/**
	 * Define name in serialized format. Highly recommanded for safe refactoring.
	 */
	String value() default "";

	/**
	 * Define whether a {@link AnnotationBasedSerializer} should be created for this type. 
	 * Default is false. That is all non transient accessible fields are persisted.
	 * When true, only fields annotated with {@link Storable} will be persisted.
	 * Changing this option prevent to mark all other field transient.
	 */
	boolean auto() default false;
}
