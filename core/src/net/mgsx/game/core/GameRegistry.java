package net.mgsx.game.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.helpers.TypeMap;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.core.meta.ReflectionCache;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.serializers.AnnotationBasedSerializer;
import net.mgsx.game.core.ui.accessors.Accessor;

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
		// XXX not used : ClassRegistry.instance.getTypesAnnotatedWith(PluginDef.class);
		
		for(Class<? extends Plugin> type : ClassRegistry.instance.getSubTypesOf(Plugin.class)){
			registerPlugin(type);
		}
		
		for(Class<? extends Component> type : ClassRegistry.instance.getSubTypesOf(Component.class)){
			register(type);
		}
	}
	
	void init(GameScreen screen) 
	{
		// bootstrap here ...
		scanPackages();
		
		for(Plugin plugin : plugins.values()){
			plugin.initialize(screen);
		}
		// register automatic serializers
		for(Entry<String, Class<? extends Component>> entry : typeMap.entries())
		{
			// if this component doesn't have custom serializer then add automatic annotation based.
			if(!serializers.containsKey(entry.value)){
				Storable storable = entry.value.getAnnotation(Storable.class);
				if(storable != null && storable.auto()){
					serializers.put(entry.value, new AnnotationBasedSerializer(entry.value));
				}
			}
		}
		
		collect(screen);
	}
	
	void collect(GameScreen screen)
	{
		// scan all systems in order to inject assets
		for(EntitySystem system : screen.entityEngine.getSystems())
		{
			for(Accessor accessor : ReflectionCache.fieldsFor(system, Asset.class)){
				Asset asset = accessor.config(Asset.class);
				if(asset != null && !asset.value().isEmpty()){
					screen.assets.load(asset.value(), accessor.getType());
				}
			}
		}
	}

	public void inject(Engine engine, Object system) 
	{
		for(Accessor accessor : ReflectionCache.fieldsFor(system, Inject.class)){
			if(EntitySystem.class.isAssignableFrom(accessor.getType()))
			{
				EntitySystem dep = engine.getSystem(accessor.getType());
				if(dep == null){
					Gdx.app.error("reflection", "system " + accessor.getType().getSimpleName() + " cannot be injected in " + system.getClass().getSimpleName() + " : not found in engine");
				}
				accessor.set(dep);
			}else{
				Gdx.app.error("reflection", "not supported type injection");
			}
		}
	}
	
	void inject(GameScreen screen) 
	{
		for(EntitySystem system : screen.entityEngine.getSystems())
		{
			for(Accessor accessor : ReflectionCache.fieldsFor(system, Asset.class)){
				Asset asset = accessor.config(Asset.class);
				if(asset != null && !asset.value().isEmpty()){
					accessor.set(screen.assets.get(asset.value(), accessor.getType()));
				}
			}
			inject(screen.entityEngine, system);
		}
	}
	
}
