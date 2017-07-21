package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.badlogic.ashley.core.Component;

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

	/** Whether to automatically clone component when cloning entity, if true
	 * all non transient public field are copied by reference. 
	 * In order to customize cloning, component should implements {@link Duplicable} interface. 
	 * Default is false.
	 */
	// TODO cloning is not reserved to editor context, should be defined as Clone annotation with properties (by value, by reference ...)
	boolean autoClone() default false;
}
