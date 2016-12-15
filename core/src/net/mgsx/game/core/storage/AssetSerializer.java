package net.mgsx.game.core.storage;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Serializer for assets : just serialize reference (fileName), type and optionally loader parameters.
 * 
 * @author mgsx
 *
 * @param <T>
 */
public class AssetSerializer<T> extends AssetAwareSerializer<T>
{
	private Class<T> type;
	
	public AssetSerializer(Class<T> type) {
		super();
		this.type = type;
	}
	
	/**
	 * Subclasses may reference additional assets.
	 * @param fileName
	 * @return fileName to save (may not be the same after some path resolution)
	 */
	protected String reference(String fileName){
		return parent.reference(fileName);
	}

	public Class<T> getType() {
		return type;
	}
	
	protected AssetManager getAssets() {
		return assets;
	}
	
	@Override
	public void write(Json json, T object, Class knownType) 
	{
		String ref = getReference(object);
		json.writeValue(parent.reference(ref));
	}

	@Override
	public T read(Json json, JsonValue jsonData, Class type) 
	{
		String ref = jsonData.asString();
		return getInstance(ref);
	}
	
	protected T getInstance(String reference){
		return assets.get(reference, type);
	}
	
	protected String getReference(T object)
	{
		return assets.getAssetFileName(object);
	}
}
