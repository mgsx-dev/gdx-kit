package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;

import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;

@Storable("proxy")
public class ProxyComponent implements Component, Duplicable
{
	public String ref;

	@Override
	public Component duplicate(Engine engine) {
		ProxyComponent clone = engine.createComponent(ProxyComponent.class);
		clone.ref = ref;
		return clone;
	}
	
	
	
}
