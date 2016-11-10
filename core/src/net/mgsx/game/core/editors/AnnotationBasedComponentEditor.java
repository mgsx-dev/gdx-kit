package net.mgsx.game.core.editors;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.ui.EntityEditor;

public class AnnotationBasedComponentEditor implements EntityEditorPlugin {
	private final Class<? extends Component> type;

	public AnnotationBasedComponentEditor(Class<? extends Component> type) {
		this.type = type;
	}

	@Override
	public Actor createEditor(Entity entity, Skin skin) {
		return new EntityEditor(entity.getComponent(type), true, skin);
	}
}