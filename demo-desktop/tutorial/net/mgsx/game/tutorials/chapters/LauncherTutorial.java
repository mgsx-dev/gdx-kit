package net.mgsx.game.tutorials.chapters;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;

import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.screen.Transitions;
import net.mgsx.game.tutorials.Tutorial;
import net.mgsx.kit.files.DesktopNativeInterface;

/**@md

This is the first tutorial. Our goal is to launch a basic desktop Kit application.

Here is the boiler plate. No real changes from LibGDX setup boiler plate.

First we have our main method in our desktop launcher.

@md*/

@Tutorial(id="launchers", title="Get started with KIT")

public class LauncherTutorial 
{
	public static void main (final String[] args) 
	{
		new LauncherTutorial();
	}
	
	public LauncherTutorial() {
		//@code
		NativeService.instance = new DesktopNativeInterface(); // TODO simplify this
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		new LwjglApplication(new TutorialGame(), config);
		//@code
	}
	
	/**@md

	Our main (cross platform) entry point is our TutorialGame.
	Instead of extending LibGDX Game class, we will extends the KitGame class.

	On creation, we just set the screen : an empty brown screen.

	@md*/
	//@code
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
	//@code

	/**@md

	It's time to run our DesktopLauncher.

	@md*/
}


/**@md

For other targeted platforms, boiler plate is almost the same since there is no platform specific
code to add in order to use Kit at game runtime.

For instance, Android launcher will be :

```java
public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		ClassRegistry.instance = new StaticClassRegistry(KitClass.class); // TODO explain this
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		
		initialize(new TutorialGame() , config);
	}
}

```

It sould be the same for any other launchers (TODO be more specific).

@md*/