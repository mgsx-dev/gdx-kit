package net.mgsx.core.storage;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;

import net.mgsx.core.helpers.ReflectionHelper;

public class EntityGroupSerializer implements Json.Serializer<EntityGroup>
{
	private AssetManager assets;
	
	
	
	public EntityGroupSerializer(AssetManager assets) {
		super();
		this.assets = assets;
	}

	@Override
	public void write(Json json, EntityGroup object, Class knownType) 
	{
		// TODO ? export class name mapping ?
		
		json.writeObjectStart();
		
		json.writeArrayStart("assets");
		for(String name : assets.getAssetNames())
		{
			Class type = assets.getAssetType(name);
			
			json.writeObjectStart();
			json.writeValue("type", type.getName());
			json.writeValue("name", name);
			json.writeObjectEnd();
		}
		json.writeArrayEnd();
		
		json.writeArrayStart("entities");
		for(Entity entity : object.entities)
		{
			json.writeObjectStart();
			for(Component component : entity.getComponents())
			{
				if(component instanceof Storable) json.writeValue(component.getClass().getName(), component);
			}
			json.writeObjectEnd();
		}
		json.writeArrayEnd();
		
		
		
		json.writeObjectEnd();
		
	}

	@Override
	public EntityGroup read(Json json, JsonValue jsonData, Class type) 
	{
		EntityGroup object = new EntityGroup();
		object.entities = new Array<Entity>();
		
		for(JsonIterator i = jsonData.get("assets").iterator() ; i.hasNext() ; ){
			JsonValue asset = i.next();
			Class assetType = ReflectionHelper.forName(asset.get("type").asString());
			String name = asset.get("name").asString();
			assets.load(name, assetType);
		}
		
		assets.finishLoading(); // XXX remove this when go for EntityGroupLoader
		
		for(JsonIterator entityIteractor = jsonData.get("entities").iterator() ; entityIteractor.hasNext() ; ){
			JsonValue value = entityIteractor.next();
			Entity entity = new Entity();
			object.entities.add(entity);
			for(JsonIterator i = value.iterator() ; i.hasNext() ; ){
				JsonValue cvalue = i.next();
				String className = cvalue.name;
				Component component = json.readValue(ReflectionHelper.forName(className), cvalue);
				entity.add(component);
				
			}
		}
		
		return object;
	}

}
