package net.mgsx.game.plugins.parallax;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.ui.EntityEditor;

public class ParallaxEditor implements EntityEditorPlugin {

	@Override
	public Actor createEditor(Entity entity, Skin skin) {
		
		
		Table table = new Table(skin);
		
		ParallaxModel model = entity.getComponent(ParallaxModel.class);
		
		table.add("Parallax X");
		EntityEditor.createSlider(table, model, ReflectionHelper.field(model, "rateX"));
		table.add("Parallax Y");
		EntityEditor.createSlider(table, model, ReflectionHelper.field(model, "rateY"));
//		table.add(String.valueOf(model.rateY));
		
		return table;
	}

}
