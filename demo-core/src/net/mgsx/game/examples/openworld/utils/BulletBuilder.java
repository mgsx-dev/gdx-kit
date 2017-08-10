package net.mgsx.game.examples.openworld.utils;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public class BulletBuilder {

	btCollisionObject collisionObject;
	btCompoundShape compoundShape;
	btCollisionShape shape;
	
	Vector3 shapePosition = new Vector3();
	Vector3 currentShapePosition = null;
	
	Vector3 tmp = new Vector3();
	Matrix4 currentShapeTransform = new Matrix4();
	Matrix4 worldTransform = new Matrix4();
	
	Vector3 objectPosition = new Vector3();
	btMotionState motionState;
	float mass;
	int extraFlags;
	short filterGroup;
	short filterMask;
	boolean hasFilter;
	
	public BulletBuilder beginStatic(float x, float y, float z)
	{
		worldTransform.idt().translate(objectPosition.set(x,y,z));
		return beginStatic();
	}
	public BulletBuilder beginStatic(Vector3 position)
	{
		worldTransform.idt().translate(objectPosition.set(position));
		return beginStatic();
	}
	private BulletBuilder beginStatic(){
		reset();
		collisionObject = new btCollisionObject();
		collisionObject.setWorldTransform(worldTransform);
		return this;
	}
	
	public BulletBuilder beginKinematic(final Matrix4 transform){
		reset();
		motionState = new btMotionState(){
			@Override
			public void getWorldTransform(Matrix4 worldTrans) {
				worldTrans.set(transform);
			}
		};
		extraFlags = btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT;
		return this;
	}
	
	public btCollisionObject end(){
		btCollisionShape finalShape = compoundShape != null ? compoundShape : shape;
		if(finalShape == null){
			rollback();
			return null;
		}
		// static object
		if(collisionObject != null){
			collisionObject.setCollisionShape(finalShape);
		}
		// dynamic/kinematic object
		else{
			collisionObject = new btRigidBody(mass, motionState, finalShape);
		}
		collisionObject.setCollisionFlags(collisionObject.getCollisionFlags() | extraFlags);

		btCollisionObject result = collisionObject;
		collisionObject = null;
		compoundShape = null;
		shape = null;
		return result;
	}
	
	private void rollback(){
		if(collisionObject != null){
			collisionObject.dispose();
			collisionObject = null;
		}
	}
	
	public BulletBuilder beginShape(){
		closeShape();
		return this;
	}
	
	public BulletBuilder endShape(){
		closeShape();
		return this;
	}
	

	private void closeShape(){
		if(shape != null){
			boolean compound = compoundShape != null;
			if(currentShapePosition != null){
				currentShapeTransform.translate(currentShapePosition);
				compound = true;
			}
			if(compound){
				if(compoundShape == null){
					compoundShape = new btCompoundShape();
				}
				compoundShape.addChildShape(currentShapeTransform, shape);
				currentShapeTransform.idt();
				shape = null;
			}
		}
	}
	
	public BulletBuilder position(float x, float y, float z) {
		currentShapePosition = shapePosition.set(x, y, z).sub(objectPosition);
		return this;
	}

	public BulletBuilder cylinder(float hx, float hy, float hz) {
		shape = new btCylinderShape(tmp.set(hx, hy, hz));
		return this;
	}
	public BulletBuilder sphere(float radius) {
		shape = new btSphereShape(radius);
		return this;
	}
	
	public btCollisionObject commit(btCollisionWorld world){
		btCollisionObject object = end();
		if(object instanceof btRigidBody){
			btRigidBody rigidBody = (btRigidBody)object;
			if(world instanceof btDiscreteDynamicsWorld){
				btDiscreteDynamicsWorld discreteDynamicsWorld = (btDiscreteDynamicsWorld)world;
				if(hasFilter)
					discreteDynamicsWorld.addRigidBody(rigidBody, filterGroup, filterMask);
				else
					discreteDynamicsWorld.addRigidBody(rigidBody);
				reset();
				return object;
			}
		}
		if(hasFilter)
			world.addCollisionObject(collisionObject, filterGroup, filterMask);
		else
			world.addCollisionObject(collisionObject);
		reset();
		return object;
	}
	
	private void reset(){
		filterGroup = 0;
		filterMask = 0;
		motionState = null;
		mass = 0;
		extraFlags = 0;
		hasFilter = false;
	}
	public BulletBuilder filterGroup(int group) {
		this.filterGroup = (short)group;
		hasFilter = true;
		return this;
	}
	public BulletBuilder filterMask(int mask) {
		this.filterMask = (short)mask;
		hasFilter = true;
		return this;
	}
}
