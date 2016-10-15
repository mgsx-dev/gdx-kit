package net.mgsx.core;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import net.mgsx.core.plugins.Plugin;
import net.mgsx.core.plugins.StorablePlugin;

// TODO
// shoulb be a screen (because of multi screen features or multi viewport ...
// it only contains high performence / no editor stuff runtime!
public class GameEngine extends ApplicationAdapter
{

	protected ShapeRenderer shapeRenderer;
	protected SpriteBatch batch;
	protected Array<Plugin> plugins = new Array<Plugin>();
	public PooledEngine entityEngine;
	public OrthographicCamera orthographicCamera;
	public PerspectiveCamera perspectiveCamera;

	public void registerPlugin(Plugin plugin) {
		plugins.add(plugin);
	}

	private Json json;

	public <T> void registerPlugin(Class<T> type, StorablePlugin<T> plugin) {
		json.setSerializer(type, plugin);
	}
	
	@Override
	public void create() {
		super.create();
		
		entityEngine = new PooledEngine();
		orthographicCamera = new OrthographicCamera();
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		
	}

}
