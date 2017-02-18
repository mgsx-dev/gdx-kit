
Migration notes from current KIT 0.0.X to KIT 0.1.X


# Global views => Custom System Editor

Box2D and G3D have special GUI tabs for tools. This may be replaced by system editors ?

# Component Editors

Some components have special editor (Box2D, ...) how to register it ? 
* throw annotations is maybe to static and add a dependency to edit codes ...
* special registry (complexify API ...)

# Tools => System

* Is editor wrap tools or tols are all added to engine ?
* Tool render/renderDebug replaced by update method with access to debugger system (shape renderer / sprite batch)

# Loaders

loaders may need context to operate (plugin registry for instance)

# Selectors

how to ...

# Behavior Tree Tasks

how to inject ...

# Camera

* lot of system require camera. Camera can be owned by a simple camera system with no more than a camera.
* other systems get injected system to work with.
* other may change camera state (editor, animations, ...etc)

# Asset manager

* who owned asset manager ? it's on application so transverse to all screens ... so loaders may be registered statically ?
* is asset manager needed if @Asset is used every where ?

# Plugins

* editor plugin and game plugin are replaced by plugin
* create method take a KitConfiguration where can add systems, loaders, selectors, ...etc
* plugins can be replaced : config.replace(G3Drender.class, new MyG3DRender()), MyG3DRender extedns G3Drender ?
  may not work if other get system by its type !
* KitConfiguration will add system to engine after configuration phase.

* Plugins review :
  * box2d : context with world should be placed in Box2Dphysic system ...
  * add serializers
  * add entities listeners
  * add asset loaders
  * platformer plugin : lot of weired things : midi sequence, 
  * state machine with a static pool
  * ahsley : only global editor
  * asset plugin : global editors
  * add tools
  * box2d some component editors
  * selectors
  * btree : reload listeners, library static, default type editors ... lot of stuff
  * core editor plugin : loat of stuff as well : global tool, super tool, editor with load configuration....
  * profiler : static shared model ...
  * FSM editor : static class registry ...*
  
# Editor

deps from tools to editor : 
* TODOE #



