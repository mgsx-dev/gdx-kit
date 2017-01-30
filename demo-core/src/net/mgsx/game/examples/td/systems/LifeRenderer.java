package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.td.components.Life;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class LifeRenderer extends AbstractShapeSystem
{
	
	public LifeRenderer(GameScreen game) {
		super(game, Family.all(Life.class, Transform2DComponent.class).get(), GamePipeline.RENDER);
		renderer.setColor(Color.BLACK);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Transform2DComponent transform = Transform2DComponent.components.get(entity);

		float x = transform.position.x - .35f;
		float y = transform.position.y + .35f;
		float width = .5f;
		float height = .03f;
		
		float extra = .01f;
		
		// draw background
		renderer.rect(x - extra, y - extra, width + 2 * extra, height + 2 * extra);
		
		Life life = Life.components.get(entity);
		
		float lifeRate = MathUtils.clamp(life.current / life.max, 0, 1);
		
		// draw life
		renderer.rect(x, y, width * lifeRate, height, Color.YELLOW, Color.GREEN, Color.GREEN, Color.YELLOW);
	}
}
