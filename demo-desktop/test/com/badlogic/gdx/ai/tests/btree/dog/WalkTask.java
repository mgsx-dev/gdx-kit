package com.badlogic.gdx.ai.tests.btree.dog;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class WalkTask extends LeafTask<Entity>
{

	private int i = 0;

	@Override
	public void start () {
		i = 0;
		Dog dog = getObject().getComponent(Dog.class);
		dog.startWalking();
	}

	@Override
	public Status execute () {
		i++;
		Dog dog = getObject().getComponent(Dog.class);
		dog.randomlyWalk();
		return i < 3 ? Status.RUNNING : Status.SUCCEEDED;
	}

	@Override
	public void end () {
		getObject().getComponent(Dog.class).stopWalking();
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
