package net.mgsx.game.core;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.helpers.TypeMap;
import net.mgsx.game.core.plugins.Plugin;

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
		if(components.contains(type, true)) return;
		Storable storable = type.getAnnotation(Storable.class);
		if(storable != null){
			register(type, storable.value());
		}
		
		components.add(type);
		
	}
	
	public final ObjectMap<String, Class<? extends Component>> typeMap = new ObjectMap<String, Class<? extends Component>>();
	public final ObjectMap<Class<? extends Component>, String> nameMap = new ObjectMap<Class<? extends Component>, String>();
	
	
	// TODO move registration to registry ... that make sense
	
	
	/**
	 * Register a type to be persisted (saved and loaded within game file)
	 * @param storable type to store
	 * @param name type name in file (should be unique)
	 * throw runtime error if name conflicts within registry.
	 */
	private void register(Class<? extends Component> storable, String name)
	{
		if(typeMap.containsKey(name)){
			if(typeMap.get(name) == storable){
				// use syout instead of GDX log because GdxLogger not enabled yet.
				System.out.println("skip type name " + name + " already registered for the same type.");
			}else{				
				throw new Error("type name " + name + " already registered for class " + typeMap.get(name).getName());
			}
		}
		typeMap.put(name, storable);
		nameMap.put(storable, name);
	}
	
	public void scanPackages()
	{
		Reflections reflections = new Reflections(
				new ConfigurationBuilder()
			     .setUrls(ClasspathHelper.getUrlsForPackagePrefix("net.mgsx.game"))
			     .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner()));
		
		reflections.getTypesAnnotatedWith(PluginDef.class);
		
		for(Class<? extends Plugin> type : reflections.getSubTypesOf(Plugin.class)){
			registerPlugin(type);
		}
		
		for(Class<? extends Component> type : reflections.getSubTypesOf(Component.class)){
			register(type);
		}
	}
	
	public void init(GameScreen screen) 
	{
		// bootstrap here ...
		scanPackages();
		
		for(Plugin plugin : plugins.values()){
			plugin.initialize(screen);
		}
	//	typeMap.get("example.platformer.climb-zone")
		// register automatic serializers
		for(Entry<String, Class<? extends Component>> entry : typeMap.entries())
		{
			Storable storable = entry.value.getAnnotation(Storable.class);
			if(storable != null){
				
				// storable.value()
			}
		}
	}
	
}
