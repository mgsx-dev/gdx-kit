package com.badlogic.gdx.ai.tests.btree.dog;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution;
import com.badlogic.gdx.ai.utils.random.IntegerDistribution;

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
		Dog dog = getObject().getComponent(Dog.class);
		for (int i = 0; i < t; i++)
			dog.bark();
		return Status.SUCCEEDED;
	}


	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
