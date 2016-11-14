package net.mgsx.game.core.misc;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GameRegistry;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.plugins.DefaultEditorPlugin;

public class SorageModelExporter 
{
	private static void generate(GameRegistry registry){
		Array<String> tags = new Array<String>();
		for(Class<? extends Component> type : registry.components){
			Storable storable = type.getAnnotation(Storable.class);
			if(storable != null){
				String tag = storable.value();
				tags.add("[" + tag + "] : " + type.getName());
			}
		}
		tags.sort();
		System.out.println(tags.toString("\n"));
	}
	
	public static void main(String[] args) {
		GameRegistry registry = new GameRegistry();
		registry.registerPlugin(DefaultEditorPlugin.class);
		generate(registry);
	}
}
