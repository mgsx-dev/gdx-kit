package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.nav")
@EditableComponent
public class NavComponent implements Component, Poolable{

	
	public final static ComponentMapper<NavComponent> components = ComponentMapper.getFor(NavComponent.class);
	
	public transient int index = -1;
	public transient Vector3 normal = new Vector3();
	public transient Vector3 position = new Vector3();
	@Override
	public void reset() {
		index = -1;
	}
}
