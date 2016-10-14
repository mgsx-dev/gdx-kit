package com.badlogic.gdx.ai.tests.btree.dog;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class PlayTask extends LeafTask<Entity>{

	public void start () {
		Dog dog = getObject().getComponent(Dog.class);
		dog.brainLog("WOW - Lets play!");
	}

	@Override
	public Status execute () {
		Dog dog = getObject().getComponent(Dog.class);
		dog.brainLog("PANT PANT - So fun");
		return Status.RUNNING;
	}

	@Override
	public void end () {
		Dog dog = getObject().getComponent(Dog.class);
		dog.brainLog("SIC - No time to play :(");
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
