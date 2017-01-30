package net.mgsx.game.examples.td.tools;

import com.badlogic.ashley.core.Entity;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.examples.td.components.Road;

public class RoadTool extends TileTool
{

	public RoadTool(EditorScreen editor) {
		super("Tile - Road", editor);
	}

	@Override
	protected void edit(Entity entity) {
		if(Road.components.has(entity))
			entity.remove(Road.class);
		else
			entity.add(getEngine().createComponent(Road.class));
	}
	
}
