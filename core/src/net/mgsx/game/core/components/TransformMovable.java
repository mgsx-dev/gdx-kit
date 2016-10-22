package net.mgsx.game.core.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;

// TODO delete this when movable will be clean, it's just a crossover Movable and Transform;
public class TransformMovable extends Movable
{
	@Override
	public void getPosition(Entity entity, Vector3 pos) {
		Transform2DComponent t = entity.getComponent(Transform2DComponent.class);
		if(t != null) pos.set(t.position.x, t.position.y, 0);
	}
	
	@Override
	public void move(Entity entity, Vector3 deltaWorld) {
		Transform2DComponent t = entity.getComponent(Transform2DComponent.class);
		if(t != null) t.position.add(deltaWorld.x, deltaWorld.y);
	}
	
	@Override
	public void moveTo(Entity entity, Vector3 pos) {
		Transform2DComponent t = entity.getComponent(Transform2DComponent.class);
		if(t != null) t.position.set(pos.x, pos.y);
	}
	
	@Override
	public float getRotation(Entity entity) {
		Transform2DComponent t = entity.getComponent(Transform2DComponent.class);
		if(t != null) return t.angle;
		return 0;
	}
	
	@Override
	public void rotateTo(Entity entity, float angle) {
		Transform2DComponent t = entity.getComponent(Transform2DComponent.class);
		if(t != null) t.angle = angle;
	}
	
	@Override
	public void getOrigin(Entity entity, Vector3 pos) {
		Transform2DComponent t = entity.getComponent(Transform2DComponent.class);
		if(t != null) pos.set(t.origin.x, t.origin.y, 0);	
	}
	
	@Override
	public void setOrigin(Entity entity, Vector3 pos) {
		Transform2DComponent t = entity.getComponent(Transform2DComponent.class);
		if(t != null) t.origin.set(pos.x, pos.y);	
	}
}
