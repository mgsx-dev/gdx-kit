package net.mgsx.game.examples.td.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.examples.td.components.Direction;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.Entry;
import net.mgsx.game.examples.td.components.Home;
import net.mgsx.game.examples.td.components.Range;
import net.mgsx.game.examples.td.components.TileComponent;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@TaskAlias("moveToHome")
public class MoveToHomeTask extends EntityLeafTask
{
	private Vector2 targetPosition;
	
	@Override
	public void reset() {
		super.reset();
		targetPosition = null;
	}
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() 
	{
		if(targetPosition == null)
		{
			boolean isEnemy = Enemy.components.has(getEntity());
			Entity target;
			if(isEnemy)
			{
				// go to closest entry
				ImmutableArray<Entity> entries = getEngine().getEntitiesFor(Family.all(Entry.class).get());
				if(entries.size() == 0) return Status.FAILED;
				
				// TODO get closest
				target = entries.first();
			}
			else
			{
				// go to closest home
				ImmutableArray<Entity> entries = getEngine().getEntitiesFor(Family.all(Home.class).get());
				if(entries.size() == 0) return Status.FAILED;
				
				// TODO get closest
				target = entries.first();
			}
			
			Transform2DComponent targetTransform = Transform2DComponent.components.get(target);
			if(targetTransform != null)
			{
				targetPosition = new Vector2(targetTransform.position);
			}
			else
			{
				TileComponent tile = TileComponent.components.get(target);
				if(tile == null) return Status.FAILED;
				targetPosition = new Vector2(tile.x + .5f, tile.y + .5f);
			}
		}
		
		Transform2DComponent transform = Transform2DComponent.components.get(getEntity());
		
		// TODO refactor with move to opponent ...
		float d = .1f;
		Range range = Range.components.get(getEntity());
		if(range != null) d = range.distance / 2;
		
		if(transform.position.dst2(targetPosition) < d * d)
		{
			getEntity().remove(Direction.class);
			targetPosition = null;
			return Status.SUCCEEDED;
		}
		
		Direction direction = Direction.components.get(getEntity());
		if(direction == null){
			direction = getEngine().createComponent(Direction.class);
			getEntity().add(direction);
		}
		
		direction.vector.set(targetPosition).sub(transform.position).nor();
		
		return Status.RUNNING;
		
	}
}
