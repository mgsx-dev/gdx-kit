package net.mgsx.game.examples.breaker.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldSystem;

@EditableSystem
public class BreakerGameSystem extends EntitySystem
{
	@Inject Box2DWorldSystem world;
	
	public BreakerGameSystem() {
		super(GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		resetGame();
		
	}
	
	@Editable
	public void resetGame(){
		
		// TODO remove all objects
		
		// world.getWorldContext().
		
		
	}
	
}
