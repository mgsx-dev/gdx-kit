package net.mgsx.game.examples.td.tasks;

import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.examples.td.components.PathFollower;
import net.mgsx.game.examples.td.systems.NavSystem;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@TaskAlias("randomPath")
public class PathTask extends EntityLeafTask
{
	@Override
	public void start() {
		
		getEntity().remove(PathFollower.class);
		
		// path find
		NavSystem navSystem = getEngine().getSystem(NavSystem.class);
		Transform2DComponent transform = Transform2DComponent.components.get(getEntity());
		
		navSystem.randomPath(getEntity(), new Vector3(transform.position, transform.depth+1), new Vector3(0,0,-1));
	}
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() 
	{
		// TODO check if spline complete ...
		PathFollower follower = PathFollower.components.get(getEntity());
		if(follower == null)
			return Status.FAILED;
		if(follower.t >= 1)
			return Status.SUCCEEDED;
		return Status.RUNNING;
	}
}
