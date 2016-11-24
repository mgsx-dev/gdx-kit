package net.mgsx.game.plugins.fsm.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.plugins.fsm.StateMachinePlugin;

@Storable("fsm")
@EditableComponent
public class StateMachineComponent implements Component, Duplicable, Serializable, Poolable
{
	public static ComponentMapper<StateMachineComponent> components = ComponentMapper.getFor(StateMachineComponent.class);
	
	public EntityStateMachine fsm;
	public EntityState initialState;
	public EntityState globalState;
	
	@Override
	public void write(Json json) 
	{
		// TODO only if storable : need to force tagname for it
		if(initialState != null) json.writeValue("initial", initialState.getClass().getName());
	}

	@Override
	public void read(Json json, JsonValue jsonData) 
	{
		// TODO check tagname
		String typename = json.readValue("initial", String.class, jsonData);
		if(typename != null) initialState = StateMachinePlugin.state(ReflectionHelper.forName(typename));
	}

	@Override
	public Component duplicate(Engine engine) {
		StateMachineComponent clone = engine.createComponent(StateMachineComponent.class);
		clone.initialState = initialState;
		clone.globalState = globalState;
		return clone;
	}

	@Override
	public void reset() {
		if(fsm != null){
			StateMachinePlugin.fsmPool.free(fsm);
			fsm = null;
		}
		initialState = null;
		globalState = null;
	}
	
	
}
