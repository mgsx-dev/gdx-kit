package net.mgsx.game.plugins.core.storage;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.storage.AssetSerializer;
import net.mgsx.game.plugins.core.components.ProxyComponent;

public class ProxySerializer extends AssetSerializer<ProxyComponent>{

	public ProxySerializer() {
		super(ProxyComponent.class);
	}

	@Override
	public void write(Json json, ProxyComponent object, Class knownType) {
		json.writeObjectStart();
		json.writeValue("ref", reference(object.ref));
		json.writeObjectEnd();
	}

	@Override
	public ProxyComponent read(Json json, JsonValue jsonData, Class type) 
	{
		ProxyComponent object = new ProxyComponent();
		json.readField(object, "ref", jsonData);
		return object;
	}
}
