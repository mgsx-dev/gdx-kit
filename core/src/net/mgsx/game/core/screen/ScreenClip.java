package net.mgsx.game.core.screen;

import com.badlogic.gdx.Screen;

/**
 * 
 * Screen considered as a clip (with an end).
 * 
 * These screens might be renderered whether clip is complete or not.
 * 
 * ScreenClip give a hint to screen manager whether the clip is finished and may switch to another screen.
 * 
 * @author mgsx
 *
 */
public interface ScreenClip extends Screen
{
	public boolean isComplete();
}
