package net.mgsx.game.core.storage;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectSet;

import net.mgsx.game.core.GameRegistry;
import net.mgsx.game.plugins.core.components.ProxyComponent;

class EntityGroupSerializer implements Json.Serializer<EntityGroup>
{
	private AssetManager assets;
	private GameRegistry registry;
	
	private final ObjectSet<String> references = new ObjectSet<String>();
	
	public EntityGroupSerializer(AssetManager assets, GameRegistry registry) {
		super();
		this.assets = assets;
		this.registry = registry;
	}

	@Override
	public void write(Json json, EntityGroup object, Class knownType) 
	{
		// clear references
		references.clear();
		
		json.writeObjectStart();
		
		json.writeArrayStart("entities");
		for(int i=0 ; i<object.entities.size ; i++)
		{
			Entity entity = object.entities.get(i);
			
			json.writeObjectStart();
			json.writeValue("id", i);
			ProxyComponent proxy = ProxyComponent.components.get(entity);
			if(proxy != null){
				references.add(proxy.ref);
			}
			// default serialization
			for(Component component : entity.getComponents())
			{
				String typeName = registry.nameMap.get(component.getClass());
				if(typeName != null){
					json.writeValue(typeName, component);
				}
			}
			json.writeObjectEnd();
		}
		json.writeArrayEnd();
		
		json.writeArrayStart("assets");
		for(String name : references)
		{
			Class type = assets.getAssetType(name);
			
			json.writeObjectStart();
			json.writeValue("type", type.getName());
			json.writeValue("name", name);
			// TODO save some parameters (texture wrap/mipmap ?)
			json.writeObjectEnd();
		}
		json.writeArrayEnd();
		
		
		json.writeObjectEnd();
		
		references.clear();
	}

	@Override
	public EntityGroup read(Json json, JsonValue jsonData, Class type) 
	{
		throw new Error("use loader instead !");
//		if(jsonData.has("assets")){
//			for(JsonIterator i = jsonData.get("assets").iterator() ; i.hasNext() ; ){
//				JsonValue asset = i.next();
//				Class assetType = ReflectionHelper.forName(asset.get("type").asString());
//				String name = asset.get("name").asString();
//				if(assetType == Texture.class){
//					TextureParameter p = new TextureParameter();
//					p.wrapU = TextureWrap.Repeat;
//					p.wrapV = TextureWrap.Repeat; // XXX hack for reapeat texture always !
//					p.magFilter = TextureFilter.MipMapLinearLinear;
//					p.minFilter = TextureFilter.MipMapLinearLinear;
//					p.genMipMaps = true; // XXX hack again ...
//					assets.load(name, Texture.class, p);
//				}else if(assetType == Model.class){
//					ModelParameters mp = new ModelParameters();
//					TextureParameter p = mp.textureParameter;
//					p.wrapU = TextureWrap.Repeat;
//					p.wrapV = TextureWrap.Repeat; // XXX hack for reapeat texture always !
//					p.magFilter = TextureFilter.MipMapLinearLinear;
//					p.minFilter = TextureFilter.MipMapLinearLinear;
//					p.genMipMaps = true; // XXX hack again ...
//					assets.load(name, Model.class, mp);
//				}else assets.load(name, assetType);
//			}
//			
//			assets.finishLoading(); // XXX remove this when go for EntityGroupLoader
//		}
//		
//		if(jsonData.has("entities")){
//			for(JsonIterator entityIteractor = jsonData.get("entities").iterator() ; entityIteractor.hasNext() ; ){
//				JsonValue value = entityIteractor.next();
//				Entity entity = new Entity();
//				object.add(entity);
//				for(JsonIterator i = value.iterator() ; i.hasNext() ; ){
//					JsonValue cvalue = i.next();
//					String typeName = cvalue.name;
//					if("id".equals(typeName)) continue; // skip id tag
//					Class<? extends Component> componentType = registry.typeMap.get(typeName);
//					if(componentType != null)
//					{
//						Component component = json.readValue(componentType, cvalue);
//						// special case where no type given TODO try with knownType (3 args)
//						if(component == null){
//							component = ReflectionHelper.newInstance(componentType);
//						}
//						entity.add(component);
//					}
//					else
//					{
//						throw new Error("type name not registered : " + String.valueOf(typeName));
//					}
//					
//				}
//			}
//		}
//		
//		return object;
	}

	public void reference(String fileName) 
	{
		references.add(fileName);
	}

}
