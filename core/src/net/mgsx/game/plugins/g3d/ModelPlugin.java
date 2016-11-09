package net.mgsx.game.plugins.g3d;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
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
import net.mgsx.game.core.components.Hidden;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.components.Transform2DComponent;
import net.mgsx.game.core.helpers.RenderDebugHelper;
import net.mgsx.game.core.helpers.systems.ComponentIteratingSystem;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.storage.Storage;
import net.mgsx.game.core.tools.ComponentTool;
import net.mgsx.game.plugins.g3d.animation.TextureAnimationComponent;
import net.mgsx.game.plugins.g3d.animation.TextureAnimationEditor;

public class ModelPlugin extends EditorPlugin
{
	private ModelBatch modelBatch;
	// private Array<G3DModel> modelInstances = new Array<G3DModel>();
	
	public static class ModelDebugSystem extends IteratingSystem {
		private final Editor editor;
		private final Settings settings;
		public ModelDebugSystem(Editor editor, Settings settings) {
			super(Family.all(G3DModel.class).get(), GamePipeline.RENDER_OVER);
			this.editor = editor;
			this.settings = settings;
		}

		@Override
		public void update(float deltaTime) {
			// TODO mode fill switchable : Gdx.gl.glEnable(GL20.GL_BLEND); and editor.shapeRenderer.begin(ShapeType.Filled);
			editor.shapeRenderer.setColor(1, 1, 1, 0.1f);
			editor.shapeRenderer.setProjectionMatrix(editor.camera.combined);
			editor.shapeRenderer.begin(ShapeType.Line);
			
			super.update(deltaTime);
			
			if(settings.cameraFrustum)
			{
				editor.gameCamera.update(true);
				editor.shapeRenderer.setColor(0, 0, 1, 1f);
				RenderDebugHelper.frustum(editor.shapeRenderer, editor.gameCamera.frustum);
				
			}
			editor.shapeRenderer.end();
		}

		@Override
		protected void processEntity(Entity entity, float deltaTime) 
		{
			G3DModel model = G3DModel.components.get(entity);
			editor.shapeRenderer.setColor(1, 0f, 0, 1f);
			BoundingBox box = model.globalBoundary;
			if(settings.boudaryBox)
			{
				RenderDebugHelper.box(editor.shapeRenderer, box);
			}
		
			if(model.inFrustum && settings.nodeBoudaryBox)
				for(NodeBoundary nb : model.boundary)
				{
					editor.shapeRenderer.setColor(1, 0.5f, 0, 1f);
					box = nb.global;
					RenderDebugHelper.box(editor.shapeRenderer, box);
				}
		}
	}

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
		
		public boolean culling = true; // TODO engine setting, not editor ... ?

		public boolean boudaryBox = false;
		public boolean nodeBoudaryBox = false;
		public boolean cameraFrustum = true;
	}
	
	static class FilesShader extends DefaultShaderProvider
	{
		FileHandle vertexShader, fragmentShader;
		public FilesShader(FileHandle vertexShader, FileHandle fragmentShader) {
			super(vertexShader, fragmentShader);
			this.vertexShader = vertexShader;
			this.fragmentShader = fragmentShader;
		}
		public FilesShader reload()
		{
			dispose();
			return new FilesShader(vertexShader, fragmentShader);
		}
		
	}
	
	Settings settings = new Settings();
	
	Environment environment;
	
	DirectionalLight light;
	
	ShaderProvider [] shaderProviders;
	
	@Override
	public void initialize(final Editor editor) 
	{
		Storage.register(G3DModel.class, "g3d");
		Storage.register(TextureAnimationComponent.class, "g3d.texAnim");
		
		editor.addTool(new AddModelTool(editor));
		
		
		// editor for model
		editor.registerPlugin(G3DModel.class, new G3DNodeEditor());
		
		// TODO select processor
		editor.addSelector(new ModelSelector(editor));
		
		// TODO env should be configurable ... in some way but it's not 1-1 mapping !
		
		FileHandle vs = Gdx.files.classpath("net/mgsx/game/plugins/g3d/shaders/pixel-vertex.glsl");
		FileHandle fs = Gdx.files.classpath("net/mgsx/game/plugins/g3d/shaders/pixel-fragment.glsl");
		
		shaderProviders = new ShaderProvider[ShaderType.values().length];
		shaderProviders[ShaderType.DEFAULT.ordinal()] = new DefaultShaderProvider();
		shaderProviders[ShaderType.VERTEX.ordinal()] = new DefaultShaderProvider();
		shaderProviders[ShaderType.FRAGMENT.ordinal()] = new FilesShader(vs, fs);
		shaderProviders[ShaderType.TOON.ordinal()] = new FilesShader(
				Gdx.files.local("../core/src/net/mgsx/game/plugins/g3d/shaders/platform-vertex.glsl"),
				Gdx.files.local("../core/src/net/mgsx/game/plugins/g3d/shaders/platform-fragment.glsl")); // TODO toon !
		
		
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
		
		// TODO just a patch for movable ...
		editor.entityEngine.addEntityListener(Family.one(G3DModel.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
//				G3DModel model = (G3DModel)entity.remove(G3DModel.class);
//				if(model != null){
//					modelInstances.removeValue(model, true);
					entity.remove(Movable.class);
//				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
				G3DModel model = entity.getComponent(G3DModel.class);
//				modelInstances.add(model);
				model.applyBlending();
//				if(entity.getComponent(Movable.class) == null) entity.add(new Movable(new ModelMove(model)));
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
        
        editor.entityEngine.addSystem(new ComponentIteratingSystem<G3DModel>(G3DModel.class, GamePipeline.BEFORE_RENDER) {
			@Override
			protected void processEntity(Entity entity, G3DModel component, float deltaTime) {
				if(component.animationController != null){
					component.animationController.update(deltaTime);
				}
			}
		});
        
        editor.entityEngine.addSystem(new IteratingSystem(Family.all(G3DModel.class, Transform2DComponent.class).exclude(Hidden.class).get(), GamePipeline.BEFORE_RENDER) {
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
  
		editor.entityEngine.addSystem(new IteratingSystem(Family.all(G3DModel.class).exclude(Hidden.class).get(), GamePipeline.BEFORE_RENDER) {
			@Override
			public void update(float deltaTime) {
				editor.gameCamera.update(true);
				super.update(deltaTime);
			}
			private void scan(Array<NodeBoundary> bounds, Iterable<Node> nodes){
				if(nodes == null) return;
				for(Node node : nodes){
					NodeBoundary b = new NodeBoundary();
					b.node = node;
					b.local = new BoundingBox();
					node.calculateBoundingBox(b.local, false);
					b.global = new BoundingBox();
					bounds.add(b);
					scan(bounds, node.getChildren());
				}
			}
			@Override
			protected void processEntity(Entity entity, float deltaTime) 
			{
				G3DModel model = G3DModel.components.get(entity);
				if(model.localBoundary == null){
					model.localBoundary = new BoundingBox();
					model.modelInstance.calculateBoundingBox(model.localBoundary);
					model.globalBoundary = new BoundingBox();
					model.boundary = new Array<NodeBoundary>();
					scan(model.boundary, model.modelInstance.nodes);
				}
				else if(model.culling){
					model.modelInstance.calculateBoundingBox(model.localBoundary);
				}
				if(settings.culling)
				{
					model.globalBoundary.set(model.localBoundary).mul(model.modelInstance.transform);
					model.inFrustum = editor.gameCamera.frustum.boundsInFrustum(model.globalBoundary);
					if(model.inFrustum){
						for(NodeBoundary b : model.boundary)
							b.update(model.modelInstance, editor.gameCamera.frustum);
					}
				}
				else
				{
					model.inFrustum = true;
					for(NodeBoundary b : model.boundary)
						b.show();
				}
			}
		});
  
		editor.entityEngine.addSystem(new IteratingSystem(Family.all(G3DModel.class).exclude(Hidden.class).get(), GamePipeline.RENDER) {
//			final private Iterable<G3DModel> visibleModels = new FilterIterable<G3DModel>(modelInstances) {
//				@Override
//				protected boolean keep(G3DModel element) {
//					return element.inFrustum;
//				}
//			};
//			final private Iterable<RenderableProvider> visibleRenderableProviders = new AdaptIterable<G3DModel, RenderableProvider>(visibleModels){
//				@Override
//				protected RenderableProvider adapt(G3DModel element) {
//					return element.modelInstance;
//				}
//				
//			};
			@Override
			public void update(float deltaTime) {
				
				// update environnement TODO editor specific ?
				((ColorAttribute)environment.get(ColorAttribute.AmbientLight)).color.set(settings.ambient);
				light.color.set(settings.diffuse);
				light.direction.set(settings.direction.transform(new Vector3(0,0,1)));
				
				modelBatch.begin(editor.camera); // TODO allow switch between persperctive and ortho for Box2D drawings ...
				super.update(deltaTime);
			    modelBatch.end();
			}
			@Override
			protected void processEntity(Entity entity, float deltaTime) {
				G3DModel model = G3DModel.components.get(entity);
				if(model.inFrustum){
					modelBatch.render(model.modelInstance, environment);
				}
			}
		});
		
		editor.entityEngine.addSystem(new ModelDebugSystem(editor, settings));
		
		
		editor.addTool(new ComponentTool("Texture animation", editor, G3DModel.class) {
			@Override
			protected Component createComponent(Entity entity) {
				TextureAnimationComponent model = new TextureAnimationComponent();
				return model;
			}
		});
		
		// texture effect
       editor.entityEngine.addSystem(new IteratingSystem(Family.all(TextureAnimationComponent.class, G3DModel.class).get(), GamePipeline.BEFORE_RENDER) {
			@Override
			protected void processEntity(Entity entity, float deltaTime) {
				TextureAnimationComponent component = entity.getComponent(TextureAnimationComponent.class);
				component.time += deltaTime;
				component.uOffset += component.uPerSec * deltaTime;
				component.vOffset += component.vPerSec * deltaTime;
				G3DModel model = entity.getComponent(G3DModel.class);
				for(Node node : model.modelInstance.nodes)
					for(NodePart part : node.parts){
						TextureAttribute ta = (TextureAttribute)part.material.get(TextureAttribute.Diffuse);
						ta.offsetU = component.uOffset;
						ta.offsetV = component.vOffset;
					}
			}
		});
       
       
       editor.registerPlugin(TextureAnimationComponent.class, new TextureAnimationEditor());

		// TODO global editor to synchronize perspective and orthographic camera
		// need a perspective camera
		
		
	}
}
