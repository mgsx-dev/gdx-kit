package net.mgsx.plugins.parallax;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.core.Editor;
import net.mgsx.core.plugins.Movable;
import net.mgsx.core.plugins.Plugin;
import net.mgsx.core.tools.ComponentTool;

public class ParallaxPlugin extends Plugin {

	private Vector3 camPos = new Vector3();
	private Vector2 pos = new Vector2();
	
	@Override
	public void initialize(final Editor editor) 
	{
		editor.addTool(new ComponentTool("Parallax", editor, Movable.class) {
			@Override
			protected Component createComponent(Entity entity) {
				
				ParallaxModel model = new ParallaxModel();
				model.cameraOrigin.set(editor.orthographicCamera.position);
				entity.getComponent(Movable.class).getPosition(entity, model.objectOrigin);
				return model;
			}
		});
		
		// TODO simplify
		editor.entityEngine.addSystem(new IteratingSystem(Family.one(ParallaxModel.class).get()) {
			
			@Override
			protected void processEntity(Entity entity, float deltaTime) 
			{
				ParallaxModel model = entity.getComponent(ParallaxModel.class);
				camPos.set(editor.orthographicCamera.position);
				pos
					.set(model.cameraOrigin.x, model.cameraOrigin.y)
					.sub(camPos.x, camPos.y)
					.scl(model.rateX-1, model.rateY-1) // .scl(0.05f) // TODO due to Tool pixelSize * 0.5f
					.add(model.objectOrigin.x, model.objectOrigin.y);
				entity.getComponent(Movable.class).moveTo(entity, pos);
			}
		});
		
		editor.registerPlugin(ParallaxModel.class, new ParallaxEditor());
	}
}
