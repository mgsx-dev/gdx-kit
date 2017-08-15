---
layout: page
title: "Overview"
---

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


