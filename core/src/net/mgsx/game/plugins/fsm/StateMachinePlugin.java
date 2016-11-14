package net.mgsx.game.plugins.fsm;

import com.badlogic.gdx.utils.Pool;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.fsm.components.EntityStateMachine;
import net.mgsx.game.plugins.fsm.components.StateMachineComponent;
import net.mgsx.game.plugins.fsm.systems.StateMachineSystem;

@PluginDef(components={StateMachineComponent.class})
public class StateMachinePlugin implements Plugin
{
	final public static Pool<EntityStateMachine> fsmPool = new Pool<EntityStateMachine>(){
		@Override
		protected EntityStateMachine newObject() {
			return new EntityStateMachine();
		}
	};

	@Override
	public void initialize(GameScreen engine) 
	{
		engine.entityEngine.addSystem(new StateMachineSystem());
	}

}
