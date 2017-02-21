package net.mgsx.game.tutorials.tutorial1;

import com.badlogic.gdx.graphics.Color;

import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.screen.Transitions;

/*md

Our main (cross platform) entry point is our TutorialGame.
Instead of extending LibGDX Game class, we will extends the KitGame class.

On creation, we just set the screen : an empty brown screen.

md*/
//code
public class TutorialGame extends GameApplication // TODO rename KitGame
{
	@Override
	public void create() 
	{
		super.create();
		
		// just create an empty brown screen for now.
		setScreen(Transitions.empty(Color.BROWN));
	}
}
//code

/*md

It's time to run our DesktopLauncher.

md*/