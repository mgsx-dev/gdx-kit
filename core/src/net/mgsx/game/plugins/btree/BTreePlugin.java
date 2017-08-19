package net.mgsx.game.plugins.btree;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ai.btree.AnnotationBasedTaskCloner;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager;
import com.badlogic.gdx.ai.utils.random.FloatDistribution;
import com.badlogic.gdx.ai.utils.random.IntegerDistribution;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.helpers.EditorAssetManager.ReloadListener;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.storage.LoadConfiguration;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.plugins.btree.storage.BehaviorTreeSerializer;
import net.mgsx.game.plugins.btree.storage.EntityBehaviorTreeLoader;
import net.mgsx.game.plugins.btree.storage.EntityBehaviorTreeXMLLoader;
import net.mgsx.game.plugins.btree.tools.BTreeTool;
import net.mgsx.game.plugins.btree.ui.FloatDistributionEditor;
import net.mgsx.game.plugins.btree.ui.IntegerDistributionEditor;

// TODO should be separated in plugin and editor plugin ...
@PluginDef(category="behavior tree", components={BTreeModel.class}, 
	requires="com.badlogic.gdx.ai.GdxAI")
public class BTreePlugin extends EditorPlugin
{
	public final static BehaviorTreeRepository library = new BehaviorTreeRepository();
	
	private static ObjectMap<String, Class<? extends Task<EntityBlackboard>>> tasks;
	
	public static Array<String> allTasks(){
		scan();
		return tasks.keys().toArray();
	}
	public static Class<? extends Task<EntityBlackboard>> task(String type){
		scan();
		return tasks.get(type);
	}
	private static void scan(){
		if(tasks == null){
			tasks = new ObjectMap<String, Class<? extends Task<EntityBlackboard>>>();
			for(Class<? extends Task> type : ClassRegistry.instance.getSubTypesOf(Task.class)){
				
				String key = type.getName();
				tasks.put(key, (Class<? extends Task<EntityBlackboard>>)type);
			}
		}
	}
	
	
	// TODO
	// how to link behavior tree (typed) with entity/component :
	// solution 1 :
	// all Task apply to entity (which is good for reuse !)
	// a task adapter for easy coding
	
	public abstract static class EntityLeafTask extends LeafTask<EntityBlackboard>{
		@Override
		public Status execute() {
			return Status.SUCCEEDED;
		}
		@Override
		protected Task<EntityBlackboard> copyTo(Task<EntityBlackboard> task) {
			return task;
		}
		public Engine getEngine(){
			return getObject().engine;
		}
		public Entity getEntity(){
			return getObject().entity;
		}
	}
	
	@Override
	public void initialize(final EditorScreen editor) 
	{
		BehaviorTreeLibraryManager.getInstance().setLibrary(library);
		
		Task.TASK_CLONER = new AnnotationBasedTaskCloner();
		
		EntityEditor.defaultTypeEditors.put(FloatDistribution.class, new FloatDistributionEditor());
		EntityEditor.defaultTypeEditors.put(IntegerDistribution.class, new IntegerDistributionEditor());
		
		editor.assets.addReloadListener(BehaviorTree.class, new ReloadListener(){
			@Override
			public void reload(Object asset) {
				for(Entity e : editor.entityEngine.getEntitiesFor(Family.all(BTreeModel.class).get())){
					BTreeModel btree = BTreeModel.components.get(e);
					btree.tree = (BehaviorTree<EntityBlackboard>)asset;
				}
			}
		});
		
		editor.entityEngine.addSystem(new BTreeSystem(editor.game));
		
		editor.addTool(new BTreeTool(editor));
		
		editor.registry.registerPlugin(BTreeModel.class, new BTreeEditor(editor));
		
		editor.registry.addSerializer(BTreeModel.class, new BehaviorTreeSerializer(editor.entityEngine));
		
		LoadConfiguration config = new LoadConfiguration();
		config.assets = editor.assets;
		config.engine = editor.entityEngine;
		config.registry = editor.registry;
		
		editor.assets.setLoader(BehaviorTree.class, "btree", new EntityBehaviorTreeLoader(editor.assets.getFileHandleResolver(), config));
		editor.assets.setLoader(BehaviorTree.class, "xml", new EntityBehaviorTreeXMLLoader(editor.assets.getFileHandleResolver()));
	}
	
	
}
