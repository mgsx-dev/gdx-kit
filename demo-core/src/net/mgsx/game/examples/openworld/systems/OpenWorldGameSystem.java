package net.mgsx.game.examples.openworld.systems;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.PostInitializationListener;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.openworld.model.GameAction;
import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.examples.openworld.model.OpenWorldGame;
import net.mgsx.game.examples.openworld.model.OpenWorldGameEventListener;
import net.mgsx.game.examples.openworld.model.OpenWorldGameEventManager;
import net.mgsx.game.examples.openworld.model.OpenWorldModel;
import net.mgsx.game.examples.openworld.model.OpenWorldPlayer;
import net.mgsx.game.examples.openworld.model.OpenWorldQuestModel;
import net.mgsx.game.examples.openworld.model.OpenWorldQuestModel.Requirement;
import net.mgsx.game.services.gapi.GAPI;
import net.mgsx.game.services.gapi.SavedGame;

/**
 * Saving is done synchronously while loading is done on this system update in order to
 * properly load other systems and avoid glitches.
 * 
 * TODO add threading support and callback.
 * 
 * some quests are realtime based (distance, time ...) and is checked at each frames.
 * Other quests are event based and don't have to be checked every time, just
 * when player performs some actions or when world spawn something.
 * 
 * @author mgsx
 *
 */
@EditableSystem
public class OpenWorldGameSystem extends EntitySystem implements PostInitializationListener, OpenWorldGameEventManager
{
	@Inject OpenWorldSpawnSystem spawnSystem;
	@Inject OpenWorldCameraSystem cameraSystem;
	@Inject OpenWorldGeneratorSystem generator;
	@Inject OpenWorldManagerSystem manager;
	@Inject UserObjectSystem userObjectSystem;
	@Inject OpenWorldHUDSystem hudSystem;
	@Inject OpenWorldEnvSystem env;
	
	@Editable(realtime=true, readonly=true)
	public transient boolean diving, walking, flying, swimming;
	
	@Editable public boolean playerLogic = true;
	@Editable public transient boolean eventsLogic = false;
	
	public Array<OpenWorldElement> backpack = new Array<OpenWorldElement>();
	
	private SavedGame gameToLoad;
	
	private ObjectSet<OpenWorldGameEventListener> gameEventListeners = new ObjectSet<OpenWorldGameEventListener>();
	
	// TODO move player logic to dedicated system ? only keep game loading/saving logic here.
	// things need environement system to be computed first (specially at loading time)
	// , then player logic can apply, then HUD can display correct values.
	public OpenWorldPlayer player;
	
	public OpenWorldGameSystem() {
		super(GamePipeline.FIRST);
	}
	
	@Override
	public void onPostInitialization() {
		player = new OpenWorldPlayer();
		
		computePlayerStats();
		
		// compute inital values
		player.energy = player.energyMax;
		player.life = player.lifeMax;
		player.oxygen = player.oxygenMax;
		
		// TODO some values should be updated in realtime
	}
	
	@Override
	public void addGameEventListener(OpenWorldGameEventListener listener) {
		gameEventListeners.add(listener);
	}
	@Override
	public void removeGameEventListener(OpenWorldGameEventListener listener) {
		gameEventListeners.remove(listener);
	}
	
	/**
	 * compute level based on XP (see {@link #experience(int)}
	 * @param experience
	 * @return
	 */
	public static int level(long experience){
		return (int)Math.sqrt(experience/100 + 0.5) + 1;
	}
	/**
	 * compute XP based on level.
	 * Common gaming is to use square to have linear delta between level.
	 * Values are :
	 * LV1 :    0 XP
	 * LV2 :  100 XP (+100)
	 * LV3 :  400 XP (+300) (++200)
	 * LV4 :  900 XP (+500) (++200)
	 * LV5 : 1600 XP (+700) (++200)
	 * LV6 : 2500 XP (+900) (++200)
	 * ...
	 * @param level
	 * @return
	 */
	public static long experience(int level){
		long base = (long)level - 1;
		return 100L * base * base;
	}
	
	private void computePlayerStats() 
	{
		// first compute level based on experience
		player.level = level(player.experience);
		
		// TODO maybe adjust base to have something realistic ? (oxygen ...)
		// derived max values from level
		player.energyMax = player.level + 3;
		player.lifeMax = player.level + 3;
		player.oxygenMax = player.level + 3;
		
		// init basics values
		// these values are based not based on scientific stuff and limits depends
		// on exposure time, air/water ...etc. But we could consider that
		// player loose 1 life point every hour :
		// * per degree below 10°
		// * per degree above 50°
		player.temperatureMax = 50;
		player.temperatureMin = 10;
		
		// TODO modify basic values based on equipment (iterate from backpack).
		
		// XXX reset stats
		player.life = player.lifeMax;
		player.energy = player.energyMax;
		player.oxygen = player.oxygenMax;
		player.temperature = 37.2;
		
		// compute quests based on achievements (already completed quests)
		// and quests revealed from model
		ObjectSet<String> unlocked = new ObjectSet<String>();
		Array<String> toProcess = new Array<String>();
		for(String qid : player.achievements){
			toProcess.add(qid);
		}
		
		while(toProcess.size > 0){
			String qid = toProcess.pop();
			if(unlocked.add(qid)){
				OpenWorldQuestModel quest = OpenWorldModel.quest(qid);
				for(String reveal : quest.unlocking()){
					if(!unlocked.contains(reveal)){
						if(isUnlocked(reveal)){
							unlocked.add(reveal);
						}else{
							player.pendingQuests.add(reveal);
						}
					}
				}
				for(String itemId : quest.knowledges()){
					player.knownSecrets.add(itemId);
				}
			}
		}
		
		// update completed since model may have changed between last save (migration)
		for(String id : unlocked) player.completedQuests.add(id);
		
		// load history
		for(String code : player.history){
			String [] codes = code.split("\\|");
			ObjectMap<String, Integer> act = player.actions.get(codes[0]);
			if(act == null) player.actions.put(codes[0], act = new ObjectMap<String, Integer>());
			act.put(codes[1], Integer.valueOf(codes[2]));
		}
	}

	private boolean isUnlocked(String qid) 
	{
		Array<Requirement> requirements = OpenWorldModel.quest(qid).requirements();
		for(Requirement requirement : requirements){
			ObjectMap<String, Integer> act = player.actions.get(requirement.action);
			if(act == null) return false;
			Integer count = act.get(requirement.type);
			if(count == null) return false;
			if(count < requirement.count) return false;
		}
		return true;
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
		spawnSystem.clear();
		
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
		
		player = gameData.player;
		if(player == null){
			player = new OpenWorldPlayer();
		}
		computePlayerStats();
		
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
		
		gameData.player = player;
		
		gameData.player.achievements = new String[player.completedQuests.size];
		for(int i=0 ; i<gameData.player.achievements.length ; i++){
			gameData.player.achievements[i] = player.completedQuests.get(i);
		}
		
		Array<String> history = new Array<String>(String.class);
		for(Entry<String, ObjectMap<String, Integer>> act : player.actions){
			for(Entry<String, Integer> type : act.value){
				history.add(act.key + "|" + type.key + "|" + type.value);
			}
		}
		gameData.player.history = history.toArray();
		
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
			
			if(!playerLogic) return;
			
			Camera camera = cameraSystem.getCamera();
			if(camera == null) return;
			
			// offset from ground to camera is the player's tall
			final float footsToEyeHeight = 1.7f;
			
			// head offset when swimming (limit : cannot fly up to this value above sea)
			float headOffset = .2f;
			
			// TODO replace by enum : states are mutually exclusive !
			// flying is just a case we'll see later (falling or planning or bird mode ...)
			// we reset all status
			flying = false;
			walking = false;
			diving = false;
			swimming = false;
			
			// this is the ground level based on procedural lands
			float altitude = generator.getAltitude(camera.position.x, camera.position.z);
			
			
			// walking status is when ground below the sea is no more than player tall
			float waterDepth = env.waterLevel - altitude;
			
			if(waterDepth < footsToEyeHeight){
				walking = true;
			}
			else
			{
				// diving status is easy, it is when camera is below water level.
				if(camera.position.y < env.waterLevel){
					diving = true;
				}
				// all other cases, the player is swimming
				else
				{
					swimming = true;
				}
			}
			
			
			// enable flying mode in case of diving or swimming only
			if(diving || swimming){
				cameraSystem.flyingMode = true;
				
				// in this case, we limit to palyer's head above the sea.
				float altitudeMax = env.waterLevel + headOffset;
				if(camera.position.y > altitudeMax){
					camera.position.y = altitudeMax;
				}
				
				// in this case, camera offset in small, player is not vertical.
				// to avoid camera clipping we offset to 1 meter;
				cameraSystem.offset = 1.5f; // TODO maybe less depends on near camera clipping ...
				
			}
			else{
				cameraSystem.flyingMode = false;
				
				// in this case, camera offset is player tall
				cameraSystem.offset = footsToEyeHeight;
			}
			
			
			// update player logic
			float currentMove = cameraSystem.currentMove;
			
			// TODO use scentific values : nb km per hour, ...Etc.
			
			// in water then update oxygen
			if(diving){
				player.oxygen -= deltaTime * 0.5;
				if(player.oxygen < 0){
					player.oxygen = 0;
					player.life -= deltaTime * 1;
				}
			}else{
				player.oxygen += deltaTime * 2;
				if(player.oxygen > player.oxygenMax){
					player.oxygen = player.oxygenMax;
				}
			}
			
			// update temperature from environement and time ...
			player.temperature += (env.temperature - player.temperature) * deltaTime * 0.05;
			
			if(player.temperature < player.temperatureMin){
				player.energy -= deltaTime * 0.1;
			}
			if(player.temperature > player.temperatureMax){
				player.energy -= deltaTime * 0.1;
			}
			
			// lose more energy by moving
			if(walking){
				player.energy -= currentMove * 0.001;
			}else if(swimming || diving){
				player.energy -= currentMove * 0.01;
			}else if(flying){
				player.energy -= currentMove * 0.005;
			}else{
				// loose energy in all cases
				player.energy -= deltaTime * 0.0000001;
			}
			
			if(player.energy < 0){
				player.energy = 0;
				player.life -= deltaTime * 0.01;
			}else if(player.energy / player.energyMax > 0.9){ // recuperation at 90% since energy can't get at 100%
				player.life += deltaTime * 0.01;
				if(player.life > player.lifeMax) player.life = player.lifeMax;
			}
			
			if(player.life < 0){
				player.life = 0;
				// TODO death sequence ...
			}
			
			// increment stats
			if(flying) player.distanceFly += currentMove;
			if(swimming || diving) player.distanceSwim += currentMove;
			if(walking) player.distanceWalk += currentMove;
			
			player.timePlay += deltaTime;
			
			if(!eventsLogic) return;
			
			// case of game beginning : reveal the first quest and drop a machete
			// just in front of player.
			if(player.completedQuests.size == 0 && player.pendingQuests.size == 0){
				revealQuest("intro");
				
				OpenWorldElement item = OpenWorldModel.generateNewElement("machete");
				item.position.set(camera.position).mulAdd(camera.direction, 2); // 2m
				item.rotation.idt();
				userObjectSystem.appendObject(item);
			}
			
			// TODO check some realtime achievements here.
			
		}
	}
	
	@Override
	public void actionReport(GameAction action, String type) {
		String actionName;
		switch(action){
		case CRAFT:
			actionName = "build";
			break;
		case DESTROY:
			actionName = "destroy";
			break;
		case DROP:
			actionName = "drop";
			break;
		case EAT:
			actionName = "eat";
			break;
		case GRAB:
			actionName = "grab";
			break;
		case LOOK:
			actionName = "look";
			break;
		case SLEEP:
			actionName = "sleep";
			break;
		case USE:
			actionName = "use";
			break;
		default:
			actionName = "unknown";
			break;
		
		}
		
		if(action == GameAction.SLEEP){
			player.timeSleep += 8; // TODO 8 is defined in HUD should be handled differently
		}
		
		increment(actionName, type, 1);
		checkQuestsStatus();
	}
	
	private void increment(String action, String type, int count){
		ObjectMap<String, Integer> act = player.actions.get(action);
		if(act == null) player.actions.put(action, act = new ObjectMap<String, Integer>());
		Integer counter = act.get(type);
		if(counter == null) counter = 0;
		counter += count;
		act.put(type, counter);
	}

	private void revealQuest(String qid) {
		player.pendingQuests.add(qid);
		for(OpenWorldGameEventListener listener : gameEventListeners)
			listener.onQuestRevealed(qid);
		
		checkQuestsStatus();
	}

	private void checkQuestsStatus() 
	{
		// Iterate for removal
		for(int i=0 ; i<player.pendingQuests.size ; ){
			String qid = player.pendingQuests.get(i);
			if(isUnlocked(qid)){
				player.pendingQuests.removeIndex(i);
				unlockQuest(qid);
				continue;
			}
			i++;
		}
	}

	private void unlockQuest(String qid) {
		player.completedQuests.add(qid);
		
		for(OpenWorldGameEventListener listener : gameEventListeners)
			listener.onQuestUnlocked(qid);
		
		OpenWorldQuestModel quest = OpenWorldModel.quest(qid);
		for(String itemId : quest.knowledges()){
			if(player.knownSecrets.add(itemId)){
				for(OpenWorldGameEventListener listener : gameEventListeners)
					listener.onSecretUnlocked(itemId);
			}
		}
		
		for(String newQid : quest.unlocking()){
			revealQuest(newQid);
		}
	}
	
	@Override
	public void questAck(String qid) {
		player.acknowledgedQuests.add(qid);
		checkQuestsStatus();
	}

	public int getAvailableSpaceInBackpack() 
	{
		return getTotalSpaceInBackpack() - getUsedSpaceInBackpack();
	}

	public int getUsedSpaceInBackpack() {
		int sum = 0;
		for(OpenWorldElement item : backpack){
			sum += OpenWorldModel.space(item.type);
		}
		return sum;
	}

	public int getTotalSpaceInBackpack() {
		// TODO this value could be upgraded in some way ...
		return 16;
	}

	
}
