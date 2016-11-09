package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

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
}
