package net.mgsx.game.core.storage;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;

public class EntityGroup {

	final Array<Entity> entities = new Array<Entity>();
	
	/**
	 * Create instances and add them to the engine
	 * @param assets
	 * @param engine
	 * @return the created entities
	 */
	public Array<Entity> create(AssetManager assets, Engine engine)
	{
		Array<Entity> entities = new Array<Entity>();
		EntityGroupStorage.create(entities, assets, engine, this, null);
		return entities;
	}


	public int size() {
		return entities.size;
	}


	public Entity find(int reference) {
		return entities.get(reference);
	}


	public void add(Entity entity) {
		entities.add(entity);
	}


	public Entity get(int index) {
		return entities.get(index);
	}
	
	public Array<Entity> entities(){
		return entities;
	}

}
