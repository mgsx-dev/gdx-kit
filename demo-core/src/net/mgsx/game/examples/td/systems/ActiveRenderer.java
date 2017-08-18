package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.td.components.Active;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class ActiveRenderer extends AbstractShapeSystem
{

	public ActiveRenderer(GameScreen game) {
		super(game, Family.all(Transform2DComponent.class, Active.class).get(), GamePipeline.RENDER_DEBUG);
		shapeType = ShapeType.Line;
	}
	
	@Override
	public void update(float deltaTime) {
		renderer.setColor(Color.WHITE);
		super.update(deltaTime);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		renderer.circle(transform.position.x, transform.position.y, 1);
	}

}
