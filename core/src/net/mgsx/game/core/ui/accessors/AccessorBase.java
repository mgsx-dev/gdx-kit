package net.mgsx.game.core.ui.accessors;

import java.lang.annotation.Annotation;

abstract public class AccessorBase implements Accessor
{

	@Override
	public <T> T get(Class<T> type) {
		Object value = get();
		if(value != null && type.isAssignableFrom(value.getClass())){
			return (T)get();
		}
		return null;
	}
	
	@Override
	public <T extends Annotation> T config(Class<T> annotation) {
		return null;
	}

}
