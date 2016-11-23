package net.mgsx.game.plugins.box2d.storage;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.storage.ContextualSerializer;
import net.mgsx.game.plugins.box2d.components.Box2DJointModel;

// TODO doesn't require context anymore ...
public class Box2DJointSerializer extends ContextualSerializer<Box2DJointModel>
{
	public Box2DJointSerializer() {
		super(Box2DJointModel.class);
	}

	@Override
	public void write(Json json, Box2DJointModel object, Class knownType) {
		json.writeObjectStart();
		json.writeField(object, "id");
		json.writeField(object, "def");
		json.writeObjectEnd();
	}

	@Override
	public Box2DJointModel read(Json json, JsonValue jsonData, Class type) {
		Box2DJointModel object = new Box2DJointModel();
		
		// first read defs
		json.readField(object, "id", jsonData);
		json.readField(object, "def", jsonData);
		object.bodyA = jsonData.get("def").get("bodyA").asInt();
		object.bodyB = jsonData.get("def").get("bodyB").asInt();

		// TODO not same indexes !
		
		return object;
	}

}
