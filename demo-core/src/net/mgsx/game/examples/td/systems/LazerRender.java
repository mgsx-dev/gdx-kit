package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Interpolation;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.td.components.Aiming;
import net.mgsx.game.examples.td.components.Lazer;
import net.mgsx.game.examples.td.components.SingleTarget;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class LazerRender extends AbstractShapeSystem
{
	public LazerRender(GameScreen game) {
	super(game, Family.all(Transform2DComponent.class, Lazer.class, SingleTarget.class).get(), GamePipeline.RENDER);
		renderer.setColor(1, 0, 0, .5f);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Transform2DComponent srcTransform = Transform2DComponent.components.get(entity);
		Lazer lazer = Lazer.components.get(entity);
		
		// case of single target
		SingleTarget targeting = SingleTarget.components.get(entity);
		if(targeting != null && targeting.target != null)
		{
			// case of aiming : only display when target in sights.
			Aiming aiming = Aiming.components.get(entity);
			if(aiming != null && !aiming.inSights) return;
			
			if(!lazer.active) return;
			
			float angle = aiming != null ? aiming.angle : 0; // TODO handle no aiming case
			Transform2DComponent dstTransform = Transform2DComponent.components.get(targeting.target);
			float dist = dstTransform.position.dst(srcTransform.position);
			
			float s = .2f * Interpolation.pow3Out.apply(lazer.charge / lazer.chargeMax);
			
			renderer.identity();
			renderer.translate(srcTransform.position.x, srcTransform.position.y, 0);
			renderer.rotate(0, 0, 1, angle);
			renderer.rect(0, - s/4,  dist, s/2);
			
			
			// renderer.line(srcTransform.position, dstTransform.position);
		}
		else
		{
			// case of multiple targets
			
		}
	}
	

}
