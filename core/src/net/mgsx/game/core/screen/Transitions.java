package net.mgsx.game.core.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
					Gdx.files.classpath("net/mgsx/game/core/screen/shaders/gdx-default.vert"),
					Gdx.files.classpath("net/mgsx/game/core/screen/shaders/transition-fade.frag")
					));
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
	
	public static TransitionDesc fade(Screen destination, float duration){
		return fade(destination, duration, null);
	} 
	public static TransitionDesc fade(Screen destination, float duration, TransitionListener listener) 
	{
		TransitionDesc desc = new TransitionDesc();
		desc.destination = destination;
		desc.duration = duration;
		desc.interpolation = Interpolation.linear;
		desc.transition = fadeTransition();
		desc.listener = listener;
		return desc;
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

}
