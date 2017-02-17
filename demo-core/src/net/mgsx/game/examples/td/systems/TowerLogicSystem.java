package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Aiming;
import net.mgsx.game.examples.td.components.SingleTarget;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

// TODO rename aiming
public class TowerLogicSystem extends IteratingSystem
{
	public TowerLogicSystem() {
		super(Family.all(Aiming.class, SingleTarget.class, Transform2DComponent.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Aiming tower = Aiming.components.get(entity);
		SingleTarget targeting = SingleTarget.components.get(entity);
		final Transform2DComponent towerTransform = Transform2DComponent.components.get(entity);
		
		// TODO should be in target logic system
		// find best target if no targets
		
		
		// TODO aiming system ?
		// rotate canon to current target if any
		if(targeting.target != null)
		{
			Transform2DComponent targetTransform = Transform2DComponent.components.get(targeting.target);
			float angle = MathUtils.atan2(targetTransform.position.y - towerTransform.position.y, targetTransform.position.x - towerTransform.position.x) * MathUtils.radiansToDegrees;
			
			// rotate to target
			float deltaAngle = (angle - tower.angle);
			if(deltaAngle > 180){
				deltaAngle -= 360;
			}else if(deltaAngle < -180){
				deltaAngle += 360;
			}
			
			
			float angularVelocityMin = -tower.angleVelocity * deltaTime;
			float angularVelocityMax = tower.angleVelocity * deltaTime;
			
			if(deltaAngle > angularVelocityMax) deltaAngle = angularVelocityMax;
			else if(deltaAngle < angularVelocityMin) deltaAngle = angularVelocityMin;
			tower.angle += deltaAngle;
			
			deltaAngle = (angle - tower.angle);
			if(deltaAngle > 180){
				deltaAngle -= 360;
			}else if(deltaAngle < -180){
				deltaAngle += 360;
			}
			tower.inSights = deltaAngle == 0;
		}
		
	}
}
