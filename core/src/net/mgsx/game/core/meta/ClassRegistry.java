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
		public <T> Array<Class<? extends T>> getSubTypesOf(Class<T> type) {
			return new Array<Class<? extends T>>();
		}
		@Override
		public Array<Class<?>> getClasses() {
			return new Array<Class<?>>();
		}
	};
	
	public static ClassRegistry instance = none;
	
	
	abstract public <T> Array<Class<? extends T>> getSubTypesOf(Class<T> type);
	abstract public Array<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation);
	abstract public Array<Class<?>> getClasses();
}
