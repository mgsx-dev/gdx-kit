package net.mgsx.game.core.helpers;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class EditorAssetManager extends AssetManager
{
	public static interface AssetManagerListener{

		void added(String fileName, Class type);

		void removed(String fileName);

		void changed(String fileName);
		
	}
	
	public static interface ReloadListener<T>
	{
		public void reload(T asset);
	}
	
	public EditorAssetManager() {
		super();
	}
	
	public EditorAssetManager(FileHandleResolver resolver) {
		super(resolver);
	}


	private Array<AssetManagerListener> listeners = new Array<AssetManagerListener>();
	private TypeGroup<ReloadListener> reloadListeners = new TypeGroup<ReloadListener>();
	
	
	
	public void addListener(AssetManagerListener listener){
		listeners.add(listener);
	}
	public void removeListener(AssetManagerListener listener){
		listeners.removeValue(listener, true);
	}
	
	public <T> void addReloadListener(Class<T> type, ReloadListener<T> listener){
		reloadListeners.addFor(type, listener);
	}
	public void removeReloadListener(Class type, ReloadListener listener){
		reloadListeners.removeFor(type, listener, true);
	}
	
	
	
	@Override
	protected <T> void addAsset(String fileName, Class<T> type, T asset) {
		super.addAsset(fileName, type, asset);
		
		// TODO see if exists ?
		for(AssetManagerListener listener : listeners) listener.added(fileName, type);
	}
	
	private ObjectMap<String, AssetDescriptor> loadings = new ObjectMap<String, AssetDescriptor>();
	
	@Override
	public synchronized <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
		AssetDescriptor<T> desc = new AssetDescriptor<T>(fileName, type, parameter);
		loadings.put(fileName, desc);
		super.load(fileName, type, parameter);
	}
	
	@Override
	public synchronized void unload(String fileName) {
		super.unload(fileName);
		if(!isLoaded(fileName)){
			for(AssetManagerListener listener : listeners) listener.removed(fileName);
		}else{
			for(AssetManagerListener listener : listeners) listener.changed(fileName);
		}
	}
	
	public void reload(String fileName) 
	{
		Class type = getAssetType(fileName);
		if(getReferenceCount(fileName) > 1){
			System.err.println("!!!"); return;
		}
		unload(fileName);
		
		Object asset = AssetHelper.loadAssetNow(this, fileName, type);
		for(ReloadListener listener : reloadListeners.getFor(type)){
			listener.reload(asset);
		}
		for(AssetManagerListener listener : listeners) listener.changed(fileName);
	}
	
}
