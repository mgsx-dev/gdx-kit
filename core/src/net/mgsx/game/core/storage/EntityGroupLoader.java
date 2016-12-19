package net.mgsx.game.core.storage;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;

import net.mgsx.game.core.GameRegistry;
import net.mgsx.game.core.helpers.ReflectionHelper;

public class EntityGroupLoader extends AsynchronousAssetLoader<EntityGroup, EntityGroupLoaderParameters>
{
	final GameRegistry registry;
	EntityGroup entityGroup;
	JsonValue jsonData;

	public EntityGroupLoader(FileHandleResolver resolver, GameRegistry registry) {
		super(resolver);
		this.registry = registry;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, EntityGroupLoaderParameters parameter) {
		final Array<AssetDescriptor> assets = new Array<AssetDescriptor>();
		
		Json json = EntityGroupStorage.setup();
		
		// load json file and return asset part
		jsonData = new JsonReader().parse(file);
		if(jsonData.has("assets")){
			for(JsonIterator i = jsonData.get("assets").iterator() ; i.hasNext() ; ){
				JsonValue asset = i.next();
				String typeName = asset.get("type").asString();
				Class assetType = json.getClass(typeName);
				if(assetType == null) assetType = ReflectionHelper.forName(typeName);
				String name = asset.get("name").asString();
				// TODO do the same for other types (Textures ...)
				
				assets.add(new AssetDescriptor(name, assetType));
			}
		}
		
		return assets;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, EntityGroupLoaderParameters parameter) {
		
		entityGroup = new EntityGroup();
		Json json = EntityGroupStorage.setup(manager, registry, entityGroup);
		jsonData = new JsonReader().parse(file);
		if(jsonData.has("entities")){
			for(JsonIterator entityIteractor = jsonData.get("entities").iterator() ; entityIteractor.hasNext() ; ){
				JsonValue value = entityIteractor.next();
				Entity entity = new Entity(); // we don't need pool since it is a duplicable template.
				entityGroup.add(entity);
				for(JsonIterator i = value.iterator() ; i.hasNext() ; ){
					JsonValue cvalue = i.next();
					String typeName = cvalue.name;
					if("id".equals(typeName)) continue; // skip id tag
					Class<? extends Component> componentType = registry.typeMap.get(typeName);
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
		// TODO clean all references
		return entityGroup;
	}

}
