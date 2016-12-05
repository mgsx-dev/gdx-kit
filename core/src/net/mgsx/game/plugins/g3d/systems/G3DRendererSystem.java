package net.mgsx.game.plugins.g3d.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.components.Hidden;
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
	
	final public Array<FrameBuffer> fboStack = new Array<FrameBuffer>();
	
	private DirectionalShadowLight shadowLight; // unique shadow map for now
	
	@Editable public int shadowQuality = 10;
	@Editable public Vector2 shadowSize = new Vector2(50, 50);
	@Editable public float shadowNear = .1f;
	@Editable public float shadowFar = 100f;
	private int currentShadowSize;
	
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
		
		modelBatch = new ModelBatch();
		
		shadowBatch = new ModelBatch(new DepthShaderProvider());
		
		
		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, ambient));
	}
	
	public void setShaderProvider(ShaderProvider shaderProvider) {
		modelBatch.dispose();
		modelBatch = new ModelBatch(shaderProvider);
	}
	
	@Editable
	public void updateShadowSettings()
	{
		if(shadowLight != null){
			shadowLight.dispose();
			shadowLight = null;
		}
		configureShadowLight();
	}

	private void configureShadowLight()
	{
		// TODO check max render buffer size instead
		if(shadowQuality < 0) shadowQuality = 0;
		if(shadowQuality > 12) shadowQuality = 12;
		int qualitySize = 1 << shadowQuality;
		
		if(shadowLight == null)
		{
			currentShadowSize = qualitySize;
			shadowLight = new DirectionalShadowLight(currentShadowSize, currentShadowSize, shadowSize.x, shadowSize.y, shadowNear, shadowFar);
		
			// TODO use 2 versions instead !
			// dispose providers since environement has changed
			modelBatch.dispose();
		}
		else if(currentShadowSize != qualitySize)
		{
			shadowLight.dispose();
			shadowLight = null;
			configureShadowLight();
			return;
		}
		
		shadowLight.getCamera().near = shadowNear;
		shadowLight.getCamera().far = shadowFar;
		shadowLight.getCamera().viewportWidth = shadowSize.x;
		shadowLight.getCamera().viewportHeight = shadowSize.y;
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
				
				configureShadowLight();
				
				shadowLight.color.set(dl.light.color);
				shadowLight.direction.set(dl.light.direction);
				environment.add(shadowLight);
			}else{
				environment.add(dl.light);
			}
		}
		if(!shadow && shadowLight != null){
			// dispose providers since environement has changed
			// TODO use 2 versions instead !
			modelBatch.dispose();
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
			 if(fboStack.size > 0){
	        	fboStack.peek().end();
	        }
			 
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
	        
	        // TODO restore FBO
	        if(fboStack.size > 0){
	        	fboStack.peek().begin();
	        }
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