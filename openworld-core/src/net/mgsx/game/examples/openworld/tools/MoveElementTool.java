package net.mgsx.game.examples.openworld.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;
import net.mgsx.game.plugins.bullet.components.BulletComponent;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;

public class MoveElementTool extends Tool
{

	@Inject BulletWorldSystem bulletWorld;
	@Editable public float angle;
	private Entity pickedEntity;
	
	public static enum Axis{None,X,Y,Z}
	@Editable
	public Axis axis = Axis.Y;
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button != Input.Buttons.LEFT) return false;
		Ray ray = camera().getPickRay(screenX, screenY);
		ray.direction.scl(camera().far);
		Ray rayResult = new Ray();
		Entity entity = bulletWorld.rayCast(ray, rayResult);
		if(entity != null){
			ObjectMeshComponent omc = ObjectMeshComponent.components.get(entity);
			if(omc != null){
				BulletComponent bullet = BulletComponent.components.get(entity);
				bullet.world.removeCollisionObject(bullet.object);
				pickedEntity = entity;
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(pickedEntity != null){
			Ray ray = camera().getPickRay(screenX, screenY);
			ray.direction.scl(camera().far);
			Ray rayResult = new Ray();
			Entity entity = bulletWorld.rayCast(ray, rayResult);
			if(entity != null){
				
				ObjectMeshComponent omc = ObjectMeshComponent.components.get(pickedEntity);
				rayResult.direction.nor();
				
				Vector3 position = rayResult.origin.cpy().mulAdd(rayResult.direction, new Vector3(omc.userObject.element.geo_x, omc.userObject.element.geo_y, 1).scl(.5f * omc.userObject.element.size));
				
				Vector3 axis = null;
				switch (this.axis) {
				case X:
					axis = Vector3.X;
					break;
				case Y:
					axis = Vector3.Y;
					break;
				case Z:
					axis = Vector3.Z;
					break;
				case None:
				default:
					break;
				}
				
				if(axis != null){
					omc.userObject.element.rotation.setFromCross(axis, rayResult.direction);
				}else{
					omc.userObject.element.rotation.idt();
				}
				
				omc.userObject.element.position.set(position);
				
				// force transform init
				omc.getTransform();
				
				omc.invalidate();
				
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(pickedEntity != null){
			BulletComponent bullet = BulletComponent.components.get(pickedEntity);
			ObjectMeshComponent omc = ObjectMeshComponent.components.get(pickedEntity);
			bullet.object.setWorldTransform(omc.getTransform());
			if(!bullet.object.isStaticObject()){
				((btDiscreteDynamicsWorld)bullet.world).addRigidBody((btRigidBody)bullet.object);
				bullet.object.forceActivationState(Collision.DISABLE_DEACTIVATION);
			}else{
				bullet.world.addCollisionObject(bullet.object);
			}
			pickedEntity = null;
		}
		return true;
	}
	
	
}
