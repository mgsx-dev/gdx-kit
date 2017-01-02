package net.mgsx.game.plugins.parallax.tools;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.ComponentTool;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.parallax.components.ParallaxModel;

public class ParallaxTool extends ComponentTool {
	public ParallaxTool(EditorScreen editor) {
		super("Parallax", editor, Transform2DComponent.class);
	}

	@Override
	protected Component createComponent(Entity entity) {
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		ParallaxModel model = new ParallaxModel();
		model.cameraOrigin.set(editor.getGameCamera().position);
		model.objectOrigin.set(transform.position.x, transform.position.y, 0);
		return model;
	}
}