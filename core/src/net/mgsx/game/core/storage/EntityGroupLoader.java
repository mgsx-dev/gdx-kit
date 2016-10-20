package net.mgsx.game.core.storage;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class EntityGroupLoader extends AsynchronousAssetLoader<EntityGroup, AssetLoaderParameters<EntityGroup>>
{

	public EntityGroupLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, AssetLoaderParameters<EntityGroup> parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters<EntityGroup> parameter) {
		// nothing to do.
	}

	@Override
	public EntityGroup loadSync(AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters<EntityGroup> parameter) {
		
		// TODO parse file ... json
		
		return null;
	}

}
