package net.mgsx.game.core.ui.accessors;

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

}
