package net.mgsx.game.core.storage;

import java.io.StringWriter;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.AccessorScanner;

public class EngineStorage {

	public static void save(FileHandle file, SaveConfiguration config) 
	{
		Json json = new Json();

		StringWriter writer = new StringWriter();
		
		json.setWriter(writer);
		
		json.writeObjectStart();
		
		if(config.saveSystems) saveSystems(json, config);
		if(config.saveViews) saveViews(json, config);
		
		json.writeObjectEnd();
		
		file.writeString(json.prettyPrint(writer.toString()), false);
	}
	
	private static void saveSystems(Json json, SaveConfiguration config)
	{
		json.writeArrayStart("systems");
		
		for(EntitySystem system : config.engine.getSystems()){
			Storable store = system.getClass().getAnnotation(Storable.class);
			if(store != null){
				json.writeObjectStart();
				json.writeValue("type", store.value());
				for(Accessor accessor : AccessorScanner.scan(system, true, false)){
					if(accessor.getType() != void.class)
						json.writeValue(accessor.getName(), accessor.get());
				}
				json.writeObjectEnd();
			}
		}
		
		json.writeArrayEnd();
	}
	
	private static void saveViews(Json json, SaveConfiguration config)
	{
		json.writeArrayStart("views");
		
		for(EntitySystem system : config.visibleSystems){
			String name;
			Storable store = system.getClass().getAnnotation(Storable.class);
			if(store != null){
				name = store.value();
			}
			else
			{
				name = system.getClass().getName();
			}
			json.writeValue(name);
		}
		
		json.writeArrayEnd();
	}

	public static void load(FileHandle file, LoadConfiguration config){
		JsonReader reader = new JsonReader();
		JsonValue root = reader.parse(file);
		
		Json json = new Json();
		
		// type mapping
		ObjectMap<String, EntitySystem> systemRegistry = new ObjectMap<String, EntitySystem>();
		for(EntitySystem system : config.engine.getSystems()){
			Storable store = system.getClass().getAnnotation(Storable.class);
			if(store != null){
				systemRegistry.put(store.value(), system);
			}
		}
		
		
		if(root.has("systems")){
			for(JsonIterator i = root.get("systems").iterator() ; i.hasNext() ; ){
				JsonValue systemSettings = i.next();
				String type = systemSettings.getString("type");
				EntitySystem system = systemRegistry.get(type);
				if(system != null){
					// TODO shouldn't be like that : set only values when defined in json, avoid
					// setting null values !
					for(Accessor accessor : AccessorScanner.scan(system, true, false)){
						if(accessor.getType() != void.class){
							JsonValue jsonValue = systemSettings.get(accessor.getName());
							Object value = json.readValue(accessor.getType(), jsonValue);
							accessor.set(value);
						}
					}
				}else{
					Gdx.app.error("Reflection", "unknown system " + type);
				}
			}
			
		}
		
		if(root.has("views")){
			for(JsonIterator i = root.get("views").iterator() ; i.hasNext() ; ){
				JsonValue systemSettings = i.next();
				String type = systemSettings.asString();
				EntitySystem system = systemRegistry.get(type);
				if(system == null){
					system = config.engine.getSystem(ReflectionHelper.forName(type));
				}
				if(system != null){
					config.visibleSystems.add(system);
				}else{
					Gdx.app.error("Reflection", "unknown system " + type);
				}
			}
			
		}
		
	}
}
