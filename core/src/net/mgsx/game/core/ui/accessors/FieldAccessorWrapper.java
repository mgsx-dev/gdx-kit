package net.mgsx.game.core.ui.accessors;

import java.lang.reflect.Field;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.ReflectionHelper;

public class FieldAccessorWrapper extends AccessorBase
{
	private Accessor original;
	private Field field;
	
	
	
	public FieldAccessorWrapper(Accessor original, Field field) {
		super();
		this.original = original;
		this.field = field;
	}
	public FieldAccessorWrapper(Accessor original, String field) {
		this(original, ReflectionHelper.field(original.getType(), field));
	}

	@Override
	public Object get() {
		return ReflectionHelper.get(original.get(), field);
	}

	@Override
	public void set(Object value) {
		ReflectionHelper.set(original.get(), field, value);
	}

	@Override
	public String getName() {
		return field.getName();
	}

	@Override
	public Class getType() {
		return field.getType();
	}

	@Override
	public Editable config() {
		return field.getAnnotation(Editable.class);
	}
}
