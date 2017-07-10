package net.mgsx.game.examples.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Mesh;

public class TreesComponent implements Component
{
	
	public final static ComponentMapper<TreesComponent> components = ComponentMapper.getFor(TreesComponent.class);
	public Mesh mesh;
}
