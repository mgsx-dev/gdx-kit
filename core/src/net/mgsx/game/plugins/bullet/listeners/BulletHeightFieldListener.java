package net.mgsx.game.plugins.bullet.listeners;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btHeightfieldTerrainShape;

import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.bullet.components.BulletComponent;
import net.mgsx.game.plugins.bullet.components.BulletHeightFieldComponent;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;
import net.mgsx.game.plugins.core.components.HeightFieldComponent;

public class BulletHeightFieldListener extends EntitySystem
{
	@Inject public BulletWorldSystem bulletWorldSystem;
	
	private EntityListener listener;
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		engine.addEntityListener(Family.all(HeightFieldComponent.class, BulletHeightFieldComponent.class).get(), listener = new EntityListener() {
			
			@Override
			public void entityAdded(Entity entity) {
				createBullet(entity);
			}
			
			@Override
			public void entityRemoved(Entity entity) {
				entity.remove(BulletComponent.class);
			}
			
		});
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		engine.removeEntityListener(listener);
		super.removedFromEngine(engine);
	}
	
	private void createBullet(Entity entity) {
		HeightFieldComponent hfc = HeightFieldComponent.components.get(entity);
		
		int width = hfc.width;
		int height = hfc.height;
		
		FloatBuffer heightfieldData = ByteBuffer.allocateDirect(width * height * Float.SIZE / 8).order(ByteOrder.nativeOrder()).asFloatBuffer();
		heightfieldData.put(hfc.values);
		float min, max;
		min = max = hfc.values[0];
		for(int i=1 ; i<hfc.values.length ; i++){
			float value = hfc.values[i];
			if(value < min) min = value;
			if(value > max) max = value;
		}
		heightfieldData.flip();
		
		btHeightfieldTerrainShape heightfieldTerrainShape = new btHeightfieldTerrainShape(
				width, height, heightfieldData, 
				1f, // scaling is ignored for float data
				min, max, 
				1, // y-up axis
				false); // ?
		
		BulletComponent bullet = getEngine().createComponent(BulletComponent.class);
		bullet.world = bulletWorldSystem.collisionWorld;
		bullet.object = new btCollisionObject();
		bullet.shape = heightfieldTerrainShape;
		bullet.object.setCollisionShape(bullet.shape);
		
		// keep reference on buffer to prevent from GC
		BulletHeightFieldComponent bhfc = BulletHeightFieldComponent.components.get(entity);
		bhfc.data = heightfieldData;
		
		entity.add(bullet);
		bulletWorldSystem.collisionWorld.addCollisionObject(bullet.object);
		
		// compensate bullet central coordinates
		bullet.object.setWorldTransform(new Matrix4().setToTranslation(
				hfc.position.x + (hfc.width-1)/2f, 
				hfc.position.y + (max+min)/2, 
				hfc.position.z + (hfc.height-1)/2f));
	}

}
