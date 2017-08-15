---
title: "Create your game HUD"
category: ""
key: "000000"
---

HUD (Head-up display) is a very common feature in games, it refer to the UI displayed
in general sticky to the screen.

This tutorial show how to implements a custom HUD with Kit. In this example, the HUD is
used in the "game start" screen and contains 3 buttons.


	 
First we create a plugin for our game. This plugin just add our custom HUD system.
	
	
Then we create our custom HUD. We mostly use libgdx scene2d API, nothing fency here.
	

```java

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

```

