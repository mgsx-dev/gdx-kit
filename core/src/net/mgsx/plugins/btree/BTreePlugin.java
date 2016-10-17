package net.mgsx.plugins.btree;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager;

import net.mgsx.core.Editor;
import net.mgsx.core.plugins.EditorPlugin;
import net.mgsx.core.storage.Storage;

public class BTreePlugin extends EditorPlugin
{
	// TODO
	// how to link behavior tree (typed) with entity/component :
	// solution 1 :
	// all Task apply to entity (which is good for reuse !)
	// a task adapter for easy coding
	
	public abstract static class EntityLeafTask extends LeafTask<Entity>{
		@Override
		public Status execute() {
			return null;
		}
		@Override
		protected Task<Entity> copyTo(Task<Entity> task) {
			return task;
		}
	}
	
	public abstract static class BTreeComponentTask<T extends Component> extends EntityLeafTask
	{
		private Class<T> type;
		public BTreeComponentTask(Class<T> type) {
			super();
			this.type = type;
		}
		@Override
		public Status execute() 
		{
			return execute(getObject().getComponent(type));
		}
		abstract public Status execute(T model);
		@Override
		protected Task<Entity> copyTo(Task<Entity> task) {
			return task;
		}
	}
	
	
	@Override
	public void initialize(Editor editor) 
	{
		Storage.register(BTreeModel.class, "btree");
	}
	
	
}
