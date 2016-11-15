package net.mgsx.game.core.helpers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import net.mgsx.game.core.helpers.EditorAssetManager.ReloadListener;

abstract public class EntityReloader<T> implements ReloadListener<T>
{
	private ImmutableArray<Entity> entities;
	
	public EntityReloader(Engine engine, Family family) {
		super();
		entities = engine.getEntitiesFor(family);
	}

	@Override
	public void reload(T asset) {
		
		for(Entity entity : entities)
		{
			reload(entity, asset);
		}
	}

	protected abstract void reload(Entity entity, T asset);

}
