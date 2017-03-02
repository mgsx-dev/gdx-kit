package net.mgsx.game.tutorials.chapters;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.GameRegistry;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.screen.Transitions;
import net.mgsx.game.plugins.core.systems.HUDSystem;
import net.mgsx.game.plugins.graphics.GraphicsPlugin;
import net.mgsx.game.plugins.graphics.systems.ScreenSystem;
import net.mgsx.game.tutorials.Tutorial;

/**@md

HUD (Head-up display) is a very common feature in games, it refer to the UI displayed
in general sticky to the screen.

This tutorial show how to implements a custom HUD with Kit. In this example, the HUD is
used in the "game start" screen and contains 3 buttons.


@md*/

@Tutorial(id="hud", title="Create your game HUD")

public class HUDTutorial extends GameApplication
{
	public static void main (final String[] args) 
	{
		new LwjglApplication(new HUDTutorial(), new LwjglApplicationConfiguration());
	}
	
	@Override
	public void create() {
		super.create();
		GameRegistry registry = new GameRegistry();
		registry.registerPlugin(new HUDTutorialPlugin());
		setScreen(Transitions.loader(assets, Transitions.empty(Color.BLACK)));
		addScreen(new GameScreen(this, assets, registry));
	}
	
	/**@md
	 
	First we create a plugin for our game. This plugin just add our custom HUD system.
	
	@md*/
	@PluginDef(dependencies={GraphicsPlugin.class})
	public class HUDTutorialPlugin implements Plugin
	{
		@Override
		public void initialize(GameScreen engine) {
			engine.entityEngine.addSystem(new CustomHUD());
		}
	}
	
	/**@md
	
	Then we create our custom HUD. We mostly use libgdx scene2d API, nothing fency here.
	
	@md*/
	//@code
	public class CustomHUD extends HUDSystem
	{
		// we need the screen manager in order to spawn screen transitions.
		@Inject protected ScreenSystem screen;
		
		// we need a skin to build our UI
		@Asset("uiskin.json")
		protected Skin skin;
		
		// this will be our main UI component
		private Table main;
		
		@Override
		public void update(float deltaTime) 
		{
			if(main == null)
			{
				// we create the main menu
				
				main = new Table(skin);
				
				main.defaults().pad(30);
				
				TextButton create = new TextButton("New Game", skin);
				TextButton resume = new TextButton("Resume", skin);
				TextButton credits = new TextButton("Credits", skin);

				main.add("Welcome in Game Land!").row();
				main.add(create).row();
				main.add(resume).row();
				main.add(credits).row();
				
				main.setFillParent(true);
				
				getStage().addActor(main);

				// we attach listener to our buttons using screen system helper.
				// each button will spawn same sequence : fade out to a black screen.
				create.addListener(screen.trigger(Transitions.fade(Transitions.empty(Color.BLACK), 2)));
				resume.addListener(screen.trigger(Transitions.fade(Transitions.empty(Color.BLACK), 2)));
				credits.addListener(screen.trigger(Transitions.fade(Transitions.empty(Color.BLACK), 2)));
				
			}
			super.update(deltaTime);
		}
	}
	//@code
	
}


