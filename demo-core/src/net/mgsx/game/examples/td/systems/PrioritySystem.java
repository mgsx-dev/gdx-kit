package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.MultiTarget;
import net.mgsx.game.examples.td.components.Priority;
import net.mgsx.game.examples.td.components.SingleTarget;

public class PrioritySystem extends IteratingSystem
{
	public PrioritySystem() {
		super(Family.all(Priority.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Priority priority = Priority.components.get(entity);
		if(priority.current != priority.rule){
			priority.current = priority.rule;
			
			SingleTarget singleTarget = SingleTarget.components.get(entity);
			if(singleTarget != null) singleTarget.target = null;
			
			MultiTarget multiTarget = MultiTarget.components.get(entity);
			if(multiTarget != null) multiTarget.targets.clear();
			
		}
	}
}
