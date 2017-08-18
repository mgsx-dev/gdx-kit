package net.mgsx.game.examples.td.storage;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.storage.ContextualSerializer;
import net.mgsx.game.examples.td.components.Attachement;

public class AttachementSerializer extends ContextualSerializer<Attachement>{

	public AttachementSerializer() {
		super(Attachement.class);
	}

	@Override
	public void write(Json json, Attachement object, Class knownType) {
		json.writeObjectStart();
		json.writeField(object, "offset");
		json.writeValue("parent", context.entities().indexOf(object.parent, true));
		json.writeObjectEnd();
	}

	@Override
	public Attachement read(Json json, JsonValue jsonData, Class type) {
		Attachement object = new Attachement();
		
		json.readField(object, "offset", jsonData);
		if(jsonData.has("parent")){
			int index = json.readValue("parent", int.class, jsonData);
			if(index >= 0) object.parent = context.find(index);
		}
		
		return object;
	}

}
