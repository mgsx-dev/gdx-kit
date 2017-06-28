package net.mgsx.game.plugins.bullet.tools;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.bullet.components.BulletComponent;

public class BulletThirdPersonTool extends Tool
{
	public BulletThirdPersonTool(EditorScreen editor) {
		super("Third Person", editor);
		activator = Family.all(BulletComponent.class).get();
	}
	
	@Override
	public boolean keyDown(int keycode) 
	{
//		BulletComponent bullet = BulletComponent.components.get(currentEntity());
//		if(bullet != null && bullet.object instanceof btRigidBody){
//			btRigidBody body = (btRigidBody)bullet.object;
//			body.setFriction(.1f);
//			body.setRestitution(.8f);
//			body.setGravity(new Vector3(0,0,0));
//			Vector3 vel = new Vector3();
//			if(keycode == Input.Keys.J){
//				vel.x = -1;
//			}
//			else if(keycode == Input.Keys.L){
//				vel.x = 1;
//			}
//			else if(keycode == Input.Keys.I){
//				vel.z = -1;
//			}
//			else if(keycode == Input.Keys.K){
//				vel.z = 1;
//			}
//			else if(keycode == Input.Keys.O){
//				vel.y = 1;
//			}
//			 body.applyCentralImpulse(vel.scl(30f));
//			//body.setLinearVelocity(vel.scl(30f));
//			//body.setLinearFactor(vel);
//		}
		return super.keyDown(keycode);
	}
	
	@Override
	public void update(float deltaTime) {
		
		
		
		BulletComponent bullet = BulletComponent.components.get(currentEntity());
		if(bullet != null && bullet.object instanceof btRigidBody){
			
			Camera c = editor.game.camera;
			
			Vector3 pos = bullet.object.getWorldTransform().getTranslation(new Vector3());
			Vector3 cam = new Vector3(c.position);
			
			
			btRigidBody body = (btRigidBody)bullet.object;
			
			Vector3 characterLook = new Vector3(0,0,1).rot(body.getWorldTransform());
			
			Vector3 delta = pos.cpy().sub(cam);
			Vector3 target = pos.cpy().add(characterLook.cpy().nor().scl(-4));
			c.position.lerp(target, 2.5f * deltaTime);
			c.direction.set(delta.x, 0, delta.z).nor();
			c.up.set(Vector3.Y);
			c.lookAt(pos);
			c.update();
			
			
			body.setFriction(.1f);
//			body.setRestitution(.8f);
//			body.setGravity(new Vector3(0,-9,0));
			Vector3 vel = new Vector3();
			Vector3 avel = new Vector3();
			boolean key = false;
			if(Gdx.input.isKeyPressed(Input.Keys.J)){
				avel.y = 1;
				key = true;
			}
			else if(Gdx.input.isKeyPressed(Input.Keys.L)){
				avel.y = -1;
				key = true;
			}
			if(Gdx.input.isKeyPressed(Input.Keys.I)){
				vel.z = -1;
				key = true;
			}
			else if(Gdx.input.isKeyPressed(Input.Keys.K)){
				vel.z = 1;
				
				key = true;
			}
			if(Gdx.input.isKeyPressed(Input.Keys.O)){
				vel.y = 1;
				key = true;
			}
			Vector3 ovel = body.getLinearVelocity();
//			body.setGravity(new Vector3(0, -9, 0));
			body.setDamping(0,0);
			if(key){
				
				body.setActivationState(Collision.ACTIVE_TAG);
				// body.applyCentralImpulse(vel.scl(30f));
				body.setLinearFactor(new Vector3(1,1,1));
				body.applyCentralImpulse(characterLook.cpy().nor().scl(-10 * deltaTime * vel.z).add(new Vector3(0,vel.y * 30 * deltaTime,0)));
				//body.setLinearFactor(vel);
			}else{
				body.setLinearVelocity(ovel.set(0, ovel.y, 0));
			}
			body.setAngularFactor(Vector3.Y);
			body.setAngularVelocity(avel.scl(3f));
		}
	}
}
