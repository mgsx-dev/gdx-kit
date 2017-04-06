package net.mgsx.game.core.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;

import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.screen.Transitions.ColorScreen;

public class ScreenTransitionTest {
	
	private static class ScreenTest extends ColorScreen implements ScreenTransitionListener, ScreenClip
	{
		private boolean active, complete;
		private String label;
		
		public ScreenTest(Color color, String label) {
			super(color);
			this.label = label;
		}
		
		private void log(String msg){
			Gdx.app.log(label, msg);
		}

		@Override
		public void preHide() {
			log("preHide");
			active = false;
		}

		@Override
		public void postShow() {
			log("postShow");
			active = true;
		}
		
		@Override
		public void show() {
			log("show");
			super.show();
		}
		
		@Override
		public void hide() {
			log("hide");
			super.hide();
		}
		
		@Override
		public void render(float delta) {
			super.render(delta);
			if(active && Gdx.input.isTouched()){
				log("complete");
				complete = true;
			}
		}
		
		@Override
		public boolean isComplete() {
			return complete;
		}
		
	}
	
	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		GameApplication game = new GameApplication() {
			@Override
			public void create() {
				super.create();
				
				// create some screens
				Screen screen1 = new ScreenTest(Color.RED, "red");
				Screen screen2 = new ScreenTest(Color.GREEN, "green");
				Screen screen3 = new ScreenTest(Color.BLUE, "blue");
				
				// create some transitions
				setScreen(screen1);
				addTransition(Transitions.fade(screen2, 2));
				addTransition(Transitions.fade(screen3, 2));
			}
		};
		
		new LwjglApplication(game, config);
	}
}
