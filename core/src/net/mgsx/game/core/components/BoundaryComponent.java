package net.mgsx.game.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.collision.BoundingBox;

public class BoundaryComponent implements Component
{
	public static final ComponentMapper<BoundaryComponent> components = ComponentMapper.getFor(BoundaryComponent.class);

	public BoundingBox box = new BoundingBox();
	public boolean inside, justInside, justOutside;
}
