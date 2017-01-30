package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.Freezer;
import net.mgsx.game.examples.td.components.PathFollower;
import net.mgsx.game.examples.td.components.Range;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class FreezeSystem extends IteratingSystem
{
	private ImmutableArray<Entity> targets;
	
	public FreezeSystem() {
		super(Family.all(Freezer.class, Transform2DComponent.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) 
	{
		super.addedToEngine(engine);
		targets = engine.getEntitiesFor(Family.all(Enemy.class, Transform2DComponent.class, PathFollower.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Range range = Range.components.get(entity);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		Freezer freezer = Freezer.components.get(entity);
		
		for(Entity target : targets)
		{
			Enemy enemy = Enemy.components.get(target);
			PathFollower path = PathFollower.components.get(target);
			if(range != null)
			{
				Transform2DComponent targetTransform = Transform2DComponent.components.get(target);
				if(targetTransform.position.dst2(transform.position) > range.distance * range.distance)
				{
					path.speed = enemy.speed;
					continue;
				}
			}
			path.speed = enemy.speed * freezer.force;
		}
	}
}
