package net.mgsx.game.core.ui.accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.NotEditable;
import net.mgsx.game.core.helpers.ReflectionHelper;

public class AccessorScanner {

	private static void scanField(Array<Accessor> accessors, Object entity, Field field, boolean annotationBasedOnly)
	{
		if(Modifier.isStatic(field.getModifiers())) return;
		
		if(annotationBasedOnly){
			Editable editable = field.getAnnotation(Editable.class);
			if(editable == null){
				return;
			}
			if(editable.value().isEmpty())
				accessors.add(new FieldAccessor(entity, field));
			else
				accessors.add(new FieldAccessor(entity, field, editable.value()));
		}
		else
		{
			if(field.getAnnotation(NotEditable.class) != null) return;
			
			accessors.add(new FieldAccessor(entity, field));
		}
	}
	
	private static void scanMethod(Array<Accessor> accessors, Object entity, Method method, boolean annotationBasedOnly)
	{
		if(Modifier.isStatic(method.getModifiers())) return;
		
		Editable editable = method.getAnnotation(Editable.class);
		if(editable == null && annotationBasedOnly){
			return;
		}
		
		if(method.getAnnotation(NotEditable.class) != null) return;
		
		if(editable != null && method.getReturnType() == void.class && method.getParameterTypes().length == 0){
			if(editable.value().isEmpty())
				accessors.add(new VoidAccessor(entity, method));
			else
				accessors.add(new VoidAccessor(entity, method, editable.value()));
		}
		
		if(method.getName().startsWith("set") && method.getName().length() > 3 && method.getParameterTypes().length == 1)
		{
			String getterName = "g" + method.getName().substring(1);
			Method getter = ReflectionHelper.method(entity.getClass(), getterName);
			if(getter == null || getter.getReturnType() != method.getParameterTypes()[0]){
				// try boolean pattern setX/isX
				getterName = "is" + method.getName().substring(3);
				getter = ReflectionHelper.method(entity.getClass(), getterName);
			}
			if(getter != null && getter.getReturnType() == method.getParameterTypes()[0]){
				String name = method.getName().substring(3,4).toLowerCase() + method.getName().substring(4);
				accessors.add(new MethodAccessor(entity, name, getter, method));
			}
		}
	}

	
	public static Array<Accessor> scan(Object entity, boolean annotationBasedOnly)
	{
		return scan(entity, annotationBasedOnly, true);
	}
	public static Array<Accessor> scan(Object entity, boolean annotationBasedOnly, boolean includeTransient)
	{
		Array<Accessor> accessors = new Array<Accessor>();
		
		// scan fields
		for(Field field : entity.getClass().getFields())
		{
			if(includeTransient || !Modifier.isTransient(field.getModifiers()))
				scanField(accessors, entity, field, annotationBasedOnly);
		}
		
		// scan getter/setter pattern
		for(Method method : entity.getClass().getMethods())
		{
			scanMethod(accessors, entity, method, annotationBasedOnly);
			
		}
		return accessors;
	}
	
	
	public static Array<Accessor> scan(Object entity, Class<? extends Annotation> ... annotations)
	{
		Array<Accessor> accessors = new Array<Accessor>();
		
		// scan fields
		for(Field field : entity.getClass().getFields())
		{
			boolean match = true;
			for(Class<? extends Annotation> a : annotations){
				if(field.getAnnotation(a) == null){
					match = false;
					break;
				}
			}
			if(match){
				scanField(accessors, entity, field, false);
			}
		}
		
		// scan getter/setter pattern
		for(Method method : entity.getClass().getMethods())
		{
			boolean match = true;
			for(Class<? extends Annotation> a : annotations){
				if(method.getAnnotation(a) == null){
					match = false;
					break;
				}
			}
			if(match){
				scanMethod(accessors, entity, method, false);
			}
		}
		return accessors;
	}

}
