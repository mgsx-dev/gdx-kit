package net.mgsx.game.core.storage;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;

import net.mgsx.game.core.components.OverrideProxy;
import net.mgsx.game.core.components.ProxyComponent;
import net.mgsx.game.core.helpers.ReflectionHelper;

public class EntityGroupSerializer implements Json.Serializer<EntityGroup>
{
	private AssetManager assets;
	
	/** used to track entities */
	EntityGroup object;
	
	
	public EntityGroupSerializer(AssetManager assets) {
		super();
		this.assets = assets;
	}

	@Override
	public void write(Json json, EntityGroup object, Class knownType) 
	{
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
		for(int i=0 ; i<object.entities.size ; i++)
		{
			Entity entity = object.entities.get(i);
			
			json.writeObjectStart();
			json.writeValue("id", i);
			ProxyComponent proxy = entity.getComponent(ProxyComponent.class);
			if(proxy != null){
				// just serialize proxy
				json.writeValue(Storage.nameMap.get(ProxyComponent.class), proxy);
				for(Component component : entity.getComponents())
				{
					if(component instanceof OverrideProxy){
						String typeName = Storage.nameMap.get(component.getClass());
						if(typeName != null){
							json.writeValue(typeName, component);
						}
					}
				}
			}
			else{
				// default serialization
				for(Component component : entity.getComponents())
				{
					String typeName = Storage.nameMap.get(component.getClass());
					if(typeName != null){
						json.writeValue(typeName, component);
					}
				}
			}
			json.writeObjectEnd();
		}
		json.writeArrayEnd();
		
		
		
		json.writeObjectEnd();
		
	}

	@Override
	public EntityGroup read(Json json, JsonValue jsonData, Class type) 
	{
		EntityGroup object = this.object = new EntityGroup();
		object.entities = new Array<Entity>();
		
		for(JsonIterator i = jsonData.get("assets").iterator() ; i.hasNext() ; ){
			JsonValue asset = i.next();
			Class assetType = ReflectionHelper.forName(asset.get("type").asString());
			String name = asset.get("name").asString();
			if(assetType == Texture.class){
				TextureParameter p = new TextureParameter();
				p.wrapU = TextureWrap.Repeat;
				p.wrapV = TextureWrap.Repeat; // XXX hack for reapeat texture always !
				assets.load(name, Texture.class, p);
			}else assets.load(name, assetType);
		}
		
		assets.finishLoading(); // XXX remove this when go for EntityGroupLoader
		
		for(JsonIterator entityIteractor = jsonData.get("entities").iterator() ; entityIteractor.hasNext() ; ){
			JsonValue value = entityIteractor.next();
			Entity entity = new Entity();
			object.entities.add(entity);
			for(JsonIterator i = value.iterator() ; i.hasNext() ; ){
				JsonValue cvalue = i.next();
				String typeName = cvalue.name;
				if("id".equals(typeName)) continue; // skip id tag
				Class<? extends Component> componentType = Storage.typeMap.get(typeName);
				if(componentType != null)
				{
					Component component = json.readValue(componentType, cvalue);
					// special case where no type given TODO try with knownType (3 args)
					if(component == null){
						component = ReflectionHelper.newInstance(componentType);
					}
					entity.add(component);
				}
				else
				{
					throw new Error("type name not registered : " + String.valueOf(typeName));
				}
				
			}
		}
		
		return object;
	}

}
