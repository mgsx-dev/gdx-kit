---
title: "Using KIT Screen API"
category: ""
key: "000000"
---
This tutorial desmonstrate use of screens in KIT.
KIT screen API is based on LibGDX screen API so if your familiar with [Game](http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/Game.html) and [Screen](http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/Screen.html) you
won't be lost in here.
KIT screens API provides usefull tools to easily build screen workflow including transitions.
All built-in transitions can be accessed throw [Transitions](http://kit.mgsx.net/docs/api/net/mgsx/game/core/screen/Transitions.html) like you do with [Actions](http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/scenes/scene2d/actions/Actions.html) in scene 2D.
In order to use KIT screen API, we need to extends [GameApplication](http://kit.mgsx.net/docs/api/net/mgsx/game/core/GameApplication.html) which is no more than a [Game](http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/Game.html) with
KIT screens features, it is a screen management implementation.

**Note about screens workflow** : Screens have 3 nested states : living, displayed, paused.
[ApplicationListener](http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/ApplicationListener.html) describes events occuring when transiting.

First living state start when screen is created (new Screen()) and end when it is disposed ([Screen.dispose](http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/Screen.html#dispose--).
As [Game](http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/Game.html), [GameApplication](http://kit.mgsx.net/docs/api/net/mgsx/game/core/GameApplication.html) won't automatically dispose your screens because sometime you want to reuse
some screens (loaders, menus...) and some you don't (in game level screen). This is the responsability of your
[GameApplication](http://kit.mgsx.net/docs/api/net/mgsx/game/core/GameApplication.html) sub class. Usually you create some long living screens at creation time on ({@link #create()}
and create others when needed. You may also want to lazy create screen but cache them for futur uses. Since strategy is
game specific, you'll have to decide yourself.

Then display state is trigger by [Screen.show](http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/Screen.html#show--) method. For long living
screen, some resources may be released when hidden and recreated when shown. This is the case with [StageScreen](http://kit.mgsx.net/docs/api/net/mgsx/game/core/screen/StageScreen.html) which
create scene2D object when shown and clear state when hidden. You can override this behavior if for instance your GUI take
long time to build.

With transition from screen A to screen B, display states are handled as follow : 
* initially A is shown and B is hidden.
* when transition starts, B is shown ([Screen.show](http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/Screen.html#show--) is called)
* during transitions, both A and B screens are rendered
* when transitions ends, A is hidden ([Screen.hide](http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/Screen.html#hide--) is called)
* after transition, only B screen is rendered.

Finally paused state occurs when application is minimized (on Android when activity leave foreground). In this case
current screen is paused and resume when going back in foreground.

During transitions all displayed screens goes to background and [Screen.pause](http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/Screen.html#pause--) is called for all of them
(including [TransitionScreen](http://kit.mgsx.net/docs/api/net/mgsx/game/core/screen/TransitionScreen.html) itself). When application goes foreground, [Screen.resume](http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/Screen.html#resume--) is called for
all screens (including [TransitionScreen](http://kit.mgsx.net/docs/api/net/mgsx/game/core/screen/TransitionScreen.html) itself) and all can continue their animations.

We first create a screen base that we will use later as base class to illustrate various
screens use case in this tutorial. It's just a white screen with KIT logo and some text.
This screen has no workflow and can be used as is and illustrate an endless game screen.

```java

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
		
	public TutorialScreen(String text, Viewport vp) {
		super(null);
		this.text = text;
		stage.setViewport(vp);
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

```

This one is a [ScreenClip](http://kit.mgsx.net/docs/api/net/mgsx/game/core/screen/ScreenClip.html), a screen clip has a complete status, usually a
screen with some animations or logic used to transit from/to interactive screens.

```java

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

```

Another [ScreenClip](http://kit.mgsx.net/docs/api/net/mgsx/game/core/screen/ScreenClip.html) which end when user touch screen, this is a basic
interactive screen.

```java

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

```

Sometimes you need a screen that just display some info and get back to previous screen
when user press back button.

```java

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

```

This one is a more complex example where we want to transit to other screens depending
on some user choices, typically a game menu. When previous screen participate passively in workflow
by just saying screen sequence is complete or saying nothing, this one will directly spawn some
new screen transitions.

```java

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
			
		addTransitButton("Fade transition", Transitions.fade(Transitions.timeout(new TutorialScreen("fade"), 4), 2));
			
		addTransitButton("Fade transition (custom interpolation)", 
				Transitions.fade(Transitions.timeout(new TutorialScreen("fade"), 4), 2, Interpolation.pow5In, null));
			
		addTransitButton("Swap transition", 
				Transitions.swap(Transitions.timeout(new TutorialScreen("swap"), 4)));
			
		addTransitButton("Swap transition (custom viewport)", 
				Transitions.fade(Transitions.timeout(new TutorialScreen("test", new FitViewport(480, 640)), 4), 2));
			
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

```

Now we have lot of screen examples, it's time to wire up all in order to play the demo.
This is done in the {@link #create()} method of our [GameApplication](http://kit.mgsx.net/docs/api/net/mgsx/game/core/GameApplication.html).
When our game is first created, we usually configure some screen transitions : splash screen,
default loading screen, application loading and so on. For simplicity, we will just start with a
black screen and fade to a demo screen.

```java

@Override
public void create() {
	super.create();
	setScreen(Transitions.loader(assets, Transitions.empty(Color.BLACK)));
	addTransition(Transitions.fade(Transitions.timeout(new TutorialScreen("KIT screens tutorial"), 2), .5f));
	addTransition(Transitions.fade(new MenuScreen("A simple menu screen waiting for user interaction"), 2.3f));
}

```

