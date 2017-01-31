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
		Enemy enemy = Enemy.components.get(entity);
		final float s = .25f;
		
		renderer.identity();
		renderer.translate(transform.position.x, transform.position.y, 0);
		renderer.rotate(0, 0, 1, transform.angle);
		
		switch(enemy.type){
		case CIRCLE:
			renderer.setColor(Color.CYAN);
			renderer.circle(0, 0, s, 12);
			break;
		case HEXAGON:
			renderer.setColor(Color.PURPLE);
			renderer.circle(0, 0, s, 6);
			break;
		case PENTAGON:
			renderer.setColor(Color.ORANGE);
			renderer.circle(0, 0, s, 5);
			break;
		default:
		case SQUARE:
			renderer.setColor(Color.RED);
			renderer.rect(- s, - s, 2*s, 2*s);
			break;
		case TRIANGLE:
			renderer.setColor(Color.YELLOW);
			renderer.circle(0, 0, s, 3);
			break;
		}
		
	}
}
