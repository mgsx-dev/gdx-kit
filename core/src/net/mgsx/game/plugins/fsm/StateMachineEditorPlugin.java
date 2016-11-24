package net.mgsx.game.plugins.fsm;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.fsm.components.EntityState;
import net.mgsx.game.plugins.fsm.components.StateMachineComponent;
import net.mgsx.game.plugins.fsm.editors.StateMachineEditor;

@PluginDef(dependencies=StateMachinePlugin.class)
public class StateMachineEditorPlugin extends EditorPlugin
{

private static Array<EntityState> allStates;
	
	public static Array<EntityState> allStates(){
		if(allStates == null){
			
			// TODO do it once and keep result in static class
			Reflections reflections = new Reflections(
					new ConfigurationBuilder()
				     .setUrls(ClasspathHelper.getUrlsForPackagePrefix("net.mgsx.game"))
				     .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner()));
			
			allStates = new Array<EntityState>();
			for(Class<? extends EntityState> type : reflections.getSubTypesOf(EntityState.class)){
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
