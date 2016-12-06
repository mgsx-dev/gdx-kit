package net.mgsx.game.core.ui.accessors;

import java.lang.reflect.Method;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.ReflectionHelper;

public class MethodAccessor implements Accessor
{
	private Object object;
	private String name;
	private Method getter;
	private Method setter;
	
	
	public MethodAccessor(Object object, String name, String getter, String setter) {
		super();
		this.object = object;
		this.name = name;
		this.getter = ReflectionHelper.method(object.getClass(), getter);
		this.setter = ReflectionHelper.method(object.getClass(), setter, this.getter.getReturnType());
	}
	public MethodAccessor(Object object, String name, Method getter, Method setter) {
		super();
		this.object = object;
		this.name = name;
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public Object get() {
		return ReflectionHelper.invoke(object, getter);
	}

	@Override
	public void set(Object value) {
		ReflectionHelper.invoke(object, setter, value);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class getType() {
		return getter.getReturnType();
	}
	@Override
	public Editable config() {
		Editable a = setter.getAnnotation(Editable.class);
		if(a == null){
			a = getter.getAnnotation(Editable.class);
		}
		return a;
	}
	
}