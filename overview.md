---
layout: page
title: "Overview"
---

<style>
pre{
    font-size: 16px;
    font-family: "Courier";
    color: #77FF77;
}
</style>

<div class="row">
    <div class="col-md-3">
        <div class="alert alter-success">
            <h3>Plug in your existing code without any effort</h3>
            out of the box ApplicationListener, Game, Screen, Stage wrappers
        </div>
    </div>
    <div class="col-md-3">
        <div class="alert alter-success">
            <h3>Lightweight dependencies </h3>
            Don't have to be included in your game :
            Separated module per gdx dependencies / extension,
            Lightweight module for definitions (annotations)
        </div>
    </div>
     <div class="col-md-3">
        <div class="alert alter-success">
            <h3>Easy to configure</h3>
            @Editable makes your field alive, persistable.
        </div>
    </div>
    <div class="col-md-3">
        <div class="alert alter-success">
            <h3>Micro code generation</h3>
            generate code for java (object instance) or JSON
        </div>
    </div>
    <div class="col-md-3">
        <div class="alert alter-success">
            <h3>Example</h3>
            here is commits to enable kit on an existing project (game made during a game jam : TODO choose a game)
        </div>
    </div>
    <div class="col-md-4">
        <div class="alert alter-success">
            <h3>Example</h3>
            in desktop test folder
            <pre>
class MyKitLauncher {

}
            </pre>
        </div>
    </div>
    <div class="col-md-5">
        <div class="alert alter-success">
             <h3>Example</h3>
            in desktop test folder
            <pre>
class MyGame {
    @Editable
    private float speed = 1;
}
            </pre>
        </div>
    </div>
   <div class="col-md-12">
        <div class="alert alter-success">
             <h3>Example</h3>
            in desktop test folder
            <pre>
class MyGameDesktopLauncher {
    main{
        new LWJGLApplicaion(config, Kit.forGame(new MyGame()));
    }
}
            </pre>
        </div>
    </div>
    <div class="col-md-12">
        <div class="alert alter-success">
             <h3>Easy to extends</h3>
            your own Kit module. Here, replace default Vector2 editor
            <pre>
class MyKitModule extends KitModule {
    @Override
    public void initialize(Kit kit){
        kit.register(Vecor2.class, MyVector2Editor.class);
    }
}

class MyVector2Editor extends KitEditor {
    @Override
    public Actor create(final Kit kit, final Accessor accessor){
        Editable config = accessor.get(Editable.class);
        Slider slider = new Slider(kit.skin, accessor.get(float.class), 0, 100, false);
        slider.addListener(
            ... accessor.set(slider.getValue());
        );
        return slider;
    }
}
            </pre>
        </div>
    </div>
</div>

|Modules           | DESCRIPTION |  DEPENDENCIES||
| kit-api|       all annotations and static definitions (lightweight)| none |
| - package kit.annotations |   @Editable, @storage, ...etc |
| kit-gdx       |base implementation for and with libGDX | libgdx (core) ||
| - <span style="color: red;">package kit.core</span>    |core framework class and java specific support |ok||
| - package kit.gdx     |all basic libgdx support and core ui implementation||
| - package kit.ui     | all ui specific (base widtgets + graph + tree + file choose, etc) |||
| kit-box2d |||
| kit-ashley| all ECS stuff (more like KIT v0.1) | ashley 1.7.3+ |

# Concepts

Among other, following concepts are part of KIT :

* ECS (entity component system) based.
* IoC (inversion of control) : lot of editor feature and some runtime features are automatically injected based on introspection and annotations.
* DRY (don't repeat yourself) : KIT tries to reduce game code as much as possible.
* convention over configuration : Convention as been made for plugin structure to avoid extra configuration.
* Reduced learning curve : KIT tries to fit libGDX design and avoid to wrap things, libGDX and Ashley users won't be lost.

# Features

## Core

Core features :
* Ashley (tooling, injection)
* Assets (tooling, injection, loading)
* Graphics (cameras, viewports)
* Storage (entities/components, system settings, editor configurations)
* Screens (workflow, transitions)

## Plugins

Built-in core plugins
* Graphics 2D
* Particle 2D
* Graphics 3D
* Particle 3D
* Splines
* Tilemaps
* Scene2D

Built-in optional plugins (for LibGDX extensions)
* Box2D (gdx-box2d)
* Bullet (gdx-bullet)
* Behavior Tree (gdx-ai)
* Puredata (gdx-pd)

## Cross platform support

At this time, only desktop and android are officially supported. Since KIT is pure java, it should work with any platform targetted by LibGDX but there is few chances it works with GWT deployment.

# Design

## Dependencies

Dependency organization for core, plugins, editor and editor plugins was not obvious, some likes to have individual repo or jars. The choice made was to keep all together for many reasons : 
* By experience, tools logic is often used in the final game. Game could be seen as a "player friendly" editor in some way.
* With a clean structure, unnecessary plugins / packages / classes can be easily ripped off releases.
* ...

Like LibGDX or any extension, KIT provides a core and specific jars for specific platforms.

Note that all plugins for LibGDX extension are optional dependencies that is your game projects doen't depends on them except if you explicitely add them.


