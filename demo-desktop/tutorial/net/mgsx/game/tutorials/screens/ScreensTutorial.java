package net.mgsx.game.tutorials.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.Kit;
import net.mgsx.game.core.screen.ScreenClip;
import net.mgsx.game.core.screen.StageScreen;
import net.mgsx.game.core.screen.TransitionDesc;
import net.mgsx.game.core.screen.Transitions;

/**@md
This tutorial desmonstrate use of screens in KIT.
KIT screen API is based on LibGDX screen API so if your familiar with {@link Game} and {@link Screen} you
won't be lost in here.
KIT screens API provides usefull tools to easily build screen workflow including transitions.
All built-in transitions can be accessed throw {@link Transitions} like you do with {@link Actions} in scene 2D.
In order to use KIT screen API, we need to extends {@link GameApplication} which is no more than a {@link Game} with
KIT screens features, it is a screen management implementation.
@md*/
//@code
public class ScreensTutorial extends GameApplication
//@code
{
	public static void main (final String[] args) 
	{
		new LwjglApplication(new ScreensTutorial(), new LwjglApplicationConfiguration());
	}
	
	/**@md
	We first create a screen base that we will use later as base class to illustrate various
	screens use case in this tutorial. It's just a white screen with KIT logo and some text.
	This screen has no workflow and can be used as is and illustrate an endless game screen.
	@md*/
	//@code
	public class TutorialScreen extends StageScreen
	{
		private final String text;
		protected Image image;
		protected Table table;
		protected BitmapFont font;
		public TutorialScreen(String text) {
			super(null);
			this.text = text;
		}
		
		@Override
		public void show() {
			Gdx.app.log("SHOW screen", text);
			super.show();
			table = new Table();
			table.add(image = new Image(new Texture(Gdx.files.classpath("gdxkit_logo.png"))));
			LabelStyle style = new LabelStyle();
			style.font = font = new BitmapFont();
			style.fontColor = Color.LIGHT_GRAY;
			Label info = new Label(text, style);
			table.row();
			table.add(info).padTop(20).row();
			table.setFillParent(true);
			stage.addActor(table);
		}
		@Override
		public void render(float deltaTime) {
			Gdx.gl.glClearColor(1, 1, 1, 0);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			super.render(deltaTime);
		}
		@Override
		public void hide() {
			super.hide();
			Gdx.app.log("HIDE screen", text);
		}
	}
	//@code
	
	/**@md
	This one is a {@link ScreenClip}, a screen clip has a complete status, usually a
	screen with some animations or logic used to transit from/to interactive screens.
	@md*/
	//@code
	public class AnimatedScreen extends TutorialScreen implements ScreenClip
	{
		private boolean finished;
		public AnimatedScreen(String text) {
			super(text);
		}
		
		@Override
		public void show() {
			super.show();
			Runnable setOver = new Runnable() {
				@Override
				public void run() {
					finished = true;
				}
			};
			image.setOrigin(Align.center);
			image.addAction(Actions.sequence(Actions.rotateBy(360, 3), Actions.delay(1), Actions.run(setOver)));
		}

		@Override
		public boolean isComplete() {
			return finished;
		}
	}
	//@code
	
	/**@md
	Another {@link ScreenClip} which end when user touch screen, this is a basic
	interactive screen.
	@md*/
	//@code
	public class InteractiveScreen extends TutorialScreen implements ScreenClip
	{
		private boolean finished;
		private InputProcessor inputProcessor;
		public InteractiveScreen(String text) {
			super(text);
			inputProcessor = new InputAdapter(){
				@Override
				public boolean touchDown(int screenX, int screenY, int pointer, int button) {
					finished = true;
					return true;
				}
			};
		}
		
		@Override
		public void show() {
			super.show();
			Kit.inputs.addProcessor(inputProcessor);
		}
		
		@Override
		public void hide() {
			Kit.inputs.removeProcessor(inputProcessor);
			super.hide();
		}

		@Override
		public boolean isComplete() {
			return finished;
		}
	}
	//@code
	
	/**@md
	Sometimes you need a screen that just display some info and get back to previous screen
	when user press back button.
	@md*/
	//@code
	public class BackScreen extends TutorialScreen implements ScreenClip
	{
		private boolean finished;
		public BackScreen(String text) {
			super(text);
		}
		
		@Override
		public void render(float deltaTime) {
			if(Gdx.input.isKeyPressed(Input.Keys.BACK)) finished = true;
			super.render(deltaTime);
		}

		@Override
		public boolean isComplete() {
			return finished;
		}
	}
	//@code
	
	/**@md
	This one is a more complex example where we want to transit to other screens depending
	on some user choices, typically a game menu. When previous screen participate passively in workflow
	by just saying screen sequence is complete or saying nothing, this one will directly spawn some
	new screen transitions.
	@md*/
	//@code
	public class MenuScreen extends TutorialScreen
	{
		public MenuScreen(String text) {
			super(text);
		}
		
		@Override
		public void show() {
			super.show();
			
			// TODO transitions builder ??? :
			// builder.screen(a).timeout(4).fade(2).screen(b)
			// builder.fade(2).screen(black).timeout(4).fade(2).screen(b).loader(assets);
			
			addTransitButton("Back Screen", Transitions.fade(new BackScreen("press back (< key on desktop)"), 2));
			addTransitButton("Clip Screen", Transitions.fade(new AnimatedScreen("just wait animation to be completed"), 2));
			addTransitButton("Interactive Screen", Transitions.fade(new InteractiveScreen("touch screen to end this screen"), 2));
			addTransitButton("Endless Screen", Transitions.fade(Transitions.timeout(new TutorialScreen("just wait 4s timeout"), 4), 2));
			
			// TODO loader with endless screen
			// TODO loader with endless screen and minimum time
			// TODO custom loader (responding to asset manager progress)
			
			// TODO special transition effects : blur, strips, offset, ...etc
			// TODO custom pixel shader effect
			
			// TODO sequence builder ...
			TransitionDesc r = Transitions.fade(Transitions.timeout(Transitions.empty(Color.RED), 4), 2);
			TransitionDesc g = Transitions.fade(Transitions.timeout(Transitions.empty(Color.GREEN), 4), 2);
			TransitionDesc b = Transitions.fade(Transitions.timeout(Transitions.empty(Color.BLUE), 4), 2);
			// XXX addTransitButton("RGB Sequence", Transitions.sequence(r, g, b));
		}
		
		private void addTransitButton(String title, final TransitionDesc transition)
		{
			TextButtonStyle style = new TextButtonStyle();
			style.font = font;
			TextButton button = new TextButton("Touch to play " + title, style);
			button.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					setTransition(transition);
					addTransition(Transitions.fade(MenuScreen.this, 2));
				}
			});
			table.add(button).row();
		}

	}
	//@code
	
	// TODO loading test by mocking asset manager locking with sleep loaders.
	
	/**@md
	Now we have lot of screen examples, it's time to wire up all in order to play the demo.
	This is done in the {@link #create()} method of our {@link GameApplication}.
	When our game is first created, we usually configure some screen transitions : splash screen,
	default loading screen, application loading and so on. For simplicity, we will just start with a
	black screen and fade to a demo screen.
	@md*/
	//@code
	@Override
	public void create() {
		super.create();
		setScreen(Transitions.loader(assets, Transitions.empty(Color.BLACK)));
		addTransition(Transitions.fade(Transitions.timeout(new TutorialScreen("Classic cross fade"), 2), 2.3f));
		addTransition(Transitions.fade(new MenuScreen("A simple menu screen waiting for user interaction"), 2.3f));
	}
	//@code
	
}
