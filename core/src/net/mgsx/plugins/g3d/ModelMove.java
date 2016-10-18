package net.mgsx.plugins.g3d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.core.components.Movable;

public class ModelMove  extends Movable
{
	private G3DModel model;
	
	
	public ModelMove(G3DModel model) {
		this.model = model;
	}

	@Override
	public void move(Entity entity, Vector3 deltaWorld) 
	{
		model.modelInstance.transform.translate(deltaWorld);
	}
	
	@Override
	public void moveTo(Entity entity, Vector3 pos) 
	{
		model.modelInstance.transform.setTranslation(pos);
	}
	
	@Override
	public void getPosition(Entity entity, Vector3 pos) {
		model.modelInstance.transform.getTranslation(pos);
	}

	@Override
	public void rotateTo(Entity entity, float angle) {
		Vector3 position = new Vector3();
		model.modelInstance.transform.getTranslation(position);
		model.modelInstance.transform.idt();
		model.modelInstance.transform.translate(position);
		model.modelInstance.transform.rotate(0, 0, 1, angle);
		model.modelInstance.transform.translate(-model.origin.x, -model.origin.y, -model.origin.z-1);
//		model.modelInstance.transform.translate(-model.origin.x, -model.origin.y, -model.origin.z);
	}

	@Override
	public void getOrigin(Entity entity, Vector3 pos) {
		pos.set(model.origin);
	}
	
	@Override
	public void setOrigin(Entity entity, Vector3 pos) {
		model.origin.set(pos);
	}
}