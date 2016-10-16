package net.mgsx.plugins.g3d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.core.plugins.Movable;

public class ModelMove  extends Movable
{
	private ModelInstance modelInstance;
	
	public ModelMove(ModelInstance modelInstance) {
		this.modelInstance = modelInstance;
	}

	@Override
	public void move(Entity entity, Vector3 deltaWorld) 
	{
		modelInstance.transform.translate(deltaWorld);
	}
	
	@Override
	public void moveTo(Entity entity, Vector3 pos) 
	{
		modelInstance.transform.setTranslation(pos);
	}
	
	@Override
	public void getPosition(Entity entity, Vector3 pos) {
		modelInstance.transform.getTranslation(pos);
	}

	@Override
	public void rotateTo(Entity entity, float angle) {
		modelInstance.transform.setToRotation(0, 0, 1, angle);
	}

}