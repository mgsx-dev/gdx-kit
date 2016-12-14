package net.mgsx.game.examples.platformer.ai;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent(autoClone=true)
public class MortarState implements Component, Poolable
{
	
	public final static ComponentMapper<MortarState> components = ComponentMapper.getFor(MortarState.class);
	
	public int count;

	@Override
	public void reset() {
		count = 0;
	}
}
