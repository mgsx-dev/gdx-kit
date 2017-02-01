package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.td.components.Lazer;
import net.mgsx.game.examples.td.components.SingleTarget;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class LazerRender extends AbstractShapeSystem
{
	public LazerRender(GameScreen game) {
		super(game, Family.all(Transform2DComponent.class, Lazer.class, SingleTarget.class).get(), GamePipeline.RENDER);
		shapeType = ShapeType.Line;
		renderer.setColor(1, 0, 0, .5f);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Transform2DComponent srcTransform = Transform2DComponent.components.get(entity);
		SingleTarget targeting = SingleTarget.components.get(entity);
		if(targeting.target != null)
		{
			Transform2DComponent dstTransform = Transform2DComponent.components.get(targeting.target);
			renderer.line(srcTransform.position, dstTransform.position);
		}
	}
	

}
