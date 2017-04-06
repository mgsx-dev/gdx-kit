package net.mgsx.game.plugins.bullet.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;

import net.mgsx.game.core.GamePipeline;

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

}
