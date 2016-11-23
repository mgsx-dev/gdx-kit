package net.mgsx.game.plugins.boundary.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("boundary")
@EditableComponent(name="Boundary")
public class BoundaryComponent implements Component
{
	public static final ComponentMapper<BoundaryComponent> components = ComponentMapper.getFor(BoundaryComponent.class);

	// TODO handle local and global boundary : global will be automatically updated with only one system
	// other component are responsible to set local boundary only.
	@Editable public BoundingBox box = new BoundingBox();
	public boolean inside, justInside, justOutside;
}
