package net.mgsx.game.tutorials.tutorial1;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.kit.files.DesktopNativeInterface;

/*md

This is the first tutorial. Our goal is to launch a basic desktop Kit application.

Here is the boiler plate. No real changes from LibGDX setup boiler plate.

md*/

//code
public class DesktopLauncher {

	public static void main (final String[] args) 
	{
		NativeService.instance = new DesktopNativeInterface(); // TODO simplify this
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		new LwjglApplication(new TutorialGame(), config);
	}
}
//code

/*md

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

It sould be the same for any other launchers.

md*/