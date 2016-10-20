package net.mgsx.game.core.helpers;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;

public class EmptySerializer<T> implements Serializer<T>
{

	@Override
	public void write(Json json, T object, Class knownType) {
		// write just an empty object for compatibility when type have field in future.
		json.writeObjectStart();
		json.writeObjectEnd();
	}

	@Override
	public T read(Json json, JsonValue jsonData, Class type) {
		return null; // XXX lets default kickon ?
	}

}
