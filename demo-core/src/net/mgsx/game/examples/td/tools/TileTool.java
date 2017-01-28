package net.mgsx.game.examples.td.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.components.Repository;
import net.mgsx.game.core.tools.ClickTool;
import net.mgsx.game.examples.td.systems.MapSystem;

public class TileTool extends ClickTool
{

	public TileTool(EditorScreen editor) {
		super("Tile set/unset", editor);
	}

	@Override
	protected void create(Vector2 position) 
	{
		MapSystem map = getEngine().getSystem(MapSystem.class);
		int x = (int)position.x;
		int y = (int)position.y;
		Entity created = map.switchTile(x, y);
		if(created != null){
			created.add(getEngine().createComponent(Repository.class));
		}
	}

}
