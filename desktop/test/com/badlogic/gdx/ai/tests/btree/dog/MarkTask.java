package com.badlogic.gdx.ai.tests.btree.dog;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import net.mgsx.fwk.editor.Entity;

public class MarkTask extends LeafTask<Entity>
{

	int i;

	@Override
	public void start () {
		i = 0;
		getObject().as(Dog.class).log("Dog lifts a leg and pee!");
	}

	@Override
	public Status execute () {
		Dog dog = getObject().as(Dog.class);
		Boolean result = dog.markATree(i++);
		if (result == null) {
			return Status.RUNNING;
		}
		return result ? Status.SUCCEEDED : Status.FAILED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
