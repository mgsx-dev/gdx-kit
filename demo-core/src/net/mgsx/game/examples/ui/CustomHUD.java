package net.mgsx.game.examples.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.screen.Transitions;
import net.mgsx.game.plugins.core.systems.HUDSystem;
import net.mgsx.game.plugins.graphics.systems.ScreenSystem;

public class CustomHUD extends HUDSystem
{
	@Inject
	protected ScreenSystem screen;
	
	@Asset("uiskin.json")
	protected Skin skin;
	
	private Table main;
	
	@Override
	public void update(float deltaTime) 
	{
		if(main == null)
		{
			// we create the main menu
			
			main = new Table(skin);
			
			main.defaults().pad(30);
			
			main.add("Welcome in Game Land!").row();
			TextButton create = new TextButton("New Game", skin);
			main.add(create).row();
			
			TextButton resume = new TextButton("Resume", skin);
			main.add(resume).row();
			
			TextButton credits = new TextButton("Credits", skin);
			main.add(credits).row();
			
			create.addListener(screen.trigger(Transitions.fade(Transitions.empty(Color.BLACK), 2)));
			resume.addListener(screen.trigger(Transitions.fade(Transitions.empty(Color.BLACK), 2)));
			credits.addListener(screen.trigger(Transitions.fade(Transitions.empty(Color.BLACK), 2)));
			
			main.setFillParent(true);
			
			
			getStage().addActor(main);
		}
		super.update(deltaTime);
	}
	
}
