package net.mgsx.game.core.ui.accessors;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.ReflectionHelper;

public class AccessorScanner {

	public static Array<Accessor> scan(Object entity, boolean annotationBasedOnly)
	{
		Array<Accessor> accessors = new Array<Accessor>();
		
		// scan fields
		for(Field field : entity.getClass().getFields())
		{
			if(Modifier.isStatic(field.getModifiers())) continue;
			
			if(annotationBasedOnly){
				Editable editable = field.getAnnotation(Editable.class);
				if(editable == null){
					continue;
				}
				if(editable.value().isEmpty())
					accessors.add(new FieldAccessor(entity, field));
				else
					accessors.add(new FieldAccessor(entity, field, editable.value()));
			}
			else
			{
				accessors.add(new FieldAccessor(entity, field));
			}
		}
		
		// scan getter/setter pattern
		for(Method method : entity.getClass().getMethods())
		{
			if(Modifier.isStatic(method.getModifiers())) continue;
			
			if(annotationBasedOnly){
				Editable editable = method.getAnnotation(Editable.class);
				if(editable == null){
					continue;
				}
				if(method.getReturnType() != void.class) continue;
				if(method.getParameterCount() > 0) continue;
				if(editable.value().isEmpty())
					accessors.add(new VoidAccessor(entity, method));
				else
					accessors.add(new VoidAccessor(entity, method, editable.value()));
			}
			else
			{
				if(method.getName().startsWith("set") && method.getName().length() > 3 && method.getParameterCount() == 1)
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
			
		}
		return accessors;
	}
}
