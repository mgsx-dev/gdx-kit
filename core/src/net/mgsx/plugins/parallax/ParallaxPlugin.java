package net.mgsx.plugins.parallax;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.core.Editor;
import net.mgsx.core.plugins.Movable;
import net.mgsx.core.plugins.Plugin;

public class ParallaxPlugin extends Plugin {

	private Vector3 camPos = new Vector3();
	private Vector2 pos = new Vector2();
	
	@Override
	public void initialize(final Editor editor) 
	{
		editor.entityEngine.addEntityListener(Family.one(Movable.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void entityAdded(Entity entity) {
				ParallaxModel parallax = new ParallaxModel();
//				parallax.rateX = MathUtils.random(-3, 3);
//				parallax.rateY = MathUtils.random(-3, 3);
				entity.getComponent(Movable.class).getPosition(entity, parallax.objectOrigin);
				parallax.cameraOrigin.set(editor.orthographicCamera.position);
				entity.add(parallax);
			}
		});
		
		editor.entityEngine.addSystem(new IteratingSystem(Family.one(Movable.class).get()) {
			
			@Override
			protected void processEntity(Entity entity, float deltaTime) 
			{
				ParallaxModel model = entity.getComponent(ParallaxModel.class);
				camPos.set(editor.orthographicCamera.position);
				pos
					.set(model.cameraOrigin.x, model.cameraOrigin.y)
					.sub(camPos.x, camPos.y)
					.scl(model.rateX, model.rateY)
					.add(model.objectOrigin.x, model.objectOrigin.y);
				entity.getComponent(Movable.class).moveTo(entity, pos);
			}
		});
		
		editor.registerPlugin(ParallaxModel.class, new ParallaxEditor());
	}
}
