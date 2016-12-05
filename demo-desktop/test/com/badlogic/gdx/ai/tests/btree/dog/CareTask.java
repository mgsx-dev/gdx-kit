package com.badlogic.gdx.ai.tests.btree.dog;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

public class CareTask extends LeafTask<Entity>
{
	@TaskAttribute(required = true) 
	public float urgentProb;
	
	@Override
	public Status execute() {
		if (Math.random() < urgentProb) {
			return Status.SUCCEEDED;
		}
		Dog dog = getObject().getComponent(Dog.class);
		dog.brainLog("GASP - Something urgent :/");
		return Status.FAILED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
