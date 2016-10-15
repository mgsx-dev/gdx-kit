package net.mgsx.plugins.g3d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.Array;

import net.mgsx.core.Editor;
import net.mgsx.core.plugins.Plugin;

public class ModelPlugin extends Plugin
{

	private ModelBatch modelBatch;
	private Array<ModelInstance> modelInstances = new Array<ModelInstance>();

	@Override
	public void initialize(final Editor editor) 
	{
		
		// TODO tool for model adding/loading (TODO use a special asset manager to propose already loaded assets like blender)
		// TODO a file can contains several files ... so on loading, propose list of nodes
		editor.addTool(new AddModelTool(editor));
		
		// TODO storage handler for model : just save reference
		
		// TODO model as movable (create a move model)
		
		// TODO editor for model
		
		// TODO select processor
		editor.addGlobalTool(new SelectModelTool(editor));
		
		// TODO render processor
		
		// TODO env should be configurable ... in some way but it's not 1-1 mapping !
		
		modelBatch = new ModelBatch();
		
		editor.entityEngine.addEntityListener(Family.one(G3DModel.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				modelInstances.removeValue(entity.getComponent(G3DModel.class).modelInstance, true);
			}
			
			@Override
			public void entityAdded(Entity entity) {
				modelInstances.add(entity.getComponent(G3DModel.class).modelInstance);
			}
		});
		
		final Environment environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		editor.entityEngine.addSystem(new EntitySystem() {
			
			@Override
			public void update(float deltaTime) {
				modelBatch.begin(editor.perspectiveCamera);
				modelBatch.render(modelInstances, environment);
			    modelBatch.end();
			}
		});
		
		
		// TODO global editor to synchronize perspective and orthographic camera
		// need a perspective camera
		
		
	}
}
