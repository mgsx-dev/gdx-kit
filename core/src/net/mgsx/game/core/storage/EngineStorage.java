package net.mgsx.game.core.storage;

import java.io.StringWriter;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.AccessorScanner;

public class EngineStorage {

	public static void save(FileHandle file, SaveConfiguration config) 
	{
		Json json = new Json();

		StringWriter writer = new StringWriter();
		
		json.setWriter(writer);
		
		json.writeObjectStart();
		
		// TODO maybe some other configuration ? plugins ?
		
		json.writeArrayStart("systems");
		
		for(EntitySystem system : config.engine.getSystems()){
			Storable store = system.getClass().getAnnotation(Storable.class);
			if(store != null){
				json.writeObjectStart();
				json.writeValue("type", store.value());
				for(Accessor accessor : AccessorScanner.scan(system, true)){
					if(accessor.getType() != void.class)
						json.writeValue(accessor.getName(), accessor.get());
				}
				json.writeObjectEnd();
			}
		}
		
		json.writeArrayEnd();
		
		json.writeObjectEnd();
		
		file.writeString(json.prettyPrint(writer.toString()), false);
		
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
				for(Accessor accessor : AccessorScanner.scan(system, true)){
					if(accessor.getType() != void.class){
						JsonValue jsonValue = systemSettings.get(accessor.getName());
						Object value = json.readValue(accessor.getType(), jsonValue);
						accessor.set(value);
					}
				}
			}
			
		}
		
	}
}
