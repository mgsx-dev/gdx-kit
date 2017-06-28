package net.mgsx.game.plugins.bullet.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
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
public class BulletBoundaryTool extends Tool
{
	@Inject
	public BulletWorldSystem bulletWorldSystem;
	
	@Editable public float radiusOffset = 1;
	@Editable public float heightOffset = 1;
	
	public BulletBoundaryTool(EditorScreen editor) {
		super("Capsule", editor);
	}
	
	@Override
	public boolean allowed(Array<Entity> selection) {
		return selection.size == 1 && G3DModel.components.has(selection.first());
	}
	
	@Editable
	public void apply() 
	{
		final G3DModel model = G3DModel.components.get(currentEntity());
		
		currentEntity().remove(BulletComponent.class);
		
		BulletComponent bullet = getEngine().createComponent(BulletComponent.class);
		
		// compute boundary
		BoundingBox bbox = new BoundingBox();
		model.modelInstance.calculateBoundingBox(bbox);
		
		final Vector3 fcenter = bbox.getCenter(new Vector3());
		
		float radius = radiusOffset * Math.max(bbox.getWidth(), bbox.getDepth()) / 2;
		float height = Math.max(0, heightOffset * bbox.getHeight() - radius * 2);
		
		// TODO select shape from user ...
		bullet.shape = new btCapsuleShape(radius, height);
		
		btMotionState motionState = new btMotionState(){
			final Matrix4 transform = model.modelInstance.transform;
			final Vector3 center = fcenter;
			final Vector3 centerInv = new Vector3().set(fcenter).scl(-1);
			@Override
			public void getWorldTransform(Matrix4 worldTrans) {
				worldTrans.set(transform).translate(center);
			}
			@Override
			public void setWorldTransform(Matrix4 worldTrans) {
				transform.set(worldTrans).translate(centerInv);
			}
		};
		bullet.object = new btRigidBody(1, motionState, bullet.shape);
		bullet.world = bulletWorldSystem.collisionWorld;
		
		
		currentEntity().add(bullet);
		((btDiscreteDynamicsWorld)bulletWorldSystem.collisionWorld).addRigidBody((btRigidBody)bullet.object);
		
		
		
		bullet.object.setWorldTransform(model.modelInstance.transform);
	}

}
