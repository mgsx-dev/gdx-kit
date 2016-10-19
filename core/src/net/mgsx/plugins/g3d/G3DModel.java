package net.mgsx.plugins.g3d;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.core.components.Duplicable;

public class G3DModel implements Component, Duplicable, Serializable
{
	public ModelInstance modelInstance;
	public Vector3 origin = new Vector3(0,0,0); // TODO introduce a pre matrix ... ?
	public AnimationController animationController;
	
	@Override
	public Component duplicate() 
	{
		G3DModel model = new G3DModel();
		model.modelInstance = new ModelInstance(modelInstance);
		model.origin.set(origin);
		if(animationController != null){
			model.animationController = new AnimationController(model.modelInstance);
		}
		return model;
	}

	@Override
	public void write(Json json) 
	{
		json.writeField(this, "modelInstance");
		json.writeField(this, "origin");
	}

	@Override
	public void read(Json json, JsonValue jsonData) 
	{
		json.readField(this, "modelInstance", jsonData);
		json.readField(this, "origin", jsonData);
		animationController = new AnimationController(modelInstance);
	}
	
	
	
}
