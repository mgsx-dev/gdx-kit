package net.mgsx.game.core;

import com.badlogic.ashley.core.EntitySystem;

/**
 * see {@link EntitySystem#priority}
 */
final public class GamePipeline 
{
	// never used this is the default value.
	public static final int FIRST = -1;
	
	public static final int DEFAULT = 0;
	
	
	// first analyse input and interact with game (physics/logic)
	public static final int INPUT = 1;
	
	// then update phyisics (check collision to get a status)
	public static final int BEFORE_PHYSICS = INPUT; // alias
	public static final int PHYSICS = BEFORE_PHYSICS + 1;
	
	// good place to update logic from physics (position, angle ...)
	public static final int AFTER_PHYSICS = PHYSICS + 1;
			
	
	// then logic (responding to physic and input analysis)
	public static final int BEFORE_LOGIC = AFTER_PHYSICS; // alias
	public static final int LOGIC = BEFORE_LOGIC + 1;
	
	// after logic could be matrix updates
	public static final int AFTER_LOGIC = LOGIC + 1;

	// then render (normal rendering and over rendering (debug, tools, ... always on top)
	public static final int BEFORE_RENDER = AFTER_LOGIC; // alias
	public static final int RENDER_OPAQUE = BEFORE_RENDER + 1;
	public static final int RENDER_TRANSPARENT = RENDER_OPAQUE + 1;
	public static final int RENDER_DEBUG = RENDER_TRANSPARENT + 1;
	public static final int AFTER_RENDER = RENDER_DEBUG + 1;
	
	
	public static final int RENDER = RENDER_OPAQUE; // alias
	public static final int RENDER_OVER = RENDER_DEBUG; // alias
	
	
	// 
	public static final int LAST = AFTER_RENDER + 1;
	
}
