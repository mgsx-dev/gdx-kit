package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.td.components.Home;
import net.mgsx.game.examples.td.components.TileComponent;

public class HomeRenderer extends AbstractShapeSystem
{

	public HomeRenderer(GameScreen game) {
		super(game, Family.all(TileComponent.class, Home.class).get(), GamePipeline.RENDER);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TileComponent tile = TileComponent.components.get(entity);
		renderer.setColor(Color.BROWN);
		final float s = .25f;
		renderer.rect(tile.x - s + .5f, tile.y - s + .5f, 2*s, 2*s);
	}
}
