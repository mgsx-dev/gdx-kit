package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
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
@Target({FIELD, METHOD})
public @interface Editable 
{
	/** name in the editor, default is field name */
	String value() default "";
	
	/** editor group name, default is no group */
	String group() default "";
	
	EnumType type() default EnumType.AUTO;
	
	Class<? extends FieldEditor> editor() default DefaultFieldEditor.class;
}
