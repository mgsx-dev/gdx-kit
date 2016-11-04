package net.mgsx.game.plugins.boundary.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.mgsx.game.core.components.OverrideProxy;

public class BoundaryComponent implements Component, OverrideProxy
{
	public static final ComponentMapper<BoundaryComponent> components = ComponentMapper.getFor(BoundaryComponent.class);

	public BoundingBox box = new BoundingBox();
	public boolean inside, justInside, justOutside;
}
