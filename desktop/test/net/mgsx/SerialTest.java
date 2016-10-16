package net.mgsx;

import java.io.StringReader;
import java.io.StringWriter;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.core.storage.Storage;
import net.mgsx.plugins.sprite.SpriteModel;

public class SerialTest {

	public static void main(String[] args) {

		LwjglNativesLoader.load();
		Gdx.files = new LwjglFiles();
		Gdx.gl = new GL20Fake();
		
		AssetManager assets = new AssetManager(){
			@Override
			public synchronized <T> T get(String fileName, Class<T> type) {
				// TODO Auto-generated method stub
				return (T) new Texture(new FileTextureData(this.getFileHandleResolver().resolve(fileName), null, null, false));
			}
		};
		String file = "../../presets/images.png";
		Texture tex = assets.get(file, Texture.class);
		
		Engine engine = new Engine();
		Entity entity = new Entity();
		engine.addEntity(entity);
		
		SpriteModel sm = new SpriteModel();
		sm.sprite = new Sprite(tex);
		sm.sprite.setX(45.76f);
		entity.add(sm);
		
		StringWriter writer = new StringWriter();
		Storage.save(engine, assets, writer, true, new ObjectMap<Class, Json.Serializer>());
		engine.removeAllEntities();
		
		Storage.load(engine, new StringReader(writer.toString()), assets, new ObjectMap<Class, Json.Serializer>());
		
		writer.append("\n\n");
		Storage.save(engine, assets, writer, true, new ObjectMap<Class, Json.Serializer>());
		
		
		System.out.println(writer.toString());
		
	}
}
