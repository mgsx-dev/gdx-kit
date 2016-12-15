package net.mgsx.game.core.ui.accessors;

import java.lang.reflect.Field;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.ReflectionHelper;

public class FieldAccessor extends AccessorBase
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
		this.field = ReflectionHelper.field(object.getClass(), fieldName);
		this.label = fieldName;
	}
	public FieldAccessor(Field field) {
		super();
		this.field = field;
		this.label = field.getName();
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
	@Override
	public Editable config() {
		return field.getAnnotation(Editable.class);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof FieldAccessor){
			return ((FieldAccessor) obj).field.equals(field);
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return field.hashCode();
	}
	
}