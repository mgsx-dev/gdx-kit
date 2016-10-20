package net.mgsx.game.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;

// TODO is it the right way to do it ?
// in term of entity/component concept, a movable
// is data (x,y,z) and could have a behavior for this data (track, player, btree ... and so on ...)
//
// TODO should be renamed Transform controller ..
// we could imagine an object translated by a box2D body and rotated by an AI. How
// to manage that with components :
// a TranslationComponent with an optional Translation controller
// a RotationComponent with an optional RotationController
// idem for scale ?
//
// in this way an entity can have one and only one translation controller at a time.
// but a special controller can have many other controllers which it sum up to set final position.
// 
// summary :
// Controller <- TransformControl <- CompositeTransformControl (as in blender ...)


public class Movable implements Component
{
	private Movable delegate;
	public Movable(){}
	public Movable(Movable delegate) {
		super();
		this.delegate = delegate;
	}
	public void moveBegin(Entity entity){if(delegate != null) delegate.moveBegin(entity);}
	public void move(Entity entity, Vector3 deltaWorld){if(delegate != null) delegate.move(entity, deltaWorld);}
	public void moveEnd(Entity entity){if(delegate != null) delegate.moveEnd(entity);}
	public void moveTo(Entity entity, Vector3 pos) {if(delegate != null) delegate.moveTo(entity, pos);}
	public void rotateTo(Entity entity, float angle) {if(delegate != null) delegate.rotateTo(entity, angle);}
	
	public void getPosition(Entity entity, Vector3 pos) {if(delegate != null) delegate.getPosition(entity, pos);}
	public float getRotation(Entity entity) { if(delegate != null) return delegate.getRotation(entity); else return 0;}
	
	public void getOrigin(Entity entity, Vector3 pos) {if(delegate != null) delegate.getOrigin(entity, pos);}
	public void setOrigin(Entity entity, Vector3 pos) {if(delegate != null) delegate.setOrigin(entity, pos);}
	
}
