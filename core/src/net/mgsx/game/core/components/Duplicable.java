package net.mgsx.game.core.components;

import com.badlogic.ashley.core.Component;

public interface Duplicable {

	// TODO should be void duplicate(Component clone)
	public Component duplicate();
}
