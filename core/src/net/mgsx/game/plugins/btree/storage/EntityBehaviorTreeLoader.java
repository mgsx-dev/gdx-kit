package net.mgsx.game.plugins.btree.storage;

import java.lang.reflect.Field;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLoader;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.annotations.TaskAsset;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.storage.EntityGroup;
import net.mgsx.game.core.storage.EntityGroupLoaderParameters;
import net.mgsx.game.core.storage.LoadConfiguration;

public class EntityBehaviorTreeLoader extends BehaviorTreeLoader {

	private BehaviorTree tree;
	private LoadConfiguration config;
	
	public EntityBehaviorTreeLoader(FileHandleResolver resolver, LoadConfiguration config) {
		super(resolver);
		this.config = config;
	}
	
	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, BehaviorTreeParameter parameter) {
		// nothing to do.
	}
	
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, BehaviorTreeParameter parameter) {
		if(parameter == null){
			parameter = new BehaviorTreeParameter();
		}
		// register in library manager
		super.loadAsync(null, fileName, file, parameter);
		tree = super.loadSync(null, fileName, file, parameter);
		
		Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
		findReferences(deps, tree);
		return deps;
	}
	
	private void findReferences(Array<AssetDescriptor> deps, Task task){
		for(Field field : task.getClass().getFields()){
			TaskAsset asset = field.getAnnotation(TaskAsset.class);
			if(asset != null && field.getType() == String.class){
				String fileName = ReflectionHelper.get(task, field, String.class);
				if(fileName != null){
					AssetLoaderParameters parameters = null;
					if(asset.value() == EntityGroup.class){
						EntityGroupLoaderParameters egsp = new EntityGroupLoaderParameters();
						egsp.config = config;
						parameters = egsp;
					}
					deps.add(new AssetDescriptor(fileName, asset.value(), parameters)); 
				}
				// TODO else warning ?
			}
		}
		
		for(int i=0 ; i<task.getChildCount() ; i++)
			findReferences(deps, task.getChild(i));
	}
	
	@Override
	public BehaviorTree loadSync(AssetManager manager, String fileName, FileHandle file,
			BehaviorTreeParameter parameter) {
		// register in library manager (make it available to render thread)
		BehaviorTreeLibraryManager.getInstance().getLibrary().registerArchetypeTree(fileName, tree);
		BehaviorTree bundle = tree;
		tree = null;
		return bundle;
	}

}
