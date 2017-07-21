package net.mgsx.game.core.storage;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;

import net.mgsx.game.core.helpers.ReflectionHelper;

// TODO move code to storage (reader) and call it from here!
// usefull for tests bypassing asset manager

public class EntityGroupLoader extends AsynchronousAssetLoader<EntityGroup, EntityGroupLoaderParameters>
{
	EntityGroup entityGroup;

	public EntityGroupLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, EntityGroupLoaderParameters parameter) {
		final Array<AssetDescriptor> assets = new Array<AssetDescriptor>();
		
		Json json = EntityGroupStorage.setup();
		
		// load json file and return asset part
		JsonValue jsonData = new JsonReader().parse(file);
		if(jsonData == null)
		{
			String message = "file " + fileName + " is empty or is not a json file.";
			if(parameter.config.failSafe){
				Gdx.app.error("Storage", message);
			}else{
				throw new GdxRuntimeException(message);
			}
		}
		if(jsonData != null && jsonData.has("assets")){
			for(JsonIterator i = jsonData.get("assets").iterator() ; i.hasNext() ; ){
				JsonValue asset = i.next();
				String typeName = asset.get("type").asString();
				Class assetType = json.getClass(typeName);
				if(assetType == null) assetType = ReflectionHelper.forName(typeName);
				String name = asset.get("name").asString();
				
				if(assetType == EntityGroup.class){
					LoadConfiguration cfg = new LoadConfiguration();
					cfg.assets = parameter.config.assets;
					cfg.engine = parameter.config.engine;
					cfg.registry = parameter.config.registry;
					EntityGroupLoaderParameters params = new EntityGroupLoaderParameters();
					params.config = cfg;
					assets.add(new AssetDescriptor(name, assetType, params)); 
				}else{
					// get default params
					AssetLoaderParameters params = parameter.config.registry.getDefaultLoaderParameter(assetType);
					
					assets.add(new AssetDescriptor(name, assetType, params)); 
				}
			}
		}
		
		return assets;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, EntityGroupLoaderParameters parameter) {
		
		entityGroup = new EntityGroup();
		Json json = EntityGroupStorage.setup(manager, parameter.config.registry, entityGroup);
		JsonValue jsonData = new JsonReader().parse(file);
		
		entityGroup.json = json;
		entityGroup.jsonData = jsonData;
		
		if(jsonData != null && jsonData.has("entities")){
			for(JsonIterator entityIteractor = jsonData.get("entities").iterator() ; entityIteractor.hasNext() ; ){
				JsonValue value = entityIteractor.next();
				Entity entity = new Entity(); // we don't need pool since it is a duplicable template.
				entityGroup.add(entity);
				for(JsonIterator i = value.iterator() ; i.hasNext() ; ){
					JsonValue cvalue = i.next();
					String typeName = cvalue.name;
					if("id".equals(typeName)) continue; // skip id tag
					Class<? extends Component> componentType = parameter.config.registry.typeMap.get(typeName);
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
						String error = "type name not registered : " + String.valueOf(typeName);
						if(parameter.config.failSafe){
							parameter.config.diagnostic.add(error);
						}else{
							throw new GdxRuntimeException(error);
						}
						
					}
					
				}
			}
		}
	}

	@Override
	public EntityGroup loadSync(AssetManager manager, String fileName, FileHandle file, EntityGroupLoaderParameters parameter) {
		
		// clean all references
		EntityGroup result = entityGroup;
		
		entityGroup = null;
		
		return result;
	}

}
