package net.mgsx.game.plugins.parallax;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.ui.EntityEditor;

public class ParallaxEditor implements EntityEditorPlugin {

	@Override
	public Actor createEditor(Entity entity, Skin skin) {
		
		ParallaxModel model = entity.getComponent(ParallaxModel.class);
		
		return new EntityEditor(model, skin);
	}

}
