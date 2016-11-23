package net.mgsx.game.plugins.box2d.storage;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;

public class Box2DModelSerializer implements Serializer<Box2DBodyModel> {

	@Override
	public void write(Json json, Box2DBodyModel object, Class knownType) {
		json.writeObjectStart();
		json.writeField(object, "id");
		json.writeField(object, "def");
		json.writeField(object, "fixtures");
		json.writeObjectEnd();
	}

	@Override
	public Box2DBodyModel read(Json json, JsonValue jsonData, Class type) {
		Box2DBodyModel object = new Box2DBodyModel();
		
		// first read defs
		json.readField(object, "id", jsonData);
		json.readField(object, "def", jsonData);
		json.readField(object, "fixtures", jsonData);

		return object;
	}

}
