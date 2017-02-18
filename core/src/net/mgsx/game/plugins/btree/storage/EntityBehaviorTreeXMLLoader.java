package net.mgsx.game.plugins.btree.storage;

import java.lang.reflect.Field;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.annotations.TaskAsset;
import net.mgsx.game.core.helpers.ReflectionHelper;

public class EntityBehaviorTreeXMLLoader<T> extends AsynchronousAssetLoader<BehaviorTree<T>, AssetLoaderParameters<BehaviorTree<T>>> {

	private BehaviorTree tree;
	
	public EntityBehaviorTreeXMLLoader(FileHandleResolver resolver) {
		super(resolver);
	}
	
	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters<BehaviorTree<T>> parameter) {
		// nothing to do.
	}
	
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, AssetLoaderParameters<BehaviorTree<T>> parameter) {
		
		// read tree
		tree = new BehaviorTree<T>();
		
		new BehaviorTreeXmlReader().read(tree, file.reader());
		
		Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
		findReferences(deps, tree);
		return deps;
	}
	
	private void findReferences(Array<AssetDescriptor> deps, Task task){
		for(Field field : task.getClass().getFields()){
			TaskAsset asset = field.getAnnotation(TaskAsset.class);
			if(asset != null && field.getType() == String.class){
				String fileName = ReflectionHelper.get(task, field, String.class);
				deps.add(new AssetDescriptor(fileName, asset.value()));
			}
		}
		
		for(int i=0 ; i<task.getChildCount() ; i++)
			findReferences(deps, task.getChild(i));
	}
	
	@Override
	public BehaviorTree loadSync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<BehaviorTree<T>> parameter) {
		// register in library manager (make it available to render thread)
		BehaviorTreeLibraryManager.getInstance().getLibrary().registerArchetypeTree(fileName, tree);
		BehaviorTree bundle = tree;
		tree = null;
		return bundle;
	}

}
