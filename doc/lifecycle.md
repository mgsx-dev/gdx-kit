
# Global Lifecycle

Main lifecycle for KitGame is :
* Game creation
* Cache screens creation
* Asset loading
* Screen stack configuration

Main lifecycle for a KitScreen is :
* 1 - configuration phase : plugins are created, systems are created, ...
* 2 - collect phase : objects are scanned to retrieve assets, ...
* 3 - loading phase : assets are loaded
* 4 - injection phase : assets and systems are injected in systems
* 5 - initialization phase : systems are added to engine, ...
* 6 - runtime phase : engine is updated

## Screen transition workflow 

* TODOC

## Pause/Resume workflow

Game mignification (from visible state) :
* TODOC

Game restoration (from hidden state) :
* TODOC

