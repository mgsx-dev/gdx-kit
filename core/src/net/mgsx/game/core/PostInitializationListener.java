package net.mgsx.game.core;

/**
 * Systems and tools may listen to post initialization phase.
 * see {@link #onPostInitialization()}
 * 
 * @author mgsx
 *
 */
public interface PostInitializationListener 
{
	/**
	 * Called just before first execution, when the containing screen show up.
	 * All required assets are loaded. Settings are loaded.
	 * All fields annotated with {@link net.mgsx.game.core.annotations.Inject} and
	 * {@link net.mgsx.game.core.annotations.Asset} are ready to be used.
	 * This post initialization phase is the good place to initialize data based on injected assets
	 * or other systems.
	 */
	public void onPostInitialization();
}
