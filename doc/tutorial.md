DRAFT KIT spec V0.1.x

# Your first KIT game

First you need to generate LibGDX projects as usual using LibGDX setup application. 
You only need to check KIT extension and other extension you plan to use (box2D, bullet, ...)

Once generated, you'll have default LibGDX template : an application adapter drawing a sprite and launchers
for your selected targets (android, desktop, ...).
To use KIT, you'll only need to change core application implementation as follow :

```java

public MyApplication extends KitGame
{
	...
}

```

At this point, you could implement your game as usual but you can use KIT screens facility.
KIT screen is based on LibGDX screen API and provides managed screen workflow including transitions.
See detailed documentation in screens.md

# Your first KIT plugin

All modules in KIT are called plugin. A plugin just aggregate entity systems, loaders, serializers and other plugins.
Usually all your screens won't use same plugins : in game screen may use lot of custom plugin, in another hand a loading
screen may use only a subset of these.

Lets create our first plugin :

```java

public MyPlugin implements KitPlugin
{
	public void create(KitConfiguration config)
	{
		config.addSystem(new MySystem1());
		config.addSystem(new MySystem2());
		config.addSystem(new MySystem3());
	}
}

```

At this point, you can code all your game with standard ECS modeling. But you may want to use out of the box plugins.
There are several way to do it : with annotations on your plugin :

```java

@PluginDef(plugins={Plugin1.class, Plugin2.class}
public MyPlugin implements KitPlugin
{
}

```

or extends (implements) any other aggregator :

```java

public MyPlugin implements KitPlugin, DefaultPlugins
{
}

```

## Your first editor

Your game is ready but all power of KIT is in editor facilities. In order to enable edition of your game, you just need
to add the editor plugin.

```java

public MyPlugin implements KitPlugin, DefaultPlugins, EditorPlugin
{
}

```

Usually you use a special launcher in desktop project to run your game with editor but you can ship editor in
your android application (usefull for debugging) or you can even ship editor in your final application.

At this point you have all core tools available but you may want some special tools for your game specifics.

## Your first tool

A tool is an EntitySystem : it is updated at each engine update and can render stuff like bounding box or anything
needed to be displayed to user during use of this tool.
Only difference with other systems, tools are usually disabled, the editor system is responsible for enable or disable
tools when user change current tool.
Tool receives input events (touch, keys, ...) and can implement any of InputListener methods.

## Persist things

Obvioulsy KIT comes with serialization facilities in order to load/save entities and systems both at design time and
at runtime. By default nothing is persisted, you'll need extra configuration (mainly with annotations to do so) :

```java

@Storable("mygame.mycomponent")
public MyComponent implements Component
{
}

```

It works on components and systems. Storable annotation force you to set a type name. The reason is to prevent
refactoring (renaming/moving) impacts on your data. You can then rename your components/systems without needs to
migrate your data.

### Data filtering

Sometimes you don't want some fields to be serialized. Transient java keyword is used for this need : 

```java

@Storable("mygame.mycomponent")
public MyComponent implements Component
{
	public float duration;
	public transient float time;
}

```

Here, duration will be saved but not time, because time variable will be handled at runtime.

### Entities filtering

Sometimes you don't want to save all entities : particle alike entities usually don't need to be saved.
KIT will only save entities containing a special component "Repository". All entities without this component
won't be saved even if it contains storable components.
Usually all tools automatically add this component to created/imported entities.


# Advanced topics

## Make things editable

By default nothing can be edited by designer. To do so you just have to mark fields or methods editable :

```java

public MyComponent implements Component
{
	@Editable
	public float size;
	
	public float strength;
}

```

In this example, size field will be displayed in editor and is editable by user. The other field strength won't.
This works with systems (including tools) and components.

Note that getter/setter pattern is also supported : 

```java

public MyComponent implements Component
{
	private float size;
	
	@Editable
	public float getSize(){
		return size;
	}
	@Editable
	public void setSize(float value){
		size = value;
	}

}

```

It comply with java beans conventions : getXXX, setXXX and isXXX for boolean.

You can annotate some procedure as well (void method) : 

```java

public MyComponent implements Component
{
	@Editable
	public void refresh(){
		...
	}
}

```

User will be able to call this code by just clicking a button.


## Dependency injection

Often you need to access other system from your systems. It could be done by retreiving it from engine when added. 
Problem is system may not have been added yet in engine and can't be retrieve at adding time and you have to copy/paste
lot of boiler plate code (usually same code) in your system. More, system access is O(Log(n)) and could be heavy to get it
during update or worse during entities iterations.

To overcome these issues, KIT provide a dependency injection mecanism. You can now declare it like this :

```java

public MySystem1 extends EntitySystem
{
	@Inject public MySystem2 mySystem2;
}

```

With @Inject annotation, mySystem2 variable will be initialized automatically with related system and available after
instanciation : you can use it when added to engine and during updates.

## Assets injection

Assets have a special lifecycle (loading, restoring, ...). So an asset can't be usable before loading is finished but
you need to load it in some way.

To overcome this complexity, KIT provide Asset injection mecanism like so :

```java

public MySystem1 extends EntitySystem
{
	@Asset(fileName="textures/background.png")
	public Texture backgroundTexture;
}

```

With asset annotation, backgroundTexture variable will be automatically initialized with loaded assets. Filename is used
to pre load asset before system become active. No need to unload this asset, KIT do it for you when system is disposed.

More, this annotation allow designer to change asset at runtime and save configuration. Filename provides in annotation is
the default fielname for the asset.


## Loader and serializers

TODOC

## Proxies

TODOC

## Reload some assets during edition

TODOC

## Selectors

TODOC
