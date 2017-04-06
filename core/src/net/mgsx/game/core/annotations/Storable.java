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

	// TODO maybe obsolete since transient fields are not saved ... point is system with a lot of fields and
	// we want to persit one only ... but it would be cleaner to do it with transient though.
	/**
	 * Define whether a {@link AnnotationBasedSerializer} should be created for this type. Default is false.
	 * When auto, only fields annotated with {@link Storable} will be serialized. When not auto, all non transient
	 * fields will be serialized.
	 */
	boolean auto() default false;
}
