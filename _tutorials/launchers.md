---
title: "Get started with KIT"
category: ""
key: "000000"
---

This is the first tutorial. Our goal is to launch a basic desktop Kit application.

Here is the boiler plate. No real changes from LibGDX setup boiler plate.

First we have our main method in our desktop launcher.


```java

LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
new LwjglApplication(new TutorialGame(), config);

```


Our main (cross platform) entry point is our TutorialGame.
Instead of extending LibGDX Game class, we will extends the KitGame class.

On creation, we just set the screen : an empty brown screen.


```java

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

```


It's time to run our DesktopLauncher.


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

