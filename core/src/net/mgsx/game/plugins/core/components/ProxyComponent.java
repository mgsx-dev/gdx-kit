package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;
import net.mgsx.game.core.storage.EntityGroup;

@Storable("proxy")
@EditableComponent(name="Proxy", autoTool=false)
public class ProxyComponent implements Component, Duplicable, Serializable
{
	
	public final static ComponentMapper<ProxyComponent> components = ComponentMapper.getFor(ProxyComponent.class);
	
	public EntityGroup template, clones;
	
	public String ref;

	@Override
	public Component duplicate(Engine engine) {
		ProxyComponent clone = engine.createComponent(ProxyComponent.class);
		clone.ref = ref;
		clone.template = template; // same template but differents clones
		return clone;
	}

	@Override
	public void write(Json json) {
		json.writeField(this, "ref");
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		json.readField(this, "ref", jsonData);
	}
}
