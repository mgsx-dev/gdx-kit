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
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Hidden;
import net.mgsx.game.core.helpers.GLHelper;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.components.DirectionalLightComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.g3d.components.PointLightComponent;
import net.mgsx.game.plugins.g3d.components.ShadowCasting;

// TODO separate shadow to another system, the only common thing is environement object

// TODO supress warning for shadows ... have to watch new API when out.
@SuppressWarnings("deprecation")
@Storable("g3d.rendering")
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
	@Editable public Color fog = new Color(0,0,0,0);
	
	private int currentShadowSize;
	
	@Editable
	public Color ambient = new Color(0.4f, 0.4f, 0.4f, 1f);
	
	// TODO global listener
	private EntityListener listener = new EntityListener() {
		
		@Override
		public void entityRemoved(Entity entity) {
		}
		@Override
		public void entityAdded(Entity entity) {
			G3DModel model = G3DModel.components.get(entity);
			model.animationController = new AnimationController(model.modelInstance);
			model.applyBlending();
		}
	};
	
	
	
	public ModelBatch modelBatch;
	public Environment environment;
	
	private GameScreen engine;
	private ModelBatch shadowBatch;
	
	private Array<BaseLight> lights = new Array<BaseLight>();
	
	private ColorAttribute fogAttribute, ambientAttribute;
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(Family.all(G3DModel.class).get(), listener);
		directionalLights = engine.getEntitiesFor(Family.all(DirectionalLightComponent.class).get());
		pointLights = engine.getEntitiesFor(Family.all(PointLightComponent.class).exclude(Hidden.class).get());
		shadowCasts = engine.getEntitiesFor(Family.all(G3DModel.class, ShadowCasting.class).get());
		
		ambientAttribute = new ColorAttribute(ColorAttribute.AmbientLight);
		fogAttribute = new ColorAttribute(ColorAttribute.Fog);
		
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		engine.removeEntityListener(listener);
		super.removedFromEngine(engine);
	}


	public G3DRendererSystem(GameScreen engine) {
		super(Family.all(G3DModel.class).exclude(Hidden.class).get(), GamePipeline.RENDER);
		this.engine = engine;
		
		modelBatch = new ModelBatch();
		shadowBatch = new ModelBatch(new DepthShaderProvider());
		environment = new Environment();
	}
	
	public void setShaderProvider(ShaderProvider shaderProvider) {
		modelBatch.dispose();
		modelBatch = new ModelBatch(shaderProvider);
	}
	
	private void configureShadowLight()
	{
		int qualitySize = MathUtils.clamp(1 << shadowQuality, GLHelper.getTextureMinSize(), GLHelper.getTextureMaxSize());
		
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
		
		// rebuild light list. This is better than ashley listeners where
		// light references could be lost when components removed.
		environment.remove(lights);
		lights.clear();

		// gather all lights
		for(Entity entity : directionalLights)
		{
			DirectionalLightComponent dl = DirectionalLightComponent.components.get(entity);
			if(shadow == false && dl.shadow){
				shadow = true;
				
				configureShadowLight();
				
				shadowLight.color.set(dl.light.color);
				shadowLight.direction.set(dl.light.direction);
				environment.add(shadowLight);
				lights.add(shadowLight);
			}else{
				environment.add(dl.light);
				lights.add(dl.light);
			}
		}
		if(!shadow && shadowLight != null){
			// dispose providers since environement has changed
			// TODO use 2 versions instead !
			shadowLight.dispose();
			shadowLight = null;
			modelBatch.dispose();
		}
		
		for(Entity entity : pointLights)
		{
			PointLightComponent dl = PointLightComponent.components.get(entity);
			Transform2DComponent transform = Transform2DComponent.components.get(entity);
			if(transform != null) dl.light.position.set(transform.position, 0);
			environment.add(dl.light);
			lights.add(dl.light);
		}
		
		// update attributes
		updateAttribute(ambientAttribute, ambient);
		updateAttribute(fogAttribute, fog);
		
		Camera camera = engine.camera;
		
		if(shadow)
		{
			 if(fboStack.size > 0){
	        	fboStack.peek().end();
	        }
			 
			shadowLight.begin(camera.position, camera.direction);
	        shadowBatch.begin(shadowLight.getCamera());
	
	        for(Entity instance : shadowCasts){
	        	G3DModel model = G3DModel.components.get(instance);
	        	if(model.inFrustum ){ // TODO not true !
	        		shadowBatch.render(model.modelInstance);
	        	}
	        }
	
	        shadowBatch.end();
	        shadowLight.end();
	        
	        environment.shadowMap = shadowLight;
	        
	        // restore FBO
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
	
	private void updateAttribute(ColorAttribute attribute, Color color){
		if(color.a > 0){
			attribute.color.set(color);
			if(!environment.has(attribute.type)){
				environment.set(attribute);
			}
		}else{
			if(environment.has(attribute.type)){
				environment.remove(attribute.type);
			}
		}
	}
}