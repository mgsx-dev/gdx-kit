package net.mgsx.game.plugins.g3d;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.components.Duplicable;

public class G3DModel implements Component, Duplicable, Serializable
{
	public ModelInstance modelInstance;
	public Vector3 origin = new Vector3(0,0,0); // TODO introduce a pre matrix ... ?
	public AnimationController animationController;
	public boolean blended;
	public BoundingBox localBoundary, globalBoundary;
	public boolean inFrustum;
	public Array<NodeBoundary> boundary;
	public boolean culling = false;
	
	@Override
	public Component duplicate() 
	{
		G3DModel model = new G3DModel();
		model.modelInstance = new ModelInstance(modelInstance);
		model.origin.set(origin);
		if(animationController != null){
			model.animationController = new AnimationController(model.modelInstance);
		}
		model.blended = blended;
		return model;
	}

	@Override
	public void write(Json json) 
	{
		json.writeField(this, "modelInstance");
		json.writeField(this, "origin");
		json.writeField(this, "blended");
		json.writeField(this, "culling");
	}

	@Override
	public void read(Json json, JsonValue jsonData) 
	{
		json.readField(this, "modelInstance", jsonData);
		json.readField(this, "origin", jsonData);
		json.readField(this, "blended", jsonData);
		json.readField(this, "culling", jsonData);
		animationController = new AnimationController(modelInstance);
	}

	public void applyBlending() 
	{
		for(Material material : modelInstance.materials){
			BlendingAttribute attr = (BlendingAttribute)material.get(BlendingAttribute.Type);
			if(attr == null){
				attr = new BlendingAttribute(1);
				material.set(attr);
			}
			attr.blended = blended;
		}
	}
	
	
	
}
