package net.mgsx.game.core.misc;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.GameRegistry;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.plugins.Plugin;

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
		if(args.length <= 0) throw new GdxRuntimeException("expected at least one plugin class name");
		GameRegistry registry = new GameRegistry();
		for(String arg : args){
			registry.registerPlugin(ReflectionHelper.newInstance(arg, Plugin.class));
		}
		generate(registry);
	}
}
