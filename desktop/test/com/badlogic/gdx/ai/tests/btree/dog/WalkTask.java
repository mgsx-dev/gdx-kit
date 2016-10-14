package com.badlogic.gdx.ai.tests.btree.dog;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import net.mgsx.fwk.editor.Entity;

public class WalkTask extends LeafTask<Entity>
{

	private int i = 0;

	@Override
	public void start () {
		i = 0;
		Dog dog = getObject().as(Dog.class);
		dog.startWalking();
	}

	@Override
	public Status execute () {
		i++;
		Dog dog = getObject().as(Dog.class);
		dog.randomlyWalk();
		return i < 3 ? Status.RUNNING : Status.SUCCEEDED;
	}

	@Override
	public void end () {
		getObject().as(Dog.class).stopWalking();
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
