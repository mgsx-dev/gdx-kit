package net.mgsx.game.core;

/**
 * Kit game listener enable application to hook on main game lifecycle.
 * 
 * Register with {@link Kit#gameListeners}
 * 
 * @author mgsx
 *
 */
public interface KitGameListener {

	/**
	 * Called just before game exiting either as normal or before a crash.
	 * This give a chance to process errors just before a crash or persist data.
	 * This method is always called except for native code crash.
	 * @param error the unhandled error causing the game to crash or null for normal termination.
	 */
	public void exit(Throwable error);
	
}
