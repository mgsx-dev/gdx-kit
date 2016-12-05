package net.mgsx.game.core.storage;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;

import net.mgsx.game.core.helpers.ReflectionHelper;

public class EntityGroupLoader extends AsynchronousAssetLoader<EntityGroup, EntityGroupLoaderParameters>
{
	
	public EntityGroupLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, EntityGroupLoaderParameters parameter) {
		final Array<AssetDescriptor> assets = new Array<AssetDescriptor>();
		
		Json json = EntityGroupStorage.setup();
		
		// load json file and return asset part
		parameter.entityGroup = new EntityGroup();
		parameter.jsonData = new JsonReader().parse(file);
		if(parameter.jsonData.has("assets")){
			for(JsonIterator i = parameter.jsonData.get("assets").iterator() ; i.hasNext() ; ){
				JsonValue asset = i.next();
				String typeName = asset.get("type").asString();
				Class assetType = json.getClass(typeName);
				if(assetType == null) assetType = ReflectionHelper.forName(typeName);
				String name = asset.get("name").asString();
				AssetLoaderParameters parameters = null;
				if(assetType == EntityGroup.class){
					parameters = new EntityGroupLoaderParameters(parameter.registry);
				}
				// TODO do the same for other types (Textures ...)
				
				assets.add(new AssetDescriptor(name, assetType, parameters));
			}
		}
		
		return assets;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, EntityGroupLoaderParameters parameter) {
		
		Json json = EntityGroupStorage.setup(manager, parameter.registry, parameter.entityGroup);
		
		if(parameter.jsonData.has("entities")){
			for(JsonIterator entityIteractor = parameter.jsonData.get("entities").iterator() ; entityIteractor.hasNext() ; ){
				JsonValue value = entityIteractor.next();
				Entity entity = new Entity(); // we don't need pool since it is a duplicable template.
				parameter.entityGroup.add(entity);
				for(JsonIterator i = value.iterator() ; i.hasNext() ; ){
					JsonValue cvalue = i.next();
					String typeName = cvalue.name;
					if("id".equals(typeName)) continue; // skip id tag
					Class<? extends Component> componentType = parameter.registry.typeMap.get(typeName);
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
		}
	}

	@Override
	public EntityGroup loadSync(AssetManager manager, String fileName, FileHandle file, EntityGroupLoaderParameters parameter) {
		// TODO maybe initialize ??
		return parameter.entityGroup;
	}

}
