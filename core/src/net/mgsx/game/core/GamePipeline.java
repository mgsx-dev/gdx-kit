package net.mgsx.game.core;

import com.badlogic.ashley.core.EntitySystem;

/**
 * see {@link EntitySystem#priority}
 */
final public class GamePipeline 
{
	// num slot (increment) between stages
	private static final int SLOTS = 100;
	
	// never used this is the default value.
	public static final int DEFAULT = 0;
	public static final int FIRST = DEFAULT + SLOTS;
	
	
	
	// first analyse input and interact with game (physics/logic)
	public static final int BEFORE_INPUT = FIRST + SLOTS;
	public static final int INPUT = BEFORE_INPUT + SLOTS;
	public static final int AFTER_INPUT = INPUT + SLOTS;
	
	// then update phyisics (check collision to get a status)
	public static final int BEFORE_PHYSICS = AFTER_INPUT + SLOTS;
	public static final int PHYSICS = BEFORE_PHYSICS + SLOTS;
	
	// good place to update logic from physics (position, angle ...)
	public static final int AFTER_PHYSICS = PHYSICS + SLOTS;
			
	
	// then logic (responding to physic and input analysis)
	public static final int BEFORE_LOGIC = AFTER_PHYSICS + SLOTS;
	public static final int LOGIC = BEFORE_LOGIC + SLOTS;;
	
	// after logic could be matrix updates
	public static final int AFTER_LOGIC = LOGIC + SLOTS;

	// culling act before logic and rendering and update some components
	// computing collision with frustum.
	public static final int BEFORE_CULLING = AFTER_LOGIC + SLOTS;
	public static final int CULLING = BEFORE_CULLING + SLOTS;
	public static final int AFTER_CULLING = CULLING + SLOTS;
	
	
	// then render (normal rendering and over rendering (debug, tools, ... always on top)
	public static final int BEFORE_RENDER = AFTER_CULLING + SLOTS; // alias
	
	
	public static final int BEFORE_RENDER_OPAQUE = BEFORE_RENDER + SLOTS;
	public static final int RENDER_OPAQUE = BEFORE_RENDER_OPAQUE + SLOTS;
	public static final int AFTER_RENDER_OPAQUE = RENDER_OPAQUE + SLOTS;
	
	
	public static final int RENDER_TRANSPARENT = AFTER_RENDER_OPAQUE + SLOTS;
	public static final int AFTER_RENDER = RENDER_TRANSPARENT + SLOTS;
	public static final int HUD = AFTER_RENDER + SLOTS;
	
	// special render phase for tools (last render after any FBO process)
	public static final int RENDER_DEBUG = HUD + SLOTS;
	
	public static final int RENDER_TOOLS = RENDER_DEBUG + SLOTS;
	
	// 
	public static final int LAST = RENDER_TOOLS + SLOTS;
	
	// some alias
	public static final int RENDER = RENDER_OPAQUE;
	public static final int RENDER_OVER = RENDER_DEBUG;
	
	
}
