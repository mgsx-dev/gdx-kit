package net.mgsx.game.core.screen;

import com.badlogic.gdx.Screen;

/**
 * Common interface for transition effect between 2 screens.
 * 
 * @author mgsx
 *
 */
public interface ScreenTransition 
{
	/**
	 * Render transition between src and dst screen.
	 * @param src the source screen
	 * @param dst the destination screen
	 * @param deltaTime current delta time, used to render screens.
	 * @param t normalized time range from 0 to 1. Contract of this method is
	 * at t=0, src screen should be full rendered, at t=1, destination should be
	 * full rendered. In between, both screens may be rendered.
	 */
	void render(Screen src, Screen dst, float deltaTime, float t);

	void resize(int width, int height);
}
