package net.mgsx.game.core.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.Kit;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.FieldAccessor;

/**
 * This layers make cache about all reflection stuff.
 * 
 * @author mgsx
 *
 */
public class ReflectionCache implements KitMeta
{
	/**
	 * @deprecated use Kit.meta.accessorsFor instead : {@link #accessorsFor(Object, Class)}
	 */
	@Deprecated
	public static Array<Accessor> fieldsFor(Object object, Class<? extends Annotation> annotatedBy) 
	{
		return Kit.meta.accessorsFor(object, annotatedBy);
	}
	
	@Override
	public Array<Accessor> accessorsFor(Object object, Class<? extends Annotation> annotatedBy) 
	{
		// TODO can't cache with actual accessor implementation
		Array<Accessor> r = new Array<Accessor>();
		accessorsFor(r, object, object.getClass(), annotatedBy);
		return r;
	}
	
	private void accessorsFor(Array<Accessor> r, Object object, Class<?> type, Class<? extends Annotation> annotatedBy) 
	{
		if(type.getSuperclass() != null) accessorsFor(r, object, type.getSuperclass(), annotatedBy);
		for(Class<?> iface : type.getInterfaces()) accessorsFor(r, object, iface, annotatedBy);
		for(Field field : type.getDeclaredFields())
		{
			if(field.getAnnotation(annotatedBy) != null){
				if(!field.isAccessible()){
					field.setAccessible(true);
				}
				r.add(new FieldAccessor(object, field));
			}
		}
	}

}
