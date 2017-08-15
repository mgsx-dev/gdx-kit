---
title: "ECS Basics"
category: "ecs"
key: "000001"
---

In this tutorial we will learn how systems works in ECS.

This little example is a very basic game out of interest. But you'll learn
how ordering systems with [GamePipeline](http://kit.mgsx.net/docs/api/net/mgsx/game/core/GamePipeline.html) and setup inter system communication
with [Inject](http://kit.mgsx.net/docs/api/net/mgsx/game/core/annotations/Inject.html) annotation.

**Design consideration** :

* following design is for example purpose, you typically use entities and
components to store the hero state so this example is a perfect anti-pattern.
* system inter dependency should be avoided but could be required in some case. This
can be done by externalize shared systems data in a dedicated class.

	
Our first system is our game logic : It own the hero position and
a hero status. Logic is very simple here : hero is falling down continuously
and dying when exiting the screen.
	

```java

public class MyLogicSystem extends EntitySystem {
		
	public float position;
	boolean playerAlive = true;
		
	public MyLogicSystem() {
		super(GamePipeline.LOGIC);
	}
		
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		position = Gdx.graphics.getHeight()/2;
	}
		
	@Override
	public void update(float deltaTime) {
		if(position < 0 || position > Gdx.graphics.getHeight()){
			playerAlive = false;
		}else{
			position -= deltaTime * 10;
		}
	}
}

```

	
Now we need to render our hero on the screen to see what's happen. Instead of drawing in the
logic system, we will create another system for this purpose. Our render system will be responsible of
just drawing hero at its position and with a color related to his status.
	
Since position and status are owned by logic system, we will inject our logic system in order to read
hero state. Thanks to [Inject](http://kit.mgsx.net/docs/api/net/mgsx/game/core/annotations/Inject.html) annotation, logic system will be automatically set by kit for you.
	
Rendering is very simple here, we're using the LibGDX [ShapeRenderer](http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/graphics/glutils/ShapeRenderer.html) to draw a gree or red X
depending on the hero status.
	

```java

public class MyRenderSystem extends EntitySystem {
		
	@Inject MyLogicSystem logic;
		
	private ShapeRenderer renderer;
		
	public MyRenderSystem() {
		super(GamePipeline.RENDER);
	}
		
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		renderer = new ShapeRenderer(4);
	}
		
	@Override
	public void update(float deltaTime) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(logic.playerAlive){
			renderer.setColor(Color.GREEN);
		}else{
			renderer.setColor(Color.RED);
		}
		renderer.begin(ShapeType.Line);
		renderer.x(Gdx.graphics.getWidth()/2, logic.position, 10);
		renderer.end();
	}
}

```

	
Now we need to control the player. Again instead of doing this in our logic
system, we will create a input system for this purpose. Its responsability is
to move hero position upward when player press up key.
	
Again our logic system is injected in order to update hero position value.
	

```java

public class MyInputSystem extends EntitySystem {
		
	@Inject MyLogicSystem logic;
		
	private float moveSpeed;
		
	public MyInputSystem() {
		super(GamePipeline.INPUT);
	}
		
	@Override
	public void update(float deltaTime) {
		if(Gdx.input.isKeyPressed(Input.Keys.UP)){
			moveSpeed = 50;
		}else{
			moveSpeed = 0;
		}
		logic.position += moveSpeed * deltaTime;
	}
}

```

	
Now it's time to put all systems together. As seen before, systems are
created and added to the engine through Kit plugins. So let create a plugin for
our awesome game and add our systems.
	

```java

class MyPlugin implements Plugin
{
	@Override
	public void initialize(GameScreen engine) {
		engine.entityEngine.addSystem(new MyLogicSystem());
		engine.entityEngine.addSystem(new MyRenderSystem());
		engine.entityEngine.addSystem(new MyInputSystem());
	}
}

```

	
Note that since we're using [GamePipeline](http://kit.mgsx.net/docs/api/net/mgsx/game/core/GamePipeline.html) to define system ordering, there is no matter
in which order we add our systems to the engine.
Systems will be executed in the order difined by the pipeline (except when systems have same pipeline stage).
	
Ordering is predefined as follow : 
* [GamePipeline.INPUT](http://kit.mgsx.net/docs/api/net/mgsx/game/core/GamePipeline.html#INPUT--)
* [GamePipeline.PHYSICS](http://kit.mgsx.net/docs/api/net/mgsx/game/core/GamePipeline.html#PHYSICS--)
* [GamePipeline.LOGIC](http://kit.mgsx.net/docs/api/net/mgsx/game/core/GamePipeline.html#LOGIC--)
* [GamePipeline.RENDER](http://kit.mgsx.net/docs/api/net/mgsx/game/core/GamePipeline.html#RENDER--)
	
There is some intermediate stages, for instance : 
* {@link GamePipeline#BEFORE_LOGIC}
* {@link GamePipeline#AFTER_LOGIC}
	
It is not mandatory to use [GamePipeline](http://kit.mgsx.net/docs/api/net/mgsx/game/core/GamePipeline.html). You could extend it to add your own stages or
write your own game pipeline, these constants are just integers.
	
	
It just missing our Game and the game screen. Let's create it.
	
All we have to do is to register our plugin.
	

```java

public class MyGame extends GameApplication {
	@Override
	public void create() {
		super.create();
		GameRegistry registry = new GameRegistry();
		registry.registerPlugin(new MyPlugin());
		setScreen(new GameScreen(this, assets, registry ));
	}
}

```

	
Finally we can test our game by creating a desktop launcher
	

```java

public class MyGameDesktopLauncher
{
	public static void main(String[] args) {
		new LwjglApplication(new MyGame(), new LwjglApplicationConfiguration());
	}
}

```

