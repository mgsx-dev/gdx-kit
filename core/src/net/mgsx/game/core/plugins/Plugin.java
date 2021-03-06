package net.mgsx.game.core.plugins;

import net.mgsx.game.core.GameScreen;

/**
 * Base plugin runtime
 */
public interface Plugin {

	/**
	 * Good place to :
	 * <br>
	 * {@link GameScreen#addSerializer(Class, com.badlogic.gdx.utils.Json.Serializer)}
	 * <br>
	 * A good place to configure storage :
	 * {@link Storage#register(Class, String)}
	 * and {@link Storage#register(net.mgsx.game.core.storage.AssetSerializer)}
	 * <br>
	 * A good place to add EntitySystem to engine and listener to engine
	 * 
	 * 
	 * @param engine on which initialize plugin.
	 */
	public void initialize(GameScreen engine);
}
