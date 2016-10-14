package com.badlogic.gdx.ai.tests.btree.dog;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution;
import com.badlogic.gdx.ai.utils.random.IntegerDistribution;

import net.mgsx.fwk.editor.Entity;

public class BarkTask extends LeafTask<Entity>
{
	@TaskAttribute
	public IntegerDistribution times = ConstantIntegerDistribution.ONE;
	
	private int t;

	@Override
	public void start () {
		super.start();
		t = times.nextInt();
	}

	@Override
	public Status execute () {
		Dog dog = getObject().as(Dog.class);
		for (int i = 0; i < t; i++)
			dog.bark();
		return Status.SUCCEEDED;
	}


	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
