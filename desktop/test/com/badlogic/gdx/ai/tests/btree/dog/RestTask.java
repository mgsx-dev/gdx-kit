package com.badlogic.gdx.ai.tests.btree.dog;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import net.mgsx.fwk.editor.Entity;

public class RestTask extends LeafTask<Entity>
{

	@Override
	public void start () {
		getObject().as(Dog.class).brainLog("YAWN - So tired...");
	}

	@Override
	public Status execute () {
		getObject().as(Dog.class).brainLog("zz zz zz");
		return Status.RUNNING;
	}

	@Override
	public void end () {
		getObject().as(Dog.class).brainLog("SOB - Time to wake up");
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
