package net.mgsx.fwk.editor;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Array;

import net.mgsx.fwk.editor.plugins.FactoryPlugin;

public class Entity 
{
	private static Array<FactoryPlugin> factories = new Array<FactoryPlugin>();
	public static Entity create()
	{
		Entity entity = new Entity(); // TODO pool ?
		for(FactoryPlugin factory : factories) factory.create(entity);
		return entity;
	}
	public static void registerFactory(FactoryPlugin factory){
		factories.add(factory);
	}
	

	private Map<Class<?>, Object> aspects = new HashMap<Class<?>, Object>();
	
	private Entity(){}
	
	@SuppressWarnings("unchecked")
	public <T> T as(Class<T> type){
		return (T)aspects.get(type);
	}
	
	public <T> void set(Class<T> type, T value){
		aspects.put(type, value);
	}
	public <T> void set(T value){
		aspects.put(value.getClass(), value);
	}
	public Iterable<Object> aspects(){
		return aspects.values();
	}
}
