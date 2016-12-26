package net.mgsx.game.plugins.g3d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.GL20;
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
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;

@Storable("g3d")
@EditableComponent(autoTool=false)
public class G3DModel implements Component, Duplicable, Serializable, Poolable
{
	public final static ComponentMapper<G3DModel> components = ComponentMapper.getFor(G3DModel.class);

	public ModelInstance modelInstance;
	public Vector3 origin = new Vector3(0,0,0); // TODO introduce a pre matrix ... ?
	public AnimationController animationController;
	public boolean blended;
	public BoundingBox localBoundary, globalBoundary;
	public boolean inFrustum;
	public Array<NodeBoundary> boundary;
	public boolean culling = false;
	
	// TODO should be in blend component (can be applyed to G2D as well)
	
	@Editable(type=EnumType.BLEND_MODE)
	public int blendSrc = GL20.GL_SRC_ALPHA;
	@Editable(type=EnumType.BLEND_MODE)
	public int blendDst = GL20.GL_ONE_MINUS_SRC_ALPHA;
	@Editable(type=EnumType.UNIT)
	public float opacity = 1;
	
	@Override
	public Component duplicate(Engine engine) 
	{
		G3DModel model = engine.createComponent(G3DModel.class);
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

	@Override
	public void reset() 
	{
		// TODO use pool for modelInstance and animation controller ! (pool by Model name)
		this.animationController = null;
		if(this.boundary != null) this.boundary = null;
		this.blended = false;
		this.culling = true;
		this.globalBoundary = null;
		this.localBoundary = null;
		this.inFrustum = false;
		this.modelInstance = null;
		this.origin.setZero();
	}
	
	
	
}
