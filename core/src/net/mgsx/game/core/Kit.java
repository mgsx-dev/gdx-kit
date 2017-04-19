package net.mgsx.game.core;

import com.badlogic.gdx.InputMultiplexer;

import net.mgsx.game.core.meta.KitMeta;
import net.mgsx.game.core.meta.ReflectionCache;

public class Kit {

	public static final InputMultiplexer inputs = new InputMultiplexer();
	
	public static final KitMeta meta = new ReflectionCache();
}
