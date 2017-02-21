package net.mgsx.game.plugins.parallax.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.parallax.components.ParallaxModel;

public class ParallaxTool extends Tool {
	public ParallaxTool(EditorScreen editor) {
		super("Parallax", editor);
	}
	
	@Override
	public boolean allowed(Array<Entity> selection) {
		return selection.size == 1 && Transform2DComponent.components.has(selection.first());
	}

	@Override
	protected void activate() {
		Entity entity = editor.currentEntity();
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		ParallaxModel model = new ParallaxModel();
		model.cameraOrigin.set(editor.getGameCamera().position);
		model.objectOrigin.set(transform.position.x, transform.position.y, 0);
		entity.add(model);
		end();
	}
	
}