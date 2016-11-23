package net.mgsx.game.core.storage.serializers;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class IgnoreSerializer<T> implements Json.Serializer<T>{
	@Override
	public void write(Json json, T object, Class knownType) 
	{
		json.writeValue(null);
	}
	@Override
	public T read(Json json, JsonValue jsonData, Class type) {
		return null;
	}
}