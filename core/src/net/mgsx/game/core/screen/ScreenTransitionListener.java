package net.mgsx.game.core.screen;

/**
 * interface for screens that wants to listen to transition workflow event.
 * 
 * screen transition workflow from A to B is :
 * - A.preHide
 * - B.show
 * - ... transition ... A and B rendering
 * - A.hide
 * - B.postShow
 * 
 * @author mgsx
 *
 */
public interface ScreenTransitionListener {

	/**
	 * Called on the source screen when a transition begin.
	 * It is the right time to disable inputs.
	 */
	public void preHide();
	
	/**
	 * Called on the destination screen when a transition end.
	 * It is the right time to enable inputs.
	 */
	public void postShow();
}
