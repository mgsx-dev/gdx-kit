package net.mgsx.game.core.meta;

import java.lang.annotation.Annotation;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.annotations.Kit;
import net.mgsx.game.core.helpers.ArrayHelper;

public class StaticClassRegistry extends ClassRegistry
{
	final private Array<Class<?>> classes;
	
	public StaticClassRegistry(Class type) 
	{
		Kit config = (Kit)type.getAnnotation(Kit.class);
		if(config != null)
			classes = ArrayHelper.array(config.dependencies());
		else
			classes = new Array<Class<?>>();
	}
	
	
	@Override
	public Array<Class> getSubTypesOf(Class type) 
	{
		Array<Class> r = new Array<Class>();
		for(Class<?> cls : classes){
			if(type.isAssignableFrom(cls)){
				r.add(cls);
			}
		}
		return r;
	}

	@Override
	public Array<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation) {
		Array<Class<?>> r = new Array<Class<?>>();
		for(Class<?> cls : classes){
			if(cls.getAnnotation(annotation) != null){
				r.add(cls);
			}
		}
		return r;
	}
	
	@Override
	public Array<Class<?>> getClasses() {
		return classes;
	}

}
