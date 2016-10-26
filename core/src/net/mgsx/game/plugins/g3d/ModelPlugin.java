package net.mgsx.game.plugins.g3d;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.components.Transform2DComponent;
import net.mgsx.game.core.helpers.EntityHelper;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.storage.Storage;
import net.mgsx.game.core.tools.ComponentTool;
import net.mgsx.game.plugins.g3d.animation.TextureAnimationComponent;
import net.mgsx.game.plugins.g3d.animation.TextureAnimationEditor;

public class ModelPlugin extends EditorPlugin
{
	private ModelBatch modelBatch;
	private Array<ModelInstance> modelInstances = new Array<ModelInstance>();

	// TODO should be in editor code !
	public static enum ShaderType{
		DEFAULT, VERTEX, FRAGMENT, TOON
	}
	
	public static class Settings
	{
		public ShaderType shader = ShaderType.DEFAULT;
		
		public Color ambient = new Color(0.4f, 0.4f, 0.4f, 1f);
		public Color diffuse = new Color(0.8f, 0.8f, 0.8f, 1f);

		public Quaternion direction = new Quaternion().setFromAxisRad(-1f, -0.8f, -0.2f, 0);
	}
	
	Settings settings = new Settings();
	
	Environment environment;
	
	DirectionalLight light;
	
	private ShaderProvider [] shaderProviders;
	
	@Override
	public void initialize(final Editor editor) 
	{
		Storage.register(G3DModel.class, "g3d");
		Storage.register(TextureAnimationComponent.class, "g3d.texAnim");
		
		// TODO tool for model adding/loading (TODO use a special asset manager to propose already loaded assets like blender)
		// TODO a file can contains several files ... so on loading, propose list of nodes
		editor.addTool(new AddModelTool(editor));
		
		// TODO storage handler for model : just save reference
		
		// TODO model as movable (create a move model)
		
		// editor for model
		editor.registerPlugin(G3DModel.class, new G3DNodeEditor());
		
		// TODO select processor
		editor.addSelector(new ModelSelector(editor));
		
		// TODO render processor
		
		// TODO env should be configurable ... in some way but it's not 1-1 mapping !
		
		FileHandle vs = Gdx.files.classpath("net/mgsx/game/plugins/g3d/shaders/pixel-vertex.glsl");
		FileHandle fs = Gdx.files.classpath("net/mgsx/game/plugins/g3d/shaders/pixel-fragment.glsl");
		
		shaderProviders = new ShaderProvider[ShaderType.values().length];
		shaderProviders[ShaderType.DEFAULT.ordinal()] = new DefaultShaderProvider();
		shaderProviders[ShaderType.VERTEX.ordinal()] = new DefaultShaderProvider();
		shaderProviders[ShaderType.FRAGMENT.ordinal()] = new DefaultShaderProvider(vs, fs);
		shaderProviders[ShaderType.TOON.ordinal()] = new DefaultShaderProvider(vs, fs); // TODO toon !
		
		
		ShaderProvider switchableProvider = new ShaderProvider() {
			@Override
			public Shader getShader(Renderable renderable) {
				return shaderProviders[settings.shader.ordinal()].getShader(renderable);
			}
			@Override
			public void dispose() {
			}
		};
		
		modelBatch = new ModelBatch(switchableProvider);
		
		// synchronize modelInstances with entities
		editor.entityEngine.addEntityListener(Family.one(G3DModel.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				G3DModel model = (G3DModel)entity.remove(G3DModel.class);
				if(model != null){
					modelInstances.removeValue(model.modelInstance, true);
					entity.remove(Movable.class);
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
				G3DModel model = entity.getComponent(G3DModel.class);
				modelInstances.add(model.modelInstance);
				if(entity.getComponent(Movable.class) == null) entity.add(new Movable(new ModelMove(model)));
			}
		});
		
		light = new DirectionalLight().set(settings.diffuse, settings.direction.transform(new Vector3(0,0,1)) );
		
		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, settings.ambient));
        environment.add(light);

//        editor.entityEngine.addSystem(new SingleComponentIteratingSystem<G3DModel>(G3DModel.class, GamePipeline.BEFORE_RENDER) {
//			@Override
//			protected void processEntity(Entity entity, G3DModel model, float deltaTime) {
//				model.modelInstance.transform.translate(model.origin);
//				model.modelInstance.transform.translate(model.origin);
//				model.modelInstance.transform.translate(model.origin);
//			}
//		});
        
        editor.entityEngine.addSystem(new EntityHelper.SingleComponentIteratingSystem<G3DModel>(G3DModel.class, GamePipeline.BEFORE_RENDER) {
			@Override
			protected void processEntity(Entity entity, G3DModel component, float deltaTime) {
				if(component.animationController != null){
					component.animationController.update(deltaTime);
				}
			}
		});
        
        editor.entityEngine.addSystem(new IteratingSystem(Family.all(G3DModel.class, Transform2DComponent.class).get(), GamePipeline.BEFORE_RENDER) {
			@Override
			protected void processEntity(Entity entity, float deltaTime) {
				G3DModel model = entity.getComponent(G3DModel.class);
				Transform2DComponent transformation = entity.getComponent(Transform2DComponent.class);
				if(transformation.enabled){
					model.modelInstance.transform.idt();
					model.modelInstance.transform.translate(transformation.position.x, transformation.position.y, 0); // 0 is sprite plan
					model.modelInstance.transform.rotate(0, 0, 1, transformation.angle * MathUtils.radiansToDegrees);
					model.modelInstance.transform.translate(-model.origin.x, -model.origin.y, -model.origin.z);
					model.modelInstance.transform.translate(-transformation.origin.x, -transformation.origin.y, 0);
				}
			}
		});
  
  
		editor.entityEngine.addSystem(new EntitySystem(GamePipeline.RENDER) {
			
			@Override
			public void update(float deltaTime) {
				
				// update environnement TODO editor specific ?
				((ColorAttribute)environment.get(ColorAttribute.AmbientLight)).color.set(settings.ambient);
				light.color.set(settings.diffuse);
				light.direction.set(settings.direction.transform(new Vector3(0,0,1)));
				
				modelBatch.begin(editor.camera); // TODO allow switch between persperctive and ortho for Box2D drawings ...
				modelBatch.render(modelInstances, environment);
			    modelBatch.end();
			}
		});
		
		editor.entityEngine.addSystem(new EntitySystem(GamePipeline.RENDER_OVER) {
			
			@Override
			public void update(float deltaTime) {
				BoundingBox box = new BoundingBox();
				// TODO mode fill switchable : Gdx.gl.glEnable(GL20.GL_BLEND); and editor.shapeRenderer.begin(ShapeType.Filled);
				editor.shapeRenderer.setColor(1, 1, 1, 0.1f);
				editor.shapeRenderer.setProjectionMatrix(editor.camera.combined);
				editor.shapeRenderer.begin(ShapeType.Line);
				for(ModelInstance modelInstance : modelInstances){
					modelInstance.calculateBoundingBox(box);
					box.mul(modelInstance.transform); // .mul(modelInstance.nodes.get(0).globalTransform)
					
					// TODO it works but i don't know why max Z ... same result with opposite depth.
					editor.shapeRenderer.box(box.min.x, box.min.y, Math.max(box.min.z, box.max.z), box.getWidth(), box.getHeight(), box.getDepth());
				}
				editor.shapeRenderer.end();
			}
		});
		
		
		editor.addTool(new ComponentTool("Texture animation", editor, G3DModel.class) {
			@Override
			protected Component createComponent(Entity entity) {
				TextureAnimationComponent model = new TextureAnimationComponent();
				return model;
			}
		});
		
       editor.entityEngine.addSystem(new EntityHelper.SingleComponentIteratingSystem<TextureAnimationComponent>(TextureAnimationComponent.class, GamePipeline.BEFORE_RENDER) {
			@Override
			protected void processEntity(Entity entity, TextureAnimationComponent component, float deltaTime) {
				G3DModel model = entity.getComponent(G3DModel.class);
				for(Node node : model.modelInstance.nodes)
					for(NodePart part : node.parts){
						TextureAttribute ta = (TextureAttribute)part.material.get(TextureAttribute.Diffuse);
						ta.offsetU += deltaTime * component.uPerSec;
						ta.offsetV += deltaTime * component.vPerSec;
						part.material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)); // XXX hack to force blending
					}
			}
		});
       
       editor.registerPlugin(TextureAnimationComponent.class, new TextureAnimationEditor());

		// TODO global editor to synchronize perspective and orthographic camera
		// need a perspective camera
		
		
	}
}
