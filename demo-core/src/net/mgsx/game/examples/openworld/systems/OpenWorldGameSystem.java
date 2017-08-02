package net.mgsx.game.examples.openworld.systems;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.PostInitializationListener;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.examples.openworld.model.OpenWorldGame;
import net.mgsx.game.examples.openworld.model.OpenWorldModel;
import net.mgsx.game.services.gapi.GAPI;
import net.mgsx.game.services.gapi.SavedGame;

/**
 * Saving is done synchronously while loading is done on this system update in order to
 * properly load other systems and avoid glitches.
 * 
 * TODO add threading support and callback.
 * 
 * @author mgsx
 *
 */
public class OpenWorldGameSystem extends EntitySystem implements PostInitializationListener
{
	@Inject OpenWorldCameraSystem cameraSystem;
	@Inject OpenWorldGeneratorSystem generator;
	@Inject OpenWorldManagerSystem manager;
	@Inject UserObjectSystem userObjectSystem;
	@Inject OpenWorldHUDSystem hudSystem;
	
	public Array<OpenWorldElement> backpack = new Array<OpenWorldElement>();
	
	private SavedGame gameToLoad;
	
	public OpenWorldGameSystem() {
		super(GamePipeline.FIRST);
	}
	
	@Override
	public void onPostInitialization() {
		// XXX init workaround
		if(backpack.size == 0){
			backpack.add(OpenWorldModel.generateNewElement("machete"));
		}
	}
	
	private void load(SavedGame game)
	{
		InputStream stream = GAPI.service.loadGame(game);
		
		Json json = new Json();
		
		OpenWorldGame gameData = json.fromJson(OpenWorldGame.class, stream);
		
		cameraSystem.getCamera().position.set(gameData.position);
		
		generator.seed = gameData.seed;
		generator.reset();
		
		manager.clear();
		
		userObjectSystem.removeAllElements();
		if(gameData.objects != null){
			for(OpenWorldElement element : gameData.objects){
				OpenWorldModel.generateElement(element);
				userObjectSystem.appendObject(element);
			}
		}
		
		backpack.clear();
		if(gameData.backpack != null){
			for(OpenWorldElement element : gameData.backpack){
				OpenWorldModel.generateElement(element);
				backpack.add(element);
			}
		}
		
		// finally invalidate GUI
		hudSystem.hudMain.resetState();
	}
	
	public void save(SavedGame game){
		
		OpenWorldGame gameData = new OpenWorldGame();
		
		// store camera position and seed
		gameData.position.set(cameraSystem.getCamera().position);
		gameData.seed = generator.seed;
		
		// store objects
		gameData.objects = new OpenWorldElement[userObjectSystem.allUserObjects.size];
		for(int i=0 ; i<gameData.objects.length ; i++){
			gameData.objects[i] = userObjectSystem.allUserObjects.get(i).element;
		}
		
		gameData.backpack = new OpenWorldElement[backpack.size];
		for(int i=0 ; i<gameData.backpack.length ; i++){
			gameData.backpack[i] = backpack.get(i);
		}
		
		// serialize
		Json json = new Json();
		StringWriter writer = new StringWriter();
		json.toJson(gameData, writer);
		
		InputStream data = new ByteArrayInputStream(writer.toString().getBytes());
		
		GAPI.service.saveGame(game, data);
	}

	public void loadRequest(SavedGame game) {
		gameToLoad = game;
	}
	
	@Override
	public void update(float deltaTime) 
	{
		if(gameToLoad != null){
			load(gameToLoad);
			gameToLoad = null;
		}else{
			
			// update player logic here ?
			
		}
	}

	
}
