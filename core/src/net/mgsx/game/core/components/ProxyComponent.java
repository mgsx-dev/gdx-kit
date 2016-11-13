package net.mgsx.game.core.components;

import com.badlogic.ashley.core.Component;

import net.mgsx.game.core.annotations.Storable;

@Storable("proxy")
public class ProxyComponent implements Component, Duplicable
{
	public String ref;

	@Override
	public Component duplicate() {
		ProxyComponent clone = new ProxyComponent();
		clone.ref = ref;
		return clone;
	}
	
	
	
}
