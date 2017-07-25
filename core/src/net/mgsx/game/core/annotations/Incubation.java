package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Types/methods marked with this annotation indicate this feature is in beta.
 * It is recommanded to not use it for production.
 * This indcates this API could change drastically in a near future and won't be
 * reported in CHANGES file.
 * 
 * @author mgsx
 *
 */
@Retention(SOURCE)
@Target({TYPE, METHOD})
public @interface Incubation {
	/** optional comment for the incubation state */
	public String value() default "";
}
