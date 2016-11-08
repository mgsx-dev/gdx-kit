package net.mgsx.game.core.components;

import com.badlogic.ashley.core.Component;

public class Hidden implements Component, Duplicable
{
	@Override
	public Component duplicate() {
		return new Hidden();
	}

}
