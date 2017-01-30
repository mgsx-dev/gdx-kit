package net.mgsx.game.examples.td.tools;

import com.badlogic.ashley.core.Entity;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.examples.td.components.Platform;

public class PlatformTool extends TileTool
{

	public PlatformTool(EditorScreen editor) {
		super("Tile - Platform", editor);
	}

	@Override
	protected void edit(Entity entity) 
	{
		if(Platform.components.has(entity))
			entity.remove(Platform.class);
		else
			entity.add(getEngine().createComponent(Platform.class));
	}
	
}
