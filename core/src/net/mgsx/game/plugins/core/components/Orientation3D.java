package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("core.orientation3")
@EditableComponent
public class Orientation3D implements Component
{
	
	public final static ComponentMapper<Orientation3D> components = ComponentMapper.getFor(Orientation3D.class);
	
	@Editable
	public transient Vector3 direction = new Vector3(Vector3.X);
	
	@Editable
	public transient Vector3 normal = new Vector3(Vector3.Y);
}
