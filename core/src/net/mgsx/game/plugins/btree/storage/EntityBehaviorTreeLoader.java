package net.mgsx.game.plugins.btree.storage;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLoader;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class EntityBehaviorTreeLoader extends BehaviorTreeLoader {

	public EntityBehaviorTreeLoader(FileHandleResolver resolver) {
		super(resolver);
	}
	
	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, BehaviorTreeParameter parameter) {
		if(parameter == null){
			parameter = new BehaviorTreeParameter();
		}
		super.loadAsync(manager, fileName, file, parameter);
	}
	
	@Override
	public BehaviorTree loadSync(AssetManager manager, String fileName, FileHandle file,
			BehaviorTreeParameter parameter) {
		if(parameter == null){
			parameter = new BehaviorTreeParameter();
		}
		// register in library manager
		BehaviorTree tree = super.loadSync(manager, fileName, file, parameter);
		BehaviorTreeLibraryManager.getInstance().getLibrary().registerArchetypeTree(fileName, tree);
		return tree;
	}

}
