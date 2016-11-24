package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.mgsx.game.core.storage.serializers.AnnotationBasedSerializer;

@Retention(RUNTIME)
@Target({FIELD,TYPE})
public @interface Storable 
{
	String value() default "";

	/**
	 * Define whether a {@link AnnotationBasedSerializer} should be created for this type. Default is false.
	 */
	boolean auto() default false;
}
