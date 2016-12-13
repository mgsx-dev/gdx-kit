package net.mgsx.game.core.storage;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class EntityGroupRefSerializer extends AssetSerializer<EntityGroupRef>
{
	public EntityGroupRefSerializer() {
		super(EntityGroupRef.class);
	}

	@Override
	public void write(Json json, EntityGroupRef object, Class knownType) {
		json.writeObjectStart();
		json.writeValue("ref", reference(object.ref));
		json.writeObjectEnd();
	}

	@Override
	public EntityGroupRef read(Json json, JsonValue jsonData, Class type) 
	{
		EntityGroupRef object = new EntityGroupRef();
		json.readField(object, "ref", jsonData);
		object.group = getAssets().get(object.ref, EntityGroup.class);
		return object;
	}
}
