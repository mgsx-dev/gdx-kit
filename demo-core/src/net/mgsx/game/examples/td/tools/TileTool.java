package net.mgsx.game.examples.td.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.components.Repository;
import net.mgsx.game.core.tools.ClickTool;
import net.mgsx.game.examples.td.components.TileComponent;
import net.mgsx.game.examples.td.systems.MapSystem;

public abstract class TileTool extends ClickTool
{

	public TileTool(String name, EditorScreen editor) {
		super(name, editor);
	}

	@Override
	protected void create(Vector2 position) 
	{
		MapSystem map = getEngine().getSystem(MapSystem.class);
		int x = (int)position.x;
		int y = (int)position.y;
		
		Entity cell = map.getTile(x, y);
		if(cell == null){
			cell = getEngine().createEntity();
			TileComponent tile = getEngine().createComponent(TileComponent.class);
			tile.x = x;
			tile.y = y;
			cell.add(tile);
			cell.add(getEngine().createComponent(Repository.class));
			getEngine().addEntity(cell);
		}
		edit(cell);
		map.invalidate();
	}
	
	abstract protected void edit(Entity entity);

}
