package com.badlogic.gdx.ai.tests.btree.dog;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class RestTask extends LeafTask<Entity>
{

	@Override
	public void start () {
		getObject().getComponent(Dog.class).brainLog("YAWN - So tired...");
	}

	@Override
	public Status execute () {
		getObject().getComponent(Dog.class).brainLog("zz zz zz");
		return Status.RUNNING;
	}

	@Override
	public void end () {
		getObject().getComponent(Dog.class).brainLog("SOB - Time to wake up");
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
