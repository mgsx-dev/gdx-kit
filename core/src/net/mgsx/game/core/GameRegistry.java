package net.mgsx.game.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.helpers.TypeMap;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.Storage;

public class GameRegistry {

	private TypeMap<Plugin> plugins = new TypeMap<Plugin>();
	public final ObjectMap<Class, Serializer> serializers = new ObjectMap<Class, Serializer>();
	public Array<Class<? extends Component>> components = new Array<Class<? extends Component>>();

	
	public void registerPlugin(Class plugin) 
	{
		scan(plugin);
	}
	
	public <T extends Plugin> T getPlugin(Class<T> type) {
		return (T)plugins.get(type);
	}
	
	public void registerPlugin(Plugin plugin) 
	{
		if(plugins.containsKey(plugin.getClass())) return;
		
		scan(plugin.getClass());
		
		plugins.put(plugin.getClass(), plugin);
	}
	
	private void scan(Class<?> type)
	{
		// TODO if debug : Gdx.app.log("registry", type.getName());
		if(type.getSuperclass() != null){
			scan(type.getSuperclass());
		}
		for(Class<?> iface : type.getInterfaces()){
			scan(iface);
		}
		PluginDef def = type.getAnnotation(PluginDef.class);
		if(def != null){
			for(Class<? extends Plugin> dependency : def.dependencies()){
				registerPlugin(ReflectionHelper.newInstance(dependency));
			}
			for(Class<? extends Component> component : def.components()){
				register(component);
			}
		}
	}

	public <T> void addSerializer(Class<T> type, Json.Serializer<T> serializer) {
		serializers.put(type, serializer);
	}
	
	private void register(Class<? extends Component> type){
		
		Storable storable = type.getAnnotation(Storable.class);
		if(storable != null){
			Storage.register(type, storable.value());
		}
		
		components.add(type);
		
	}
	
	public void init(GameScreen screen) {
		for(Plugin plugin : plugins.values()){
			plugin.initialize(screen);
		}
	}
	
}
