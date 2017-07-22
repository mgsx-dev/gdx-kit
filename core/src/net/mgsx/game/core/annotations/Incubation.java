package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Types marked with this annotation are candidates to pull-up to a higer level :
 * <ul>
 * <li>Demo code candidate for core</li>
 * <li>Core code candidate for Libgdx core/extension</li>
 * </ul>
 * 
 * @author mgsx
 *
 */
@Retention(SOURCE)
@Target(TYPE)
public @interface Incubation {

}
