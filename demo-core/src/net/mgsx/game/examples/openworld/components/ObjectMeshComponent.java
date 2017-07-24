package net.mgsx.game.examples.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.examples.openworld.systems.UserObjectSystem.UserObject;

public class ObjectMeshComponent implements Component, Poolable
{
	
	public final static ComponentMapper<ObjectMeshComponent> components = ComponentMapper.getFor(ObjectMeshComponent.class);
	
	private ModelInstance instance;

	public transient boolean sharedMesh;
	
	public UserObject userObject;

	private boolean dirty = true;
	
	// just used for single meshes (should be computed for each rendered parts)
	private Matrix4 finalTransform = new Matrix4();

	public Mesh getMeshToRender(){
		return instance.model.meshes.get(0);
	}
	
	@Override
	public void reset() {
		if(instance != null) {
			if(!sharedMesh){
				instance.model.dispose();
			}
			instance = null;
		}
		dirty = true;
	}

	/**
	 * this is the world transform for rendering ({@link #getMeshToRender()})
	 * TODO will be obsolete when multi mesh and model batch will be implemented ...
	 * @return
	 */
	public Matrix4 getWorldTransform() {
		update();
		return finalTransform;
	}
	
	/**
	 * This is the world transform for physics.
	 * transform is computed from position/rotation state in any case to provide
	 * an initial transform for dynamic objects.
	 * @return
	 */
	public Matrix4 getTransform() {
		return instance.transform.set(userObject.element.position, userObject.element.rotation);
	}

	public void setInstance(ModelInstance modelInstance) {
		this.instance = modelInstance;
		invalidate();
	}

	public void update() 
	{
		// get or set global transform
		if(userObject.element.dynamic){
			// dynamic object have instance.transform updated automatically by bullet
			// so we just extract position and orientation.
			instance.transform.getTranslation(userObject.element.position);
			instance.transform.getRotation(userObject.element.rotation);
		} 
		else if(dirty){
			// static objects are updated manually
			instance.transform.set(userObject.element.position, userObject.element.rotation);
		}
		
		// update internal model (animations) if necessary
		// TODO not necessary if no animated internally
		boolean dirtyModel = false;
		if(dirtyModel){
			instance.calculateTransforms();
			dirtyModel = false;
		}
		
		// update final rendering matrix
		if(dirty || userObject.element.dynamic){
			// set transform from first unique node
			finalTransform.set(instance.transform).mul(instance.nodes.get(0).globalTransform);
			dirty = false;
		}
	}

	public void invalidate() {
		dirty = true;
	}

	
}
