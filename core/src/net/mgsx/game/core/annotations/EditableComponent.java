package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.badlogic.ashley.core.Component;

import net.mgsx.game.core.components.Duplicable;

@Retention(RUNTIME)
@Target(TYPE)
public @interface EditableComponent 
{
	String name() default "";
	
	// Family (TODO maybe defined on editor itself ...)
	Class<? extends Component>[] all() default {};
	Class<? extends Component>[] one() default {};
	Class<? extends Component>[] exclude() default {};
	
	/** whether to create a tool to add component (default is true) */
	boolean autoTool() default true;

	/** whether to automatically clone component when cloning entity. All field are copied by reference.
	 * Note that by default fields are not copied. 
	 * In order to customize cloning, component should implements {@link Duplicable} interface. 
	 */
	boolean autoClone() default false;
}
