package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.home")
@EditableComponent(autoClone=true)
public class Home implements Component, Poolable
{
	
	public final static ComponentMapper<Home> components = ComponentMapper.getFor(Home.class);
	
	@Editable public int life = 100;

	@Override
	public void reset() {
		life = 100;
	}
}
