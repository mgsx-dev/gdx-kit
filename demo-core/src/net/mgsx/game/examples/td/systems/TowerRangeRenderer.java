package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.td.components.TileComponent;
import net.mgsx.game.examples.td.components.Tower;

public class TowerRangeRenderer extends AbstractShapeSystem
{

	public TowerRangeRenderer(GameScreen game) {
		super(game, Family.all(TileComponent.class, Tower.class).get(), GamePipeline.RENDER);
	}
	
	@Override
	public void update(float deltaTime) {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		super.update(deltaTime);
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TileComponent tile = TileComponent.components.get(entity);
		Tower tower = Tower.components.get(entity);
		renderer.setColor(Color.BLUE);
		renderer.getColor().a = .2f;
		renderer.identity();
		renderer.circle(tile.x + .5f, tile.y + .5f, tower.range, 16);

	}
}
