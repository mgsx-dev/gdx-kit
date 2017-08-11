package net.mgsx.game.examples.openworld.systems;

import java.util.Calendar;
import java.util.TimeZone;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.PostInitializationListener;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.plugins.g3d.components.DirectionalLightComponent;
import net.mgsx.game.plugins.g3d.systems.G3DRendererSystem;

@Storable(value="ow.env")
@EditableSystem
public class OpenWorldEnvSystem extends EntitySystem implements PostInitializationListener
{
	@Inject OpenWorldGeneratorSystem generator;
	@Inject G3DRendererSystem g3dRender;
	
	@Editable public Color fogColor = new Color(1.0f, 0.8f, 0.7f, 1.0f);
	
	@Editable(type=EnumType.UNIT, realtime=true) public Vector3 sunDirection = new Vector3(.5f, -1f, .5f).nor();
	
	@Editable public float waterLevelRate = .3f;
	public transient float waterLevel;

	/** normalized time : range from 0 (midnight) to 1 (midnight), O.5 is noon */
	@Editable(realtime=true) public float timeOfDay;
	@Editable(realtime=true) public float temperature;
	
	@Editable public Color nightColor = new Color(.1f, .1f, .15f, 1f);
	@Editable public Color dayColor = new Color(.95f, 1.3f, 1.3f, 1f);
	@Editable public Color sunsetColor = new Color(.9f, .85f, .70f, 1f);
	@Editable public Color sunriseColor = new Color(.1f, .1f, .1f, 1f);
		
	
	@Editable public float fogExponent = 10f;
	
	@Inject public WeatherSystem weather;
	@Editable public boolean autoSun, autoTime;

	@Editable public float time;

	@Editable public Color waterColor = new Color(.1f, .1f, 1f, 1f);
	
	/** this is an offset time in hour relative to realtime */
	@Editable public transient float timeOffset = 0;
	
	ImmutableArray<Entity> allDirLights;
	
	public OpenWorldEnvSystem() {
		super(GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		allDirLights = getEngine().getEntitiesFor(Family.all(DirectionalLightComponent.class).get());
	}
	
	@Override
	public void onPostInitialization() {
		g3dRender.setShaderProvider(new DefaultShaderProvider(
				Gdx.files.internal("shaders/default-openworld.vs"),
				Gdx.files.internal("shaders/default-openworld.fs")){
			
			@Override
			protected Shader createShader(Renderable renderable) {
				return new DefaultShader(renderable, config){
					{
						fogExponentLocation = register(new Uniform("u_fogExponent"));
					}
					private int fogExponentLocation;
					
					@Override
					public void begin(Camera camera, RenderContext context) {
						super.begin(camera, context);
						set(fogExponentLocation, fogExponent);
					}
				};
			}
			
		});
		
	}
	
	@Override
	public void update(float deltaTime) {
		time += deltaTime;
		if(autoTime){
			// TODO timezone gives wrong values 
			// Gives -2 error
			// Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"), Locale.FRANCE).get(Calendar.HOUR_OF_DAY)
			// Gives -1 error
			// Calendar.getInstance(TimeZone.getDefault()).get(Calendar.HOUR_OF_DAY);
			// This hardcoded value gives good value for france in summer ...
			// Calendar.getInstance(TimeZone.getTimeZone("GMT+2")).get(Calendar.HOUR_OF_DAY);
			
			// TODO calendar and time zone generate GC
			Calendar cal = Calendar.getInstance(TimeZone.getDefault());
			float ms = cal.get(Calendar.MILLISECOND) / 1000f;
			float secs = (cal.get(Calendar.SECOND) + ms) / 60f;
			float minutes = (cal.get(Calendar.MINUTE) + secs) / 60f;
			timeOfDay = (cal.get(Calendar.HOUR_OF_DAY) + minutes) / 24f;
			timeOfDay += timeOffset / 24f;
		}
		// always clip/wrap to unit normalized value.
		timeOfDay = timeOfDay % 1f;
		
		float angle = ((timeOfDay - weather.sunrise) / (weather.sunset - weather.sunrise));
		
		// TODO temperature = f(angle)
		temperature = MathUtils.lerp(-4, 45, (1-(float)Math.abs(MathUtils.clamp(angle-0.5, -1, 1))));
		if(autoSun){
			
			float half = (timeOfDay - weather.sunrise) / (weather.sunset - weather.sunrise);
			sunDirection.y = MathUtils.clamp(-MathUtils.sin(half * (float)Math.PI) , -1, 1);
			sunDirection.x = MathUtils.clamp(-MathUtils.cos(half * (float)Math.PI) , -1, 1);
			sunDirection.z = .1f;
			sunDirection.nor();
		}
		
		sunDirection.nor();
		
		float fogAlpha = fogColor.a;
		
		if(angle < 0.2f){
			float t = MathUtils.clamp((angle + .2f)/0.4f, 0, 1);
			fogColor.set(nightColor).lerp(sunriseColor, t);
		}
		else if(angle < 0.3f){
			float t = MathUtils.clamp((angle - .2f) / 0.1f, 0, 1);
			fogColor.set(sunriseColor).lerp(dayColor, t);
		}
		else if(angle < 1f){
			float t = MathUtils.clamp((angle-0.8f)/0.1f, 0, 1);
			fogColor.set(dayColor).lerp(sunsetColor, t);
		}
		else if(angle > 1f){
			float t = MathUtils.clamp((angle - 1.2f) / 0.1f, 0, 1);
			fogColor.set(sunsetColor).lerp(nightColor, t);
		}
		fogColor.a = fogAlpha;
		
		waterLevel = generator.scale * waterLevelRate;
		
		
		float luminosity = MathUtils.clamp(timeOfDay * (1-timeOfDay) * 4, 0, 1) * 2;
		
		// synchronize G3DRendering
		g3dRender.fog.set(fogColor).mul(luminosity);
		g3dRender.ambient.set(fogColor).mul(luminosity);
		
		// create a directional light if needed
		
		DirectionalLightComponent firstLight;
		if(allDirLights.size() < 1){
			firstLight = getEngine().createComponent(DirectionalLightComponent.class);
			getEngine().addEntity(getEngine().createEntity().add(firstLight));
		}else{
			firstLight = DirectionalLightComponent.components.get(allDirLights.first());
		}
		firstLight.light.direction.set(sunDirection);
		firstLight.light.color.set(fogColor).mul(luminosity);
	}
	
}
