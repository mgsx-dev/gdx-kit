package net.mgsx.game.plugins.bullet.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.RayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;

@Editable
public class BulletShooterTool extends Tool
{
	@Inject BulletWorldSystem bulletWorld;
	@Editable public float force = 1;
	
	public BulletShooterTool(EditorScreen editor) {
		super("Shoot Tool", editor);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) 
	{
		if(button == Input.Buttons.LEFT)
		{
			Ray ray = editor.getEditorCamera().camera().getPickRay(screenX, screenY);
			
			RayResultCallback resultCallback = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
			
			bulletWorld.collisionWorld.rayTest(ray.origin, ray.origin.cpy().mulAdd(ray.direction, 1e4f), resultCallback);
			
			if(resultCallback.hasHit()){
				btCollisionObject object = resultCallback.getCollisionObject();
				if(object instanceof btRigidBody){
					btRigidBody body = (btRigidBody)object;
					body.activate();
					body.applyImpulse(ray.direction.cpy().scl(force), 
							ray.origin.cpy().mulAdd(ray.direction, resultCallback.getClosestHitFraction()));
				}
			}
			
			resultCallback.release();
			return true;
		}
		return false;
	}
	
}
