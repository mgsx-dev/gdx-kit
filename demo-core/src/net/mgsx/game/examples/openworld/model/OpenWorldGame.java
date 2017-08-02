package net.mgsx.game.examples.openworld.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;

import net.mgsx.game.examples.openworld.systems.OpenWorldCameraSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldGeneratorSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldManagerSystem;
import net.mgsx.game.examples.openworld.systems.UserObjectSystem;

public class OpenWorldGame {

	public static void load(Engine engine, InputStream stream){
		Json json = new Json();
		
		OpenWorldGame gameData = json.fromJson(OpenWorldGame.class, stream);
		
		engine.getSystem(OpenWorldCameraSystem.class).getCamera().position.set(gameData.position);
		
		OpenWorldGeneratorSystem generator = engine.getSystem(OpenWorldGeneratorSystem.class);
		generator.seed = gameData.seed;
		generator.reset();
		
		OpenWorldManagerSystem manager = engine.getSystem(OpenWorldManagerSystem.class);
		manager.clear();
		
		UserObjectSystem uos = engine.getSystem(UserObjectSystem.class);
		uos.removeAllElements();
		if(gameData.objects != null){
			for(OpenWorldElement element : gameData.objects){
				OpenWorldModel.generateElement(element);
				uos.appendObject(element);
			}
		}
	}
	
	public static InputStream save(Engine engine){
		
		OpenWorldGame gameData = new OpenWorldGame();
		
		// store camera position and seed
		gameData.position.set(engine.getSystem(OpenWorldCameraSystem.class).getCamera().position);
		gameData.seed = engine.getSystem(OpenWorldGeneratorSystem.class).seed;
		
		// store objects
		UserObjectSystem uos = engine.getSystem(UserObjectSystem.class);
		gameData.objects = new OpenWorldElement[uos.allUserObjects.size];
		for(int i=0 ; i<gameData.objects.length ; i++){
			gameData.objects[i] = uos.allUserObjects.get(i).element;
		}
		
		// serialize
		Json json = new Json();
		StringWriter writer = new StringWriter();
		json.toJson(gameData, writer);
		
		return new ByteArrayInputStream(writer.toString().getBytes());
	}
	
	public long seed = 0;
	public Vector3 position = new Vector3();
	public OpenWorldElement [] objects;
}
