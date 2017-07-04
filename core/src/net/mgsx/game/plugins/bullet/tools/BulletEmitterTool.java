package net.mgsx.game.plugins.bullet.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.bullet.components.BulletComponent;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;

@Editable
public class BulletEmitterTool extends Tool
{
	public static enum Type{
		CUBE, SPHERE
	}
	
	@Inject BulletWorldSystem bulletWorld;
	@Editable public float force = 1;
	@Editable public float size = 1;
	@Editable public float mass = 1;
	@Editable public float friction = .2f;
	@Editable public float restitution = .1f;
	@Editable public Type type = Type.CUBE;
	
	public BulletEmitterTool(EditorScreen editor) {
		super("Emitter Tool", editor);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) 
	{
		if(button == Input.Buttons.LEFT)
		{
			Ray ray = editor.getEditorCamera().camera().getPickRay(screenX, screenY);
			
			BulletComponent bullet = getEngine().createComponent(BulletComponent.class);
			
			switch(type){
			default:
			case CUBE:
				bullet.shape = new btBoxShape(new Vector3(1,1,1).scl(size / 2));
				break;
			case SPHERE:
				bullet.shape = new btSphereShape(size);
				break;
			}
			
			bullet.object = new btRigidBody(mass, new btDefaultMotionState(new Matrix4().setToTranslation(ray.origin)), bullet.shape);
			bullet.world = bulletWorld.collisionWorld;
			
			bullet.object.setFriction(friction);
			bullet.object.setRestitution(restitution);
			
			Entity entity = transcientEntity();
			entity.add(bullet);
			((btDiscreteDynamicsWorld)bulletWorld.collisionWorld).addRigidBody((btRigidBody)bullet.object);
			
			((btRigidBody)bullet.object).applyCentralImpulse(ray.direction.cpy().scl(force));
			
			getEngine().addEntity(entity);
			
			return true;
		}
		return false;
	}
	
}
