package net.mgsx.game.core.ui.accessors;

import java.lang.reflect.Field;

import net.mgsx.game.core.helpers.ReflectionHelper;

public class FieldAccessor implements Accessor
{
	private Object object;
	private Field field;
	private String label;
	
	public FieldAccessor(Object object, Field field, String labelName) {
		super();
		this.object = object;
		this.field = field;
		this.label = labelName;
	}
	public FieldAccessor(Object object, Field field) {
		super();
		this.object = object;
		this.field = field;
		this.label = field.getName();
	}
	public FieldAccessor(Object object, String fieldName) {
		super();
		this.object = object;
		this.field = ReflectionHelper.field(object, fieldName);
		this.label = fieldName;
	}

	@Override
	public Object get() {
		return ReflectionHelper.get(object, field);
	}

	@Override
	public void set(Object value) {
		ReflectionHelper.set(object, field, value);
	}
	@Override
	public String getName() {
		return label;
	}
	@Override
	public Class getType() {
		return field.getType();
	}
	
}