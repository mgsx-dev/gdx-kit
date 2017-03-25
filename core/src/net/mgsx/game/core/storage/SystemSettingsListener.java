package net.mgsx.game.core.storage;

/**
 * interface for {@link com.badlogic.ashley.core.EntitySystem} requiring update after settings was loaded.
 * 
 * @author mgsx
 *
 */
public interface SystemSettingsListener {

	/**
	 * Called when settings has been loaded, called just after editable values has been changed.
	 */
	public void onSettingsLoaded();
}
