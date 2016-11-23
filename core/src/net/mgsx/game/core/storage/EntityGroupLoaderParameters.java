package net.mgsx.game.core.storage;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.GameRegistry;

public class EntityGroupLoaderParameters extends AssetLoaderParameters<EntityGroup>{
	EntityGroup entityGroup;
	JsonValue jsonData;
	final public GameRegistry registry;
	
	public EntityGroupLoaderParameters(GameRegistry registry) {
		super();
		this.registry = registry;
	}
	
	
}