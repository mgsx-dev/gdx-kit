package net.mgsx.game.core.storage;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Json.Serializer;

abstract public class AssetAwareSerializer<T> implements Serializer<T> 
{
	protected AssetManager assets;
	protected EntityGroupSerializer parent;
}
