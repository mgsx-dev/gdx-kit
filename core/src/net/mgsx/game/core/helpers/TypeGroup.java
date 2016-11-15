package net.mgsx.game.core.helpers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class TypeGroup<T> extends ObjectMap<Class, Array<T>> 
{
	public Array<T> getFor(Class type) 
	{
		Array<T> list = get(type);
		if(list == null){
			list = new Array<T>();
			put(type, list);
		}
		return list;
	}
	
	public void addFor(Class type, T value){
		getFor(type).add(value);
	}

	public void removeFor(Class type, T value, boolean identity) {
		getFor(type).removeValue(value, identity);
	}
}
