package net.mgsx.game.plugins.box2d.systems;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import net.mgsx.game.core.annotations.Editable;

@Editable
public class Box2DEditorSettings 
{
	@Editable
	public BodyDef bodyDef = new BodyDef();
	
	@Editable
	public FixtureDef fixtureDef = new FixtureDef();
	
	public Box2DEditorSettings() {
		fixtureDef.density = 1.0f;
	}
	
	// TODO copy utils for box 2D ?
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
