package net.mgsx.game.plugins.parallax;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.helpers.EntityHelper.SingleComponentIteratingSystem;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.storage.Storage;
import net.mgsx.game.core.tools.ComponentTool;

/**
 * TODO change parallax to sprite group / layers ...
 */
public class ParallaxPlugin extends EditorPlugin {

	private Vector3 camPos = new Vector3();
	private Vector3 pos = new Vector3();
	
	@Override
	public void initialize(final Editor editor) 
	{
		Storage.register(ParallaxModel.class, "parallax");
		
		editor.addTool(new ComponentTool("Parallax", editor, Movable.class) {
			@Override
			protected Component createComponent(Entity entity) {
				
				ParallaxModel model = new ParallaxModel();
				model.cameraOrigin.set(editor.orthographicCamera.position);
				entity.getComponent(Movable.class).getPosition(entity, model.objectOrigin);
				return model;
			}
		});
		
		editor.entityEngine.addSystem(new SingleComponentIteratingSystem<ParallaxModel>(ParallaxModel.class) {
			@Override
			protected void processEntity(Entity entity, ParallaxModel model, float deltaTime) 
			{
				camPos.set(editor.orthographicCamera.position);
				pos
				.set(model.cameraOrigin)
				.sub(camPos)
				.scl(model.rateX-1, model.rateY-1, 1) // .scl(0.05f) // TODO due to Tool pixelSize * 0.5f
				.add(model.objectOrigin);
				entity.getComponent(Movable.class).moveTo(entity, pos);
			}
		});
		
		editor.registerPlugin(ParallaxModel.class, new ParallaxEditor());
	}
}
