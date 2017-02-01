package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.td.components.TileComponent;
import net.mgsx.game.examples.td.components.Aiming;

public class TowerRender extends AbstractShapeSystem
{

	public TowerRender(GameScreen game) {
		super(game, Family.all(TileComponent.class, Aiming.class).get(), GamePipeline.RENDER);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TileComponent tile = TileComponent.components.get(entity);
		Aiming tower = Aiming.components.get(entity);
		renderer.setColor(Color.BLUE);
		final float s = .25f;
		renderer.identity();
		renderer.rect(tile.x - s + .5f, tile.y - s + .5f, 2*s, 2*s);

		// render cannon
		renderer.setColor(Color.CYAN);
		renderer.translate(tile.x + .5f, tile.y + .5f, 0);
		renderer.rotate(0, 0, 1, tower.angle);
		renderer.rect(0, - s/4,  2*s, s/2);
	}
}
