package net.mgsx.box2d.editor.desktop;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;

import net.mgsx.game.core.GameRegistry;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.storage.EntityGroup;
import net.mgsx.game.core.storage.EntityGroupLoader;
import net.mgsx.game.core.storage.EntityGroupLoaderParameters;

public class EntityGroupLoaderTest {

	
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Game(){
			
			private AssetManager assets;
			private AssetDescriptor<EntityGroup> asset;
			private int frames;
			private Engine engine;
			
			@Override
			public void create() 
			{
				// scan all components to configure serializers
				GameRegistry registry = new GameRegistry();
				registry.scanPackages();
				
				// create some entities
				
				// save them to temporary file
				
				// load this file
				assets = new AssetManager();
				assets.setLoader(EntityGroup.class, new EntityGroupLoader(assets.getFileHandleResolver()));
				
				engine = new Engine();
				GameScreen screen = new GameScreen(assets, engine);
				screen.registry = registry;
				
				registry.init(screen);
				
				FileHandle file = Gdx.files.classpath("egl-test1.json");
				EntityGroupLoaderParameters params = new EntityGroupLoaderParameters(registry);
				
				asset = new AssetDescriptor<EntityGroup>(file, EntityGroup.class, params);
				assets.load(asset);
			}
			
			@Override
			public void render() {
				if(assets.update()){
					// when loaded, print information and instanciate group several times.
					System.out.println("loaded in " + frames + " frames");
					EntityGroup eg = assets.get(asset);
					System.out.println("entities in group : " + eg.size());
					System.out.println("entities in engine : " + engine.getEntities().size());
					System.out.println("assets in manager (after load) : " + assets.getAssetNames().size);
					
					for(String name : assets.getAssetNames()){
						System.out.println("- " + name);
					}
					
					for(int i=0 ; i<3 ; i++){
						eg.create(assets, engine);
						System.out.println("entities in engine : " + engine.getEntities().size());
					}
					engine.removeAllEntities();
					eg.create(assets, engine);
					System.out.println("entities in engine : " + engine.getEntities().size());
					
					engine.removeAllEntities();
					
					assets.unload(asset.fileName);
					
					System.out.println("assets in manager (after unload) : " + assets.getAssetNames().size);
					for(String name : assets.getAssetNames()){
						System.out.println("- " + name);
					}
					
					Gdx.app.exit();
				}else{
					frames++;
				}
				super.render();
			}
		}, config);
	}
}
