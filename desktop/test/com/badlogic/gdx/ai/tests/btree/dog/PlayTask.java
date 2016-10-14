package com.badlogic.gdx.ai.tests.btree.dog;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import net.mgsx.fwk.editor.Entity;

public class PlayTask extends LeafTask<Entity>{

	public void start () {
		Dog dog = getObject().as(Dog.class);
		dog.brainLog("WOW - Lets play!");
	}

	@Override
	public Status execute () {
		Dog dog = getObject().as(Dog.class);
		dog.brainLog("PANT PANT - So fun");
		return Status.RUNNING;
	}

	@Override
	public void end () {
		Dog dog = getObject().as(Dog.class);
		dog.brainLog("SIC - No time to play :(");
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
