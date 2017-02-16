package net.mgsx.game.core.meta;

import java.lang.annotation.Annotation;

import com.badlogic.gdx.utils.Array;

public class CollectionClassRegistry extends ClassRegistry
{
	final protected Array<Class<?>> classes = new Array<Class<?>>();
	
	public CollectionClassRegistry(Class<?> ...classes) 
	{
		this.classes.addAll(classes);
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


	public void add(Class<?> ...classes) {
		this.classes.addAll(classes);
	}
}
