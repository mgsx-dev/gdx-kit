package net.mgsx.game.core.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.FieldAccessor;

/**
 * This layers make cache about all reflection stuff.
 * 
 * @author mgsx
 *
 */
public class ReflectionCache 
{

	public static Array<Accessor> fieldsFor(Object object, Class<? extends Annotation> annotatedBy) 
	{
		// TODO can't cache with actual accessor implementation
		Array<Accessor> r = new Array<Accessor>();
		fieldsFor(r, object, object.getClass(), annotatedBy);
		return r;
	}
	
	private static void fieldsFor(Array<Accessor> r, Object object, Class<?> type, Class<? extends Annotation> annotatedBy) 
	{
		if(type.getSuperclass() != null) fieldsFor(r, object, type.getSuperclass(), annotatedBy);
		for(Class<?> iface : type.getInterfaces()) fieldsFor(r, object, iface, annotatedBy);
		for(Field field : type.getDeclaredFields())
		{
			if(field.getAnnotation(Inject.class) != null){
				if(!field.isAccessible()){
					field.setAccessible(true);
				}
				r.add(new FieldAccessor(object, field));
			}
		}
	}

}
