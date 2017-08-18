package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.td.components.Damage;
import net.mgsx.game.examples.td.components.Road;
import net.mgsx.game.examples.td.components.TileComponent;

public class PrickleRender extends AbstractShapeSystem
{

	public PrickleRender(GameScreen game) {
		super(game, Family.all(TileComponent.class, Road.class, Damage.class).get(), GamePipeline.RENDER);
		renderer.setColor(Color.BROWN);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		TileComponent tile = TileComponent.components.get(entity);
		
		renderer.circle(tile.x + .5f, tile.y + .5f, .25f, 4);
	}

}
