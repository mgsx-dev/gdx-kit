package net.mgsx.game.plugins.fsm;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.fsm.components.EntityState;
import net.mgsx.game.plugins.fsm.components.StateMachineComponent;
import net.mgsx.game.plugins.fsm.editors.StateMachineEditor;

@PluginDef(dependencies=StateMachinePlugin.class, requires="com.badlogic.gdx.ai.GdxAI")
public class StateMachineEditorPlugin extends EditorPlugin
{

private static Array<EntityState> allStates;
	
	public static Array<EntityState> allStates(){
		if(allStates == null){
			allStates = new Array<EntityState>();
			for(Class<? extends EntityState> type : ClassRegistry.instance.getSubTypesOf(EntityState.class)){
				EntityState state = StateMachinePlugin.state(type);
				allStates.add(state);
			}
			
		}
		return allStates;
	}
	
	@Override
	public void initialize(EditorScreen editor) {
		editor.registry.registerPlugin(StateMachineComponent.class, new StateMachineEditor());
	}

}
