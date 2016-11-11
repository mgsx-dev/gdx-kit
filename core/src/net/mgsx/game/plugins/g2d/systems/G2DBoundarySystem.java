package net.mgsx.game.plugins.g2d.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.components.Transform2DComponent;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;
import net.mgsx.game.plugins.g2d.components.SpriteModel;

public class G2DBoundarySystem extends IteratingSystem
{
	public G2DBoundarySystem() {
		// we don't need transform since it is already applied to sprite, but we don't want to
		// process static ones. So this is just a tag filter.
		super(Family.all(SpriteModel.class, BoundaryComponent.class, Transform2DComponent.class).get(), GamePipeline.BEFORE_RENDER);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		// just compute boundary once in case of static entities
		engine.addEntityListener(Family.all(SpriteModel.class, BoundaryComponent.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
			}
			
			@Override
			public void entityAdded(Entity entity) {
				BoundaryComponent boundary = BoundaryComponent.components.get(entity);
				SpriteModel sprite = SpriteModel.components.get(entity);
				getBounds(boundary.box, sprite.sprite);
			}
		});
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		BoundaryComponent boundary = BoundaryComponent.components.get(entity);
		SpriteModel sprite = SpriteModel.components.get(entity);
		
		// compute local boundary from sprite
		getBounds(boundary.box, sprite.sprite);
	}
	
	private void getBounds(BoundingBox box, Sprite sprite){
		box.set(
				new Vector3(sprite.getX(), sprite.getY(), 0),
				new Vector3(sprite.getX() + sprite.getWidth(), sprite.getY() + sprite.getHeight(), 0));
	}
}
