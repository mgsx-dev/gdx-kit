package net.mgsx.game.plugins.box2d.storage;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.storage.ContextualSerializer;

public class BodySerializer extends ContextualSerializer<Body> {
	public BodySerializer() {
		super(Body.class);
	}

	@Override
	public void write(Json json, Body object, Class knownType) {
		Entity entity = (Entity)object.getUserData();
		int entityIndex = context.entities().indexOf(entity, true);;
		json.writeValue(entityIndex);
	}

	@Override
	public Body read(Json json, JsonValue jsonData, Class type) {
		return null; // body creation is done when entity is added to engine
	}
}