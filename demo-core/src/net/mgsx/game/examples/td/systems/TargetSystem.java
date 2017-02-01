package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.MultiTarget;
import net.mgsx.game.examples.td.components.SingleTarget;

public class TargetSystem extends EntitySystem
{
	public TargetSystem() {
		super(GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		// OPTIM could be optimized by storing link when singleTarget added (Map of entity Array)
		final ImmutableArray<Entity> singleTargetingEntities = engine.getEntitiesFor(Family.all(SingleTarget.class).get());
		engine.addEntityListener(new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				for(Entity targetingEntity : singleTargetingEntities)
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
		
		final ImmutableArray<Entity> multiTargetingEntities = engine.getEntitiesFor(Family.all(MultiTarget.class).get());
		engine.addEntityListener(new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				for(Entity targetingEntity : multiTargetingEntities)
				{
					MultiTarget targetting = MultiTarget.components.get(targetingEntity);
					targetting.targets.removeValue(entity, true);
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
			}
		});
	}
}
