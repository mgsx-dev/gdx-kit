package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Negate {@link Editable} behavior. By default all accessible properties
 * for an editable object are editable ut in some case we want to prevent some
 * of them to be editable.
 * 
 * Fields/Methods marked as {@link NotEditable} are excluded from scanning.
 * 
 * @author mgsx
 *
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD, TYPE})
public @interface NotEditable 
{
}
