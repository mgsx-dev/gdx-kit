package net.mgsx.game.core.ui.events;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.ui.accessors.Accessor;

public class SetEvent extends Event implements Poolable
{
	public Accessor accessor;
	
	@Override
	public void reset() {
		accessor = null;
		super.reset();
	}
}
