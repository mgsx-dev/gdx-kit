package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.mgsx.game.core.ui.DefaultFieldEditor;
import net.mgsx.game.core.ui.FieldEditor;

/**
 * 
 * @author mgsx
 *
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD, TYPE})
public @interface Editable 
{
	/** name in the editor, default is field/type name */
	String value() default "";
	
	/** editor group name, default is no group */
	String group() default "";
	
	/** use predefined type hints */
	EnumType type() default EnumType.AUTO;
	
	Class<? extends FieldEditor> editor() default DefaultFieldEditor.class;

	String doc() default "";
	
	/** field should be updated at each frames */
	boolean realtime() default false;
	
	/** only display value : user can't edit value manually */
	boolean readonly() default false;
}
