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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

import net.mgsx.core.Editor;
import net.mgsx.core.GamePipeline;
import net.mgsx.core.components.Movable;
import net.mgsx.core.plugins.EditorPlugin;
import net.mgsx.core.storage.Storage;

public class ModelPlugin extends EditorPlugin
{

	private ModelBatch modelBatch;
	private Array<ModelInstance> modelInstances = new Array<ModelInstance>();

	@Override
	public void initialize(final Editor editor) 
	{
		Storage.register(G3DModel.class, "g3d");
		
		// TODO tool for model adding/loading (TODO use a special asset manager to propose already loaded assets like blender)
		// TODO a file can contains several files ... so on loading, propose list of nodes
		editor.addTool(new AddModelTool(editor));
		
		// TODO storage handler for model : just save reference
		
		// TODO model as movable (create a move model)
		
		// TODO editor for model
		
		// TODO select processor
		editor.addSelector(new ModelSelector(editor));
		
		// TODO render processor
		
		// TODO env should be configurable ... in some way but it's not 1-1 mapping !
		
		modelBatch = new ModelBatch();
		
		// synchronize modelInstances with entities
		editor.entityEngine.addEntityListener(Family.one(G3DModel.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				modelInstances.removeValue(entity.getComponent(G3DModel.class).modelInstance, true);
				entity.remove(Movable.class);
			}
			
			@Override
			public void entityAdded(Entity entity) {
				G3DModel model = entity.getComponent(G3DModel.class);
				modelInstances.add(model.modelInstance);
				entity.add(new Movable(new ModelMove(model)));
			}
		});
		
		final Environment environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

//        editor.entityEngine.addSystem(new SingleComponentIteratingSystem<G3DModel>(G3DModel.class, GamePipeline.BEFORE_RENDER) {
//			@Override
//			protected void processEntity(Entity entity, G3DModel model, float deltaTime) {
//				model.modelInstance.transform.translate(model.origin);
//				model.modelInstance.transform.translate(model.origin);
//				model.modelInstance.transform.translate(model.origin);
//			}
//		});
  
		editor.entityEngine.addSystem(new EntitySystem(GamePipeline.RENDER) {
			
			@Override
			public void update(float deltaTime) {
				
				modelBatch.begin(editor.perspectiveCamera);
				modelBatch.render(modelInstances, environment);
			    modelBatch.end();
			}
		});
		
		editor.entityEngine.addSystem(new EntitySystem() {
			
			@Override
			public void update(float deltaTime) {
				BoundingBox box = new BoundingBox();
				editor.shapeRenderer.setProjectionMatrix(editor.orthographicCamera.combined);
				editor.shapeRenderer.begin(ShapeType.Line);
				for(ModelInstance modelInstance : modelInstances){
					Vector3 vector = new Vector3();
					modelInstance.transform.getTranslation(vector);
					modelInstance.transform.setTranslation(vector.x, vector.y, 0); // XXX
					modelInstance.calculateBoundingBox(box);
					box.mul(modelInstance.transform); // .mul(modelInstance.nodes.get(0).globalTransform)
					editor.shapeRenderer.box(box.min.x, box.min.y, box.min.z, box.max.x - box.min.x, box.max.y - box.min.y, box.max.z - box.min.z);
				}
				editor.shapeRenderer.end();
			}
		});
		
		// TODO global editor to synchronize perspective and orthographic camera
		// need a perspective camera
		
		
	}
}
