package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.SingleTarget;

public class SingleTargetSystem extends EntitySystem
{
	private ImmutableArray<Entity> targetingEntities;
	
	public SingleTargetSystem() {
		super(GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		// TODO could be optimized by storing link when singleTarget added (Map of entity Array)
		targetingEntities = engine.getEntitiesFor(Family.all(SingleTarget.class).get());
		engine.addEntityListener(new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				for(Entity targetingEntity : targetingEntities)
				{
					SingleTarget targetting = SingleTarget.components.get(targetingEntity);
					if(targetting.target == entity){
						// just remove reference since single target still aspect of entity but have to target another one.
						targetting.target = null;
					}
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
			}
		});
	}
}
