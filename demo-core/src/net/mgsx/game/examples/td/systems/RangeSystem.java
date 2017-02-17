package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.MultiTarget;
import net.mgsx.game.examples.td.components.Range;
import net.mgsx.game.examples.td.components.SingleTarget;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class RangeSystem extends IteratingSystem
{
	public RangeSystem() {
		super(Family.all(Range.class, Transform2DComponent.class).one(SingleTarget.class, MultiTarget.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Range range = Range.components.get(entity);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		
		// check if target (if any) still in range
		// remove target(s) if not.
		SingleTarget targeting = SingleTarget.components.get(entity);
		if(targeting != null && targeting.target != null)
		{
			Transform2DComponent targetTransform = Transform2DComponent.components.get(targeting.target);
			if(targetTransform.position.dst2(transform.position) > range.distance * range.distance){
				targeting.target = null;
			}
		}
		
		// TODO support for multi targets
		
		
	}
}
