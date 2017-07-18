package net.mgsx.game.examples.openworld.systems;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;

@EditableSystem
public class WeatherSystem extends EntitySystem
{
	@Editable public boolean mock = true;
	
	private JsonValue lastWeather, lastForeCast;
	private int jobsRemains = 0;
	private String openweathermapAkiKey;
	
	@Editable public float sunrise, sunset;
	
	public static class OpenWeatherMapData{
		
	}
	
	float time = -1;
	public WeatherSystem() {
		super(GamePipeline.BEFORE_LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		// injection bug workaround here
		sky = getEngine().getSystem(OpenWorldSkySystem.class);
		if(mock){
			lastWeather = new JsonReader().parse(Gdx.files.classpath("openweathermap-weather.json").read());
			lastForeCast = new JsonReader().parse(Gdx.files.classpath("openweathermap-forecast.json").read());
			processData();
		}else{
			// load API key
			FileHandle config = Gdx.files.internal("openweathermap.properties");
			if(config.exists()){
				try {
					Properties props = new Properties();
					props.load(config.read());
					openweathermapAkiKey = props.get("openweathermap.api.key").toString();
				} catch (IOException e) {
					Gdx.app.error("OPENWEATHERMAP", "cannot load properties file", e);
				}
			}
		}
	}
	
	
	@Inject public OpenWorldSkySystem sky;
	private void processData() {
		sky.cloudRate = 1f / (0.001f + (float)lastWeather.get("clouds").getDouble("all") / 100f); // In percents
		sky.cloudSpeed = lastWeather.get("wind").getFloat("speed") * .01f;
		sky.cloudDarkness = lastWeather.get("clouds").getFloat("all") + 10;
		sky.cloudDirection.set(Vector2.X).setAngle(lastWeather.get("wind").getFloat("deg"));
		long date = lastWeather.getLong("dt");
		long sunrise = lastWeather.get("sys").getLong("sunrise");
		long sunset = lastWeather.get("sys").getLong("sunset");
		
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(new Date(sunrise * 1000));
		this.sunrise = cal.get(Calendar.HOUR_OF_DAY) / 24f;
		
		cal.setTime(new Date(sunset * 1000));
		this.sunset = cal.get(Calendar.HOUR_OF_DAY) / 24f;
		
		cal.setTime(new Date(date * 1000));
		float fdate = cal.get(Calendar.HOUR_OF_DAY);
	}
	@Override
	public void update(float deltaTime) {
		if(mock) return;
		if(openweathermapAkiKey == null) return;
		if(jobsRemains == 0){
			jobsRemains = -1;
			
			processData();
			
		}
		if(time>0) time -= deltaTime;
		else{
			jobsRemains = 2;
			HttpRequest r = new HttpRequest(HttpMethods.GET);
			r.setUrl("http://api.openweathermap.org/data/2.5/weather?lat=47.291407&lon=-1.54949&appid=" + openweathermapAkiKey);
			Gdx.net.sendHttpRequest(r , new HttpResponseListener() {
				
				@Override
				public void handleHttpResponse(HttpResponse httpResponse) {
					lastWeather = new JsonReader().parse(httpResponse.getResultAsStream());
					Gdx.app.log("NET", "weather updated");
					jobsRemains--;
				}
				@Override
				public void failed(Throwable t) {
					Gdx.app.error("NET", "parsing", t);
				}
				@Override
				public void cancelled() {
				}
			});
			r = new HttpRequest(HttpMethods.GET);
			r.setUrl("http://api.openweathermap.org/data/2.5/forecast?lat=47.291407&lon=-1.54949&appid=" + openweathermapAkiKey);
			Gdx.net.sendHttpRequest(r , new HttpResponseListener() {
				
				@Override
				public void handleHttpResponse(HttpResponse httpResponse) {
					lastForeCast = new JsonReader().parse(httpResponse.getResultAsStream());
					Gdx.app.log("NET", "weather updated");
					jobsRemains--;
				}
				@Override
				public void failed(Throwable t) {
					Gdx.app.error("NET", "parsing", t);
				}
				@Override
				public void cancelled() {
				}
			});
			
		}
		super.update(deltaTime);
	}

	
}
