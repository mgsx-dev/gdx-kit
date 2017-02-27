package net.mgsx.game.core.ui.accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.ReflectionHelper;

public class VoidAccessor extends AccessorBase
{
	private final Object object;
	private final Method method;
	private final String name;
	
	public VoidAccessor(Object object, Method method) {
		this(object, method, method.getName());
	}
	
	public VoidAccessor(Object object, Method method, String name) {
		super();
		this.object = object;
		this.method = method;
		this.name = name;
	}

	@Override
	public Object get() {
		ReflectionHelper.invoke(object, method);
		return null;
	}

	@Override
	public void set(Object value) {
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class getType() {
		return void.class;
	}

	@Override
	public Editable config() {
		return method.getAnnotation(Editable.class);
	}

	@Override
	public <T extends Annotation> T config(Class<T> annotation) {
		return method.getAnnotation(annotation);
	}

}