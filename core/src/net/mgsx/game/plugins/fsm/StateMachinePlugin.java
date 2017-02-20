package net.mgsx.game.plugins.fsm;

import com.badlogic.gdx.utils.Pool;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.helpers.TypeMap;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.fsm.components.EntityState;
import net.mgsx.game.plugins.fsm.components.EntityStateMachine;
import net.mgsx.game.plugins.fsm.components.StateMachineComponent;
import net.mgsx.game.plugins.fsm.systems.StateMachineSystem;

@PluginDef(category="state machine", components={StateMachineComponent.class})
public class StateMachinePlugin implements Plugin
{
	final public static TypeMap<EntityState> states = new TypeMap<EntityState>();
	
	public static <T extends EntityState> T state(Class<T> type){
		T state = (T)states.get(type);
		if(state == null){
			state = ReflectionHelper.newInstance(type);
		}
		return state;
	}
	
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
