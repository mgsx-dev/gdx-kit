package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.td.components.Road;
import net.mgsx.game.examples.td.components.TileComponent;

public class TileRenderer extends AbstractShapeSystem
{
	public TileRenderer(GameScreen game) {
		super(game, Family.all(TileComponent.class).get(), GamePipeline.RENDER);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TileComponent tile = TileComponent.components.get(entity);
		
		Color c;
		if(Road.components.has(entity)) c = Color.GRAY;
		else c = Color.WHITE;
		
		renderer.rect(tile.x, tile.y, 1, 1, c, c, c, c);
	}
}
