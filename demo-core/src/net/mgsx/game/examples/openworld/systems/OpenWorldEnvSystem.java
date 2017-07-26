package net.mgsx.game.examples.openworld.systems;

import java.util.Calendar;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;

@Storable(value="ow.env")
@EditableSystem
public class OpenWorldEnvSystem extends EntitySystem
{
	@Inject OpenWorldManagerSystem manager;
	
	@Editable public Color fogColor = new Color(1.0f, 0.8f, 0.7f, 1.0f);
	
	@Editable(type=EnumType.UNIT, realtime=true) public Vector3 sunDirection = new Vector3(.5f, -1f, .5f).nor();
	
	@Editable public float waterLevelRate = .3f;
	public transient float waterLevel;

	@Editable(realtime=true) public float timeOfDay;
	@Editable(realtime=true) public float temperature;
	
	@Editable public Color nightColor = new Color(.1f, .1f, .15f, 1f);
	@Editable public Color dayColor = new Color(.95f, 1.3f, 1.3f, 1f);
	@Editable public Color sunsetColor = new Color(.9f, .85f, .70f, 1f);
	@Editable public Color sunriseColor = new Color(.1f, .1f, .1f, 1f);
		
	@Editable public boolean realtime = true;
	@Inject public WeatherSystem weather;
	@Editable public boolean autoSun, autoTime;

	@Editable public float time;
	
	public OpenWorldEnvSystem() {
		super(GamePipeline.LOGIC);
	}
	
	@Override
	public void update(float deltaTime) {
		time += deltaTime;
		if(autoTime){
			Calendar cal = Calendar.getInstance();
			timeOfDay = cal.get(Calendar.HOUR_OF_DAY);
		}
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
		
		waterLevel = manager.scale * waterLevelRate;
	}
	
}
