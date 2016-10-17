package net.mgsx.plugins.g3d;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.core.components.Duplicable;

public class G3DModel implements Component, Duplicable
{
	public ModelInstance modelInstance;
	public Vector3 origin = new Vector3(0,0,0); // TODO introduce a pre matrix ... ?
	
	@Override
	public Component duplicate() {
		G3DModel model = new G3DModel();
		model.modelInstance = new ModelInstance(modelInstance);
		model.origin.set(origin);
		return model;
	}
	
}
