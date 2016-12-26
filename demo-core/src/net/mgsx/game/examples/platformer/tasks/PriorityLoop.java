package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.gdx.ai.btree.BranchTask;
import com.badlogic.gdx.ai.btree.Task;

import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("priority")
public class PriorityLoop<E> extends BranchTask<E>{

	private int currentIndex = -1;
	
	@Override
	public void run() {
		for(int i=0 ; i<getChildCount() ; i+=3){
			Task<E> condition = getChild(i);
			
			if(condition.getStatus() != Status.RUNNING){
				condition.setControl(this);
				condition.start();
			}
			condition.run();
			
			if(condition.getStatus() == Status.SUCCEEDED){
				
				if(currentIndex >= 0 && currentIndex != i){
					
					Task<E> postChild = getChild(currentIndex+2);
					if(postChild.getStatus() != Status.RUNNING){
						postChild.setControl(this);
						postChild.start();
					}
					postChild.run();
					if(postChild.getStatus() == Status.RUNNING){
						break;
					}
					
					Task<E> previousTask = getChild(currentIndex+1);
					if(previousTask.getStatus() == Status.RUNNING){
						previousTask.cancel();
						previousTask.reset();
					}
				}
				
				currentIndex = i;
				Task<E> newChild = getChild(currentIndex+1);
				if(newChild.getStatus() != Status.RUNNING){
					newChild.setControl(this);
					newChild.start();
				}
				newChild.run();
				break;
			}
		}
		running();
	}

	@Override
	public void childSuccess(Task<E> task) {
		
	}

	@Override
	public void childFail(Task<E> task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void childRunning(Task<E> runningTask, Task<E> reporter) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void end() {
		currentIndex = -1;
		super.end();
	}

}
