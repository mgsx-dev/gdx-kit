package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * mark String field as asset (automatically loaded, reference automatically saved)
 * 
 * @author mgsx
 *
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface Asset 
{
	public Class value();
}
