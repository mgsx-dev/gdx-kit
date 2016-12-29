package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;

abstract public class AbstractTimeTask extends EntityLeafTask
{
	protected float time;
	
	@TaskAttribute
	protected float duration = 1;
	
	@Override
	public void start() {
		time = 0;
	}
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() 
	{
		time += GdxAI.getTimepiece().getDeltaTime();
		if(time > duration){
			update(1);
			return Status.SUCCEEDED;
		}
		update(time / duration);
		return Status.RUNNING;
	}
	
	/**
	 * update with normalized time.
	 * @param t
	 */
	abstract protected void update(float t);
}
