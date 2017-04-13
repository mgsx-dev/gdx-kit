package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.td.components.Range;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class TowerRangeRendererDebug extends AbstractShapeSystem
{

	public TowerRangeRendererDebug(GameScreen game) {
		super(game, Family.all(Transform2DComponent.class, Range.class).get(), GamePipeline.RENDER_TRANSPARENT);
	}
	
	@Override
	public void update(float deltaTime) {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		super.update(deltaTime);
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		Range range = Range.components.get(entity);
		renderer.setColor(0,0,1.01f,1);
		renderer.getColor().a = 0.1f;
		renderer.identity();
		renderer.circle(transform.position.x, transform.position.y, range.distance, 16);
	}
}
