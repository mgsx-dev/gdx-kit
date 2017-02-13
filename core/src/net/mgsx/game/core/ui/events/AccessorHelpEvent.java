package net.mgsx.game.core.ui.events;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.ui.accessors.Accessor;

public class AccessorHelpEvent extends Event implements Poolable
{
	private Accessor accessor;

	public AccessorHelpEvent(Accessor accessor) {
		super();
		this.accessor = accessor;
	}
	
	public Accessor getAccessor() {
		return accessor;
	}
	
}
