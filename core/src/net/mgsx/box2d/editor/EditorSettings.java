package net.mgsx.box2d.editor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class EditorSettings 
{
	public boolean runSimulation = true;
	public Vector2 gravity = new Vector2(0, -9.807f); // earth gravity
	public float timeStep = 1.f / 60.f;
	public int velocityIterations = 8;
	public int positionIterations = 3;
	public BodyDef bodyDef = new BodyDef();
	public FixtureDef fixtureDef = new FixtureDef();
	
	public EditorSettings() {
		fixtureDef.density = 1.0f;
	}
	
	public BodyDef body() {
		BodyDef d = new BodyDef();
		d.active = bodyDef.active;
		d.allowSleep = bodyDef.allowSleep;
		d.angle = bodyDef.angle;
		d.angularDamping = bodyDef.angularDamping;
		d.angularVelocity = bodyDef.angularVelocity;
		d.awake = bodyDef.awake;
		d.bullet = bodyDef.bullet;
		d.fixedRotation = bodyDef.fixedRotation;
		d.gravityScale = bodyDef.gravityScale;
		d.linearDamping = bodyDef.linearDamping;
		d.linearVelocity.set(bodyDef.linearVelocity);
		d.position.set(bodyDef.position);
		d.type = bodyDef.type;
		return d;
	}
	public FixtureDef fixture() {
		FixtureDef d = new FixtureDef();
		d.density = fixtureDef.density;
		d.filter.categoryBits = fixtureDef.filter.categoryBits;
		d.filter.groupIndex = fixtureDef.filter.groupIndex;
		d.filter.maskBits = fixtureDef.filter.maskBits;
		d.friction = fixtureDef.friction;
		d.isSensor = fixtureDef.isSensor;
		d.restitution = fixtureDef.restitution;
		d.shape = fixtureDef.shape;
		return d;
	}
}
