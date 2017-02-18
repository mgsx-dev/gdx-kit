package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.badlogic.gdx.ai.btree.Task;

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
