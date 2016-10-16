package net.mgsx.core.plugins;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;

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
}
