package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;

import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.EntityBlackboard;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@TaskAlias("animation")
public class AnimationTask extends EntityLeafTask implements AnimationListener
{
	@TaskAttribute(required=true)
	public String id;
	
	@TaskAttribute
	public int loops = 1;

	@TaskAttribute
	public float speed = 1;

	@TaskAttribute
	public float fade = 0;

	public boolean end = false;
	
	@Override
	public void start() {
		G3DModel model = G3DModel.components.get(getObject().entity);
		if(model != null){
			end = false;
			model.animationController.paused = false;
			model.animationController.allowSameAnimation = true;
			model.animationController.animate(id, loops, speed, this, fade);
		}
	}
	
	@Override
	public Status execute() 
	{
		// XXX bug with animation : onEnd not call if not remain ...
		G3DModel model = G3DModel.components.get(getObject().entity);
		
		end |= model.animationController.current.speed == 0;
		return end ? Status.SUCCEEDED : Status.RUNNING;
	}
	
	@Override
	protected Task<EntityBlackboard> copyTo(Task<EntityBlackboard> task) {
		AnimationTask clone = (AnimationTask)task;
		clone.fade = fade;
		clone.id = id;
		clone.loops = loops;
		clone.speed = speed;
		return super.copyTo(task);
	}

	@Override
	public void onEnd(AnimationDesc animation) {
		end = true;
	}

	@Override
	public void onLoop(AnimationDesc animation) {
	}
}
