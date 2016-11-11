package net.mgsx.game.plugins.boundary.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.OverrideProxy;

@Storable("boundary")
@EditableComponent(name="Boundary")
public class BoundaryComponent implements Component, OverrideProxy
{
	public static final ComponentMapper<BoundaryComponent> components = ComponentMapper.getFor(BoundaryComponent.class);

	@Editable public BoundingBox box = new BoundingBox();
	public boolean inside, justInside, justOutside;
}
