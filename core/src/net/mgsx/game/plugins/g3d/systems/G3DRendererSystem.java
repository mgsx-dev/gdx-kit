package net.mgsx.game.plugins.g3d.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.components.Hidden;
import net.mgsx.game.core.helpers.FilesShader;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.components.DirectionalLightComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.g3d.components.PointLightComponent;
import net.mgsx.game.plugins.g3d.components.ShadowCasting;

// TODO separate shadow to another system, the only common thing is environement object

// TODO supress warning for shadows ... have to watch new API when out.
@SuppressWarnings("deprecation")
@EditableSystem("G3D Rendering")
public class G3DRendererSystem extends IteratingSystem 
{
	private ImmutableArray<Entity> directionalLights;
	private ImmutableArray<Entity> pointLights;
	private ImmutableArray<Entity> shadowCasts;
	
	
	private DirectionalShadowLight shadowLight; // unique shadow map for now
	
	// TODO should be in editor code !
	public static enum ShaderType{
		DEFAULT, VERTEX, FRAGMENT, TOON, SHADOW
	}
	
	@Editable
	public ShaderType shader = ShaderType.DEFAULT;
	
	@Editable
	public Color ambient = new Color(0.4f, 0.4f, 0.4f, 1f);
	
	private EntityListener listener = new EntityListener() {
		
		@Override
		public void entityRemoved(Entity entity) {
		}
		@Override
		public void entityAdded(Entity entity) {
			G3DModel model = G3DModel.components.get(entity);
			model.applyBlending();
		}
	};
	
	
	
	private ModelBatch modelBatch;
	public Environment environment;
	
	public ShaderProvider [] shaderProviders;

	private GameScreen engine;
	private ModelBatch shadowBatch;
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(Family.all(G3DModel.class).get(), listener);
		directionalLights = engine.getEntitiesFor(Family.all(DirectionalLightComponent.class).get());
		pointLights = engine.getEntitiesFor(Family.all(PointLightComponent.class).get());
		shadowCasts = engine.getEntitiesFor(Family.all(G3DModel.class, ShadowCasting.class).get());
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		engine.removeEntityListener(listener);
		super.removedFromEngine(engine);
	}


	public G3DRendererSystem(GameScreen engine) {
		super(Family.all(G3DModel.class).exclude(Hidden.class).get(), GamePipeline.RENDER);
		this.engine = engine;
		
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
		shaderProviders[ShaderType.SHADOW.ordinal()] = new FilesShader(
				Gdx.files.local("../core/src/net/mgsx/game/plugins/g3d/shaders/shadow-vertex.glsl"),
				Gdx.files.local("../core/src/net/mgsx/game/plugins/g3d/shaders/shadow-fragment.glsl")); // TODO toon !
		
		
		ShaderProvider switchableProvider = new ShaderProvider() {
			@Override
			public Shader getShader(Renderable renderable) {
				return shaderProviders[shader.ordinal()].getShader(renderable);
			}
			@Override
			public void dispose() {
			}
		};
		
		modelBatch = new ModelBatch(switchableProvider);
		
		shadowBatch = new ModelBatch(new DepthShaderProvider());
		
		
		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, ambient));
        
        // TODO how to adjust values ?
        shadowLight = new DirectionalShadowLight(1024, 1024, 50f, 50f, .1f, 100f);
	}

	@Override
	public void update(float deltaTime) 
	{
		boolean shadow = false;
		
		// gather all lights
		environment.clear();
		for(Entity entity : directionalLights)
		{
			DirectionalLightComponent dl = DirectionalLightComponent.components.get(entity);
			if(shadow == false && dl.shadow){
				shadow = true;
				
				shadowLight.color.set(dl.light.color);
				shadowLight.direction.set(dl.light.direction);
				environment.add(shadowLight);
			}else{
				environment.add(dl.light);
			}
		}
		
		for(Entity entity : pointLights)
		{
			PointLightComponent dl = PointLightComponent.components.get(entity);
			Transform2DComponent transform = Transform2DComponent.components.get(entity);
			if(transform != null) dl.light.position.set(transform.position, 0);
			environment.add(dl.light);
		}
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, ambient));
		
		
		Camera camera = engine.getRenderCamera();
		
		if(shadow)
		{
			shadowLight.begin(camera.position, camera.direction);
	        shadowBatch.begin(shadowLight.getCamera());
	
	        for(Entity instance : shadowCasts){
	        	G3DModel model = G3DModel.components.get(instance);
	        	if(model.inFrustum ){
	        		shadowBatch.render(model.modelInstance);
	        	}
	        }
	
	        shadowBatch.end();
	        shadowLight.end();
	        
	        environment.shadowMap = shadowLight;
		}
		else
		{
			environment.shadowMap = null;
		}
		
		modelBatch.begin(camera);
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
}