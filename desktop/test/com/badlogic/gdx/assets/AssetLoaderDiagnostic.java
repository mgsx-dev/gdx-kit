package com.badlogic.gdx.assets;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public class AssetLoaderDiagnostic {

	public static void main(String[] args) {
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		new LwjglApplication(new Game() {
			
			@Override
			public void create() {
				
				for(Entry<Class, ObjectMap<String,AssetLoader>> entry : new AssetManager().loaders.iterator()){
					System.out.println(entry.key.getName());
					System.out.println(entry.value.size);
				}
				
			}
		}, config);
	}
}
