package net.mgsx.box2d.editor.behavior.tasks;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.physics.box2d.Body;

public class IsOnGround extends LeafTask<Body>
{

	@Override
	public Status execute() 
	{
		Body body = getObject();
		
		// TODO test if ray cast is ok (need to be stored once to avoid perf issues ...)
		
		return Status.SUCCEEDED;
	}

	@Override
	protected Task<Body> copyTo(Task<Body> task) {
		return task;
	}

}
