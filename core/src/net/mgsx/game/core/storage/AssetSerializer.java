package net.mgsx.game.core.storage;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;

public class AssetSerializer<T> implements Serializer<T>
{
	private Class<T> type;
	AssetManager assets;
	
	public AssetSerializer(Class<T> type) {
		super();
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}
	
	@Override
	public void write(Json json, T object, Class knownType) 
	{
		String ref = getReference(object);
		json.writeValue(ref);
	}

	@Override
	public T read(Json json, JsonValue jsonData, Class type) 
	{
		String ref = jsonData.asString();
		return assets.get(ref, this.type);
	}
	
	protected String getReference(T object)
	{
		return assets.getAssetFileName(object);
	}

}
