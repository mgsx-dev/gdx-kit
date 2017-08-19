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
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.annotations.Inject;

@EditableSystem
public class WeatherSystem extends EntitySystem
{
	@Inject OpenWorldSkySystem sky;

	@Editable(type=EnumType.UNIT, realtime=true) 
	public float cloudRate = 0.5f;
	
	/** km/h */
	@Editable(type=EnumType.UNIT, realtime=true)
	public float windSpeed = 20;
	
	/** 0 north, 90 east */
	@Editable(realtime=true) 
	public float windAngle = 0;
	
	@Editable(type=EnumType.UNIT, realtime=true)
	public float rainRate = 0;
	
	/** in ° Celsius */
	@Editable(realtime=true)
	public float temperature = 20;
	
	@Editable(type=EnumType.UNIT, realtime=true)
	public float humidity = 0;
	
	
	
	private boolean enabled = false;
	private long pollingMS = 1000 * 60 * 10;
	private String openweathermapAkiKey = null;
	private float geoLon, geoLat;
	private boolean enableLogging;
	
	private volatile boolean weatherDirty;
	private volatile boolean forecastDirty;
	
	private JsonValue lastWeather, lastForeCast;
	
	@Editable(realtime=true) public float sunrise, sunset;
	
	private long nextFetchTimeMS;
	
	float time = -1;
	
	public WeatherSystem() {
		super(GamePipeline.INPUT);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		// try to load configuration file
		FileHandle config = Gdx.files.local("../local/openweathermap.properties");
		if(config.exists()){
			try {
				Properties props = new Properties();
				props.load(config.read());
				openweathermapAkiKey = props.get("openweathermap.api.key").toString();
				geoLon = Float.valueOf(props.get("geo.lon").toString());
				geoLat = Float.valueOf(props.get("geo.lat").toString());
				enabled = Boolean.parseBoolean(props.get("enabled").toString());
				enableLogging = Boolean.parseBoolean(props.get("log").toString());
				pollingMS = Integer.valueOf(props.get("pollingMinutes").toString()) * 60 * 1000;
			} catch (IOException e) {
				Gdx.app.error("OPENWEATHERMAP", "cannot load properties file", e);
			}
		}else{
			Gdx.app.log("NET", "cannot find api config in " + config.path() + " switch to default configuration (mock)");
		}
		
		if(!enabled){
			lastWeather = new JsonReader().parse(Gdx.files.classpath("openweathermap-weather.json").read());
			lastForeCast = new JsonReader().parse(Gdx.files.classpath("openweathermap-forecast.json").read());
			weatherDirty = true;
			forecastDirty = true;
		}
		nextFetchTimeMS = System.currentTimeMillis();
	}
	
	private void processData() {
		// TODO who update who : env/sky/wind get from weather and weather can be manually configurable ?
		
		try{
			cloudRate = lastWeather.get("clouds").getFloat("all", 0) / 100f;
			
			// convert m/s to km/h
			windSpeed = lastWeather.get("wind").getFloat("speed", 0) * 60f / 1000f;
			
			// 0 for north, 90 for east
			windAngle = lastWeather.get("wind").getFloat("deg", 0);
			
			// From this source, mm/m²/s can be mapped from 0 to 8
			// http://pluiesextremes.meteo.fr/france-metropole/Intensite-de-precipitations.html
			rainRate = lastWeather.has("rain") ? (lastWeather.get("rain").getFloat("3h", 0) / 3f) / 8f : 0;
			
			temperature = lastWeather.get("main").getFloat("temp", 0);
			
			humidity = lastWeather.get("main").getFloat("humidity", 0);
			
			long sunrise = lastWeather.get("sys").getLong("sunrise");
			long sunset = lastWeather.get("sys").getLong("sunset");
			
			Calendar cal = Calendar.getInstance();
			
			cal.setTime(new Date(sunrise * 1000));
			this.sunrise = cal.get(Calendar.HOUR_OF_DAY) / 24f;
			
			cal.setTime(new Date(sunset * 1000));
			this.sunset = cal.get(Calendar.HOUR_OF_DAY) / 24f;
			
			// TODO cache forecast ... (40 results 1 every 3 hours => 5 days)
			for(JsonValue forecast : lastForeCast.get("list")){
				long dt = forecast.get("dt").asLong();
				cal.setTime(new Date(dt * 1000));
//				System.out.println(forecast.get("dt_txt").asString());
//				System.out.println(cal.get(Calendar.HOUR_OF_DAY));
			}
		}catch(Exception e){
			Gdx.app.error("WEATHER", "???", e);
		}
	}
	
	@Editable
	public void fetchWeather(){
		HttpRequest r = new HttpRequest(HttpMethods.GET);
		r.setUrl("http://api.openweathermap.org/data/2.5/weather?lat=" + geoLat + "&lon=" + geoLon + "&units=metric&appid=" + openweathermapAkiKey);
		Gdx.net.sendHttpRequest(r , new HttpResponseListener() {
			
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				lastWeather = new JsonReader().parse(httpResponse.getResultAsStream());
				if(enableLogging){
					FileHandle log = Gdx.files.local("../local/logs/openweathermap-weather-" + System.currentTimeMillis() + ".json");
					log.writeString(lastWeather.toString(), false);
					Gdx.app.log("NET", "data logged to " + log.path());
				}else{
					Gdx.app.log("NET", "weather fetched");
				}
				weatherDirty = true;
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
	
	@Editable
	public void fetchForecast(){
		HttpRequest r = new HttpRequest(HttpMethods.GET);
		r.setUrl("http://api.openweathermap.org/data/2.5/forecast?lat=" + geoLat + "&lon=" + geoLon + "&units=metric&appid=" + openweathermapAkiKey);
		Gdx.net.sendHttpRequest(r , new HttpResponseListener() {
			
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				lastForeCast = new JsonReader().parse(httpResponse.getResultAsStream());
				if(enableLogging){
					FileHandle log = Gdx.files.local("../local/logs/openweathermap-forecast-" + System.currentTimeMillis() + ".json");
					log.writeString(lastForeCast.toString(), false);
					Gdx.app.log("NET", "data logged to " + log.path());
				}else{
					Gdx.app.log("NET", "forecast fetched");
				}
				forecastDirty = true;
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
	
	@Override
	public void update(float deltaTime) {
		if(weatherDirty || forecastDirty){
			processData();
			weatherDirty = false;
			forecastDirty = false;
		}
		if(enabled){
			if(nextFetchTimeMS < System.currentTimeMillis()){
				fetchWeather();
				nextFetchTimeMS = System.currentTimeMillis() + pollingMS;
			}
		}
	}
	
}
