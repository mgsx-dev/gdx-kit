package net.mgsx.game.core.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.mgsx.game.core.ui.DefaultFieldEditor;
import net.mgsx.game.core.ui.FieldEditor;

/**
 * Mark element as Editable.
 * 
 * Editable element generates a GUI control.
 * 
 * By default when a property is editable, all accessible fields for its type are
 * editable as well. Then a full hierarchy editor can be automatically generated.
 * 
 * In some case it is wanted to override this default behavior by annotate sub field with {@link NotEditable}.
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
