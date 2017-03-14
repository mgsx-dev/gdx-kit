package net.mgsx.game.plugins.bullet.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.bullet.components.BulletComponent;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@Editable
public class BulletSphereTool extends Tool
{
	@Inject
	public BulletWorldSystem bulletWorldSystem;
	
	public BulletSphereTool(EditorScreen editor) {
		super("Sphere", editor);
	}
	
	@Override
	public boolean allowed(Array<Entity> selection) {
		return selection.size == 1 && G3DModel.components.has(selection.first());
	}
	
	@Override
	protected void activate() 
	{
		super.activate();
		
		final G3DModel model = G3DModel.components.get(currentEntity());
		
		BulletComponent bullet = getEngine().createComponent(BulletComponent.class);
		bullet.shape = new btSphereShape(2);
		btMotionState motionState = new btMotionState(){
			final Matrix4 transform = model.modelInstance.transform;
			@Override
			public void getWorldTransform(Matrix4 worldTrans) {
				worldTrans.set(transform);
			}
			@Override
			public void setWorldTransform(Matrix4 worldTrans) {
				transform.set(worldTrans);
			}
		};
		bullet.object = new btRigidBody(1, motionState, bullet.shape);
		bullet.world = bulletWorldSystem.collisionWorld;
		
		currentEntity().add(bullet);
		((btDiscreteDynamicsWorld)bulletWorldSystem.collisionWorld).addRigidBody((btRigidBody)bullet.object);
		
		
		
		bullet.object.setWorldTransform(model.modelInstance.transform);
	}

}
