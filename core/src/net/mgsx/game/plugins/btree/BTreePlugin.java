package net.mgsx.game.plugins.btree;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLoader;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.btree.tools.BTreeTool;

@PluginDef(components={BTreeModel.class})
public class BTreePlugin extends EditorPlugin
{
	// TODO
	// how to link behavior tree (typed) with entity/component :
	// solution 1 :
	// all Task apply to entity (which is good for reuse !)
	// a task adapter for easy coding
	
	public abstract static class EntityLeafTask extends LeafTask<EntityBlackboard>{
		@Override
		public Status execute() {
			return null;
		}
		@Override
		protected Task<EntityBlackboard> copyTo(Task<EntityBlackboard> task) {
			return task;
		}
		public Engine getEngine(){
			return getObject().engine;
		}
	}
	
	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.entityEngine.addSystem(new BTreeSystem(editor.game));
		
		editor.addTool(new BTreeTool(editor));
		
		editor.registry.registerPlugin(BTreeModel.class, new BTreeEditor());
		
		
		editor.assets.setLoader(BehaviorTree.class, new BehaviorTreeLoader(editor.assets.getFileHandleResolver()));
	}
	
	
}
