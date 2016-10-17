package net.mgsx.core;

import com.badlogic.ashley.core.EntitySystem;

/**
 * see {@link EntitySystem#priority}
 */
final public class GamePipeline 
{
	// never used this is the default value.
	public static final int FIRST = 0;
	
	// first analyse input and interact with game (physics/logic)
	public static final int INPUT = 1;
	
	// then update phyisics (check collision to get a status)
	public static final int BEFORE_LOGIC = INPUT+1;
	public static final int PHYSICS = BEFORE_LOGIC; // alias
	
	// then logic (responding to physic and input analysis)
	public static final int LOGIC = BEFORE_LOGIC + 1;
	
	// after logic could be matrix updates
	public static final int AFTER_LOGIC = LOGIC + 1;

	// then render (normal rendering and over rendering (debug, tools, ... always on top)
	public static final int BEFORE_RENDER = AFTER_LOGIC; // alias
	public static final int RENDER = BEFORE_RENDER + 1;
	public static final int RENDER_OVER = RENDER + 1;
	public static final int AFTER_RENDER = RENDER_OVER + 1;
	
}
