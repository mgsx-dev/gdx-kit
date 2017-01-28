package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class EnemyRenderer extends AbstractShapeSystem
{
	public EnemyRenderer(GameScreen game) {
		super(game, Family.all(Enemy.class, Transform2DComponent.class).get(), GamePipeline.RENDER);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		final float s = .25f;
		renderer.setColor(Color.RED);
		renderer.rect(transform.position.x - s, transform.position.y - s, 2*s, 2*s);
	}
}
