package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.td.components.Aiming;
import net.mgsx.game.examples.td.components.Freezer;
import net.mgsx.game.examples.td.components.Lazer;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class TowerRender extends AbstractShapeSystem
{

	public TowerRender(GameScreen game) {
		super(game, Family.all(Transform2DComponent.class, Aiming.class).get(), GamePipeline.RENDER);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		Aiming tower = Aiming.components.get(entity);
		
		Color canonColor, bodyColor;
		
		// choose a color
		if(Lazer.components.has(entity))
		{
			bodyColor = Color.BROWN;
			canonColor = Color.ORANGE;
		}
		else if(Freezer.components.has(entity))
		{
			bodyColor = Color.BLUE;
			canonColor = Color.CYAN;
		}
		else
		{
			// expect shooter
			bodyColor = Color.ORANGE;
			canonColor = Color.GOLD;
		}
		
		
		renderer.setColor(bodyColor);
		final float s = .25f;
		
		// render base
		renderer.identity();
		renderer.rect(transform.position.x - s, transform.position.y - s, 2*s, 2*s);

		// render cannon
		renderer.setColor(canonColor);
		renderer.translate(transform.position.x, transform.position.y, 0);
		renderer.rotate(0, 0, 1, tower.angle);
		renderer.rect(0, - s/4,  2*s, s/2);
	}
}
