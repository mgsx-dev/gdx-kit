package net.mgsx.game.core.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

/**
 * Static convenience methods for screen transitions, inspired by scene2D {@link Actions}
 * 
 * @author mgsx
 *
 */
public class Transitions 
{
	private static class ColorScreen extends ScreenClipAdapter {
		private final Color color;

		public ColorScreen(Color color) {
			this.color = color;
		}

		@Override
		public void render(float delta) {
			Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		}

		@Override
		public boolean isComplete() {
			return true;
		}
	}

	private static class FadeTransition extends ShaderTransition{
		public FadeTransition() {
			super(new ShaderProgram(
					Gdx.files.classpath("shaders/gdx-default.vert"),
					Gdx.files.classpath("shaders/transition-fade.frag")
					));
		}
	}
	
	private static class SwapTransition implements ScreenTransition{

		@Override
		public void render(Screen src, Screen dst, float deltaTime, float t) {
			dst.render(deltaTime);
		}

		@Override
		public void resize(int width, int height) {
		}
	}
	
	private static FadeTransition fadeTransition;
	public static ScreenTransition fadeTransition() 
	{
		if(fadeTransition == null){
			fadeTransition = new FadeTransition();
		}
		return fadeTransition;
	}
	
	private static SwapTransition swapTransition;
	public static ScreenTransition swapTransition() 
	{
		if(swapTransition == null){
			swapTransition = new SwapTransition();
		}
		return swapTransition;
	}
	
	public static TransitionDesc fade(Screen destination, float duration){
		return fade(destination, duration, null);
	} 
	public static TransitionDesc fade(Screen destination, float duration, TransitionListener listener) 
	{
		return fade(destination, duration, Interpolation.linear, listener);
	}
	public static TransitionDesc fade(Screen destination, float duration, Interpolation interpolation, TransitionListener listener) 
	{
		TransitionDesc desc = new TransitionDesc();
		desc.destination = destination;
		desc.duration = duration;
		desc.interpolation = interpolation;
		desc.transition = fadeTransition();
		desc.listener = listener;
		return desc;
	}
	
	public static TransitionDesc swap(Screen destination) 
	{
		TransitionDesc desc = new TransitionDesc();
		desc.destination = destination;
		desc.transition = swapTransition();
		return desc;
	}
	
	/**
	 * Build up a loader upon a screen. Created screen behave same as underlying screen
	 * exept that it is complete when both underlying screen is complete and asset manager
	 * is finish loading.
	 * @param assets asset manager to check.
	 * @param screen underlying screen
	 * @return the loader screen.
	 */
	public static ScreenClip loader(final AssetManager assets, final Screen screen)
	{
		return new ScreenClipDelegate(screen){
			@Override
			public boolean isComplete() {
				return super.isComplete() && assets.update();
			}
		};
	}
	
	/**
	 * Wrap a screen with timeout behavior : screen is complete when timeout is reached,
	 * whenether underlying screen is complete or not.
	 * @param screen
	 * @param duration
	 * @return
	 */
	public static ScreenClip timeout(final Screen screen, final float duration)
	{
		return new ScreenClipDelegate(screen){
			private float time = 0;
			@Override
			public void render(float delta) {
				super.render(delta);
				time += delta;
			}
			@Override
			public boolean isComplete() {
				return time >= duration;
			}
		};
	}
	
	
	public static ScreenClip empty(final Color color){
		return new ColorScreen(color);
	}

	public static Screen queue(final ScreenClip a, final Screen b) 
	{
		return new TransitionScreen(){
			
			@Override
			public void render(float deltaTime) {
				if(a.isComplete()){
					b.render(deltaTime);
				}else{
					a.render(deltaTime);
				}
			}
			
			@Override
			public Screen getDestination() {
				return b;
			}
			
			@Override
			public boolean isComplete() {
				return a.isComplete();
			}
		};
	}

	// TODO find a way to easily queue some transitions in order to build
	// complex sequences. maybe with a builder pattern...
	// public static TransitionDesc sequence(TransitionDesc ... transitions) 

}
