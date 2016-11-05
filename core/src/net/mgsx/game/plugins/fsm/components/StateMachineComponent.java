package net.mgsx.game.plugins.fsm.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.components.Duplicable;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.plugins.fsm.StateMachinePlugin;

public class StateMachineComponent implements Component, Duplicable, Serializable, Poolable
{
	public static ComponentMapper<StateMachineComponent> components = ComponentMapper.getFor(StateMachineComponent.class);
	
	public EntityStateMachine fsm;
	public EntityState initialState;
	public EntityState globalState;
	
	@Override
	public void write(Json json) 
	{
		json.writeValue("initial", initialState.getClass().getName());
	}

	@Override
	public void read(Json json, JsonValue jsonData) 
	{
		String typename = json.readValue("initial", String.class, jsonData);
		initialState = ReflectionHelper.newInstance(typename, EntityState.class);
	}

	@Override
	public Component duplicate() {
		StateMachineComponent clone = new StateMachineComponent();
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
