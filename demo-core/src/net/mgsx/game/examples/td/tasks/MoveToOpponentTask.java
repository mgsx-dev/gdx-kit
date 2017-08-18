package net.mgsx.game.examples.td.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.examples.td.components.Direction;
import net.mgsx.game.examples.td.components.Range;
import net.mgsx.game.examples.td.systems.TargetSystem;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@TaskAlias("moveToEnemy")
public class MoveToOpponentTask extends EntityLeafTask
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
		if(targetPosition == null){
			Entity opponent = getEngine().getSystem(TargetSystem.class).findClosestOpponent(getEntity());
			if(opponent == null) return Status.FAILED;
			Transform2DComponent opponentTransform = Transform2DComponent.components.get(opponent);
			targetPosition = new Vector2(opponentTransform.position);
		}
		
		Transform2DComponent transform = Transform2DComponent.components.get(getEntity());
		
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
		
		targetPosition = null; // XXX option to recompute at each step !! could be greedy though!!)
		
		return Status.RUNNING;
	}
}
