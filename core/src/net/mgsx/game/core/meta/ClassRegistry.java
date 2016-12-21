package net.mgsx.game.core.meta;

import java.lang.annotation.Annotation;

import com.badlogic.gdx.utils.Array;

abstract public class ClassRegistry {

	public static final ClassRegistry none = new ClassRegistry() {
		
		@Override
		public Array<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation) {
			return new Array<Class<?>>();
		}
		
		@Override
		public Array<Class> getSubTypesOf(Class type) {
			return new Array<Class>();
		}
	};
	
	public static ClassRegistry instance = none;
	
	
	abstract public Array<Class> getSubTypesOf(Class type);
	abstract public Array<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation);
}
