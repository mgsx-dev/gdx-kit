package net.mgsx.game.plugins.bullet.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.bullet.components.BulletComponent;

public class BulletWorldSystem extends EntitySystem
{
	public btCollisionConfiguration collisionConfiguration;
	public btCollisionDispatcher dispatcher;
	public btBroadphaseInterface broadphase;
	public btConstraintSolver solver;
	public btCollisionWorld collisionWorld;
	public final Vector3 gravity = new Vector3(0, -9.8f, 0); 

	public int maxSubSteps = 5;
	public float fixedTimeStep = 1f / 60f;

	public BulletWorldSystem() {
		super(GamePipeline.PHYSICS);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		collisionConfiguration = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfiguration);
		broadphase = new btDbvtBroadphase();
		solver = new btSequentialImpulseConstraintSolver();
		collisionWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		((btDynamicsWorld)collisionWorld).setGravity(gravity);
	}

	@Override
	public void update(float deltaTime)
	{
		((btDynamicsWorld)collisionWorld).stepSimulation(deltaTime, maxSubSteps, fixedTimeStep);

	}
	
	/**
	 * 
	 * @param rayFrom initial ray, clipped at collision distance (if collides)
	 * @param rayResult position/normal at ray collision (if collides)
	 * @return the hit entity or null if no collision
	 */
	public Entity rayCast(Ray rayFrom, Ray rayResult){
		Object o = rayCastObject(rayFrom, rayResult);
		return o instanceof Entity ? (Entity)o : null;
	}

	public Object rayCastObject(Ray rayFrom, Ray rayResult){
		ClosestRayResultCallback resultCallback = new ClosestRayResultCallback(Vector3.Zero.cpy(), Vector3.Z.cpy());
		collisionWorld.rayTest(rayFrom.origin, rayFrom.origin.cpy().add(rayFrom.direction), resultCallback);
		boolean hasHit = resultCallback.hasHit();
		if(hasHit){
			Vector3 p = new Vector3();
			resultCallback.getHitPointWorld(p);
			rayFrom.direction.scl(resultCallback.getClosestHitFraction());
			resultCallback.getHitNormalWorld(rayResult.direction);
			rayResult.origin.set(rayFrom.origin).add(rayFrom.direction);
		}
		resultCallback.release();
		return hasHit ? resultCallback.getCollisionObject().userData : null;
	}

	public void createBox(Entity entity, final Matrix4 transform, float w, float h, float d, boolean dynamic) {
		BulletComponent bullet = getEngine().createComponent(BulletComponent.class);
		
		
		bullet.shape = new btBoxShape(new Vector3(w,h,d).scl(0.5f));
		
		if(dynamic)
			bullet.object = new btRigidBody(new btRigidBodyConstructionInfo(10, new btMotionState(){
				@Override
				public void getWorldTransform(Matrix4 worldTrans) {
					worldTrans.set(transform);
				}
				@Override
				public void setWorldTransform(Matrix4 worldTrans) {
					transform.set(worldTrans);
				}
			}, bullet.shape, new Vector3(1,1,1)));
		else{
			bullet.object = new btCollisionObject();
			bullet.object.setCollisionShape(bullet.shape);
			bullet.object.setWorldTransform(transform);
		}
		
		bullet.world = collisionWorld;
		bullet.object.setFriction(.99f);
		bullet.object.setRestitution(0.01f);
		bullet.object.userData = entity;
		
		if(dynamic){
			((btDiscreteDynamicsWorld)collisionWorld).addRigidBody((btRigidBody)bullet.object);
		}else{
			collisionWorld.addCollisionObject(bullet.object);
		}
		
		entity.add(bullet);
		
	}

	public void createSphere(Entity entity, final Matrix4 transform, float r, boolean dynamic) {
		BulletComponent bullet = getEngine().createComponent(BulletComponent.class);
		
		
		bullet.shape = new btSphereShape(r);
		
		if(dynamic)
			bullet.object = new btRigidBody(new btRigidBodyConstructionInfo(10, new btMotionState(){
				@Override
				public void getWorldTransform(Matrix4 worldTrans) {
					worldTrans.set(transform);
				}
				@Override
				public void setWorldTransform(Matrix4 worldTrans) {
					transform.set(worldTrans);
				}
			}, bullet.shape, new Vector3(1,1,1)));
		else{
			bullet.object = new btCollisionObject();
			bullet.object.setCollisionShape(bullet.shape);
			bullet.object.setWorldTransform(transform);
		}
		
		bullet.world = collisionWorld;
		bullet.object.setFriction(.99f);
		bullet.object.setRestitution(0.01f);
		bullet.object.userData = entity;
		
		if(dynamic){
			((btDiscreteDynamicsWorld)collisionWorld).addRigidBody((btRigidBody)bullet.object);
		}else{
			collisionWorld.addCollisionObject(bullet.object);
		}
		
		entity.add(bullet);
	}

	public void createFromModel(Entity entity, Model model, final Matrix4 transform, boolean dynamic) {
		BulletComponent bullet = getEngine().createComponent(BulletComponent.class);
		
		// TODO add dynamic support ?? not really possible natively by bullet
		// require at least convex hull with less of 100 vertices.
		// see : http://www.bulletphysics.org/mediawiki-1.5.8/index.php/Collision_Shapes#Meshes
		// quick approximation is to take max extends of the model boundary but should be
		// more accurate with center offset ...etc
		
		if(dynamic){
			bullet.shape = new btBoxShape(model.calculateBoundingBox(new BoundingBox()).max);
			bullet.object = new btRigidBody(new btRigidBodyConstructionInfo(10, new btMotionState(){
				@Override
				public void getWorldTransform(Matrix4 worldTrans) {
					worldTrans.set(transform);
				}
				@Override
				public void setWorldTransform(Matrix4 worldTrans) {
					transform.set(worldTrans);
				}
			}, bullet.shape, new Vector3(1,1,1)));
		}else{
			bullet.shape = Bullet.obtainStaticNodeShape(model.nodes);
			bullet.object = new btCollisionObject();
			bullet.object.setCollisionShape(bullet.shape);
			bullet.object.setWorldTransform(transform);
		}
		
		bullet.world = collisionWorld;
		bullet.object.setFriction(.99f);
		bullet.object.setRestitution(0.01f);
		bullet.object.userData = entity;
		
		if(dynamic){
			((btDiscreteDynamicsWorld)collisionWorld).addRigidBody((btRigidBody)bullet.object);
		}else{
			collisionWorld.addCollisionObject(bullet.object);
		}
		
		entity.add(bullet);
		
	}


}
