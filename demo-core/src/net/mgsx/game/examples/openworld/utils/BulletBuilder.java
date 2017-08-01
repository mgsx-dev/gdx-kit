package net.mgsx.game.examples.openworld.utils;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;

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
		collisionObject = new btCollisionObject();
		collisionObject.setWorldTransform(worldTransform);
		return this;
	}
	
	public btCollisionObject end(){
		if(compoundShape != null){
			collisionObject.setCollisionShape(compoundShape);
		}else if(shape != null){
			collisionObject.setCollisionShape(shape);
		}else{
			collisionObject.dispose();
			collisionObject = null;
		}
		btCollisionObject result = collisionObject;
		collisionObject = null;
		compoundShape = null;
		shape = null;
		return result;
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
}
