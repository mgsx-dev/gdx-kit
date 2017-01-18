package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("waitAbsolute")
public class WaitAbsolute<T> extends LeafTask<T>
{
	@TaskAttribute
	public int steps = 2;
	
	@TaskAttribute
	public int step = 0;
	
	@TaskAttribute
	public float scale = 1;
	
	

	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() {
		if(MathUtils.floor(GdxAI.getTimepiece().getTime() * scale) % steps == step){
			return Status.SUCCEEDED;
		}
		return Status.RUNNING;
	}

	@Override
	protected Task<T> copyTo(Task<T> task) {
		WaitAbsolute<T> clone = (WaitAbsolute<T>)task;
		clone.steps = steps;
		clone.step = step;
		clone.scale = scale;
		return clone;
	}

}
