package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Mark field as auto injected.
 * 
 * When used in Kit context, systems will be injected in current field based on type.
 * 
 * TODO for now only work with tools
 * 
 * @author mgsx
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Inject {

}
