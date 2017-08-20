package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * mark String field as asset (automatically loaded, reference automatically saved)
 * used for {@link Task} only, see Asset
 * 
 * @author mgsx
 *
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface TaskAsset 
{
	public Class value();
}
