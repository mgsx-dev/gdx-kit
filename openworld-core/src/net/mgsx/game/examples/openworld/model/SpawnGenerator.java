package net.mgsx.game.examples.openworld.model;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.examples.openworld.systems.OpenWorldEnvSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldGeneratorSystem;
import net.mgsx.game.examples.openworld.systems.WeatherSystem;
import net.mgsx.game.examples.openworld.utils.FreeMindReader.FreemindNode;

public class SpawnGenerator {
	
	public static final int LAYER_ITEM = 0;
	public static final int LAYER_ANIMAL = 1;
	
	private static class SpawnCache
	{
		FreemindNode def;
		int clusterMin, clusterMax;
		int rank;
		int chance;
		float waterFactor;
		float landFactor;
		boolean landAbility;
		boolean waterAbility;
		boolean airAbility;
	}
	
	private Array<SpawnCache> items = new Array<SpawnCache>();
	private Array<SpawnCache> available = new Array<SpawnCache>();
	
	private OpenWorldGeneratorSystem generatorSystem;
	private OpenWorldEnvSystem envSystem;
	private WeatherSystem weatherSystem;
	
	public SpawnGenerator(int layer, Engine engine) 
	{
		generatorSystem = engine.getSystem(OpenWorldGeneratorSystem.class);
		envSystem = engine.getSystem(OpenWorldEnvSystem.class);
		weatherSystem = engine.getSystem(WeatherSystem.class);
		
		// collect all spawnable items and build a quick access map for chance calculation.
		for(FreemindNode item : OpenWorldModel.items())
		{
			// keep only spawnable
			FreemindNode spawnDef = item.child("spawnable");
			if(!spawnDef.exists()) continue;
			
			// match layer
			FreemindNode movingDef = item.child("moving");
			if(layer == LAYER_ANIMAL){
				if(!movingDef.exists()) continue;
			}else{
				if(movingDef.exists()) continue;
			}
			
			SpawnCache cache = new SpawnCache();
			cache.def = item;
			
			// clustering
			FreemindNode cluster = spawnDef.child("cluster");
			cache.clusterMin = cluster.first().asInt(1);
			cache.clusterMax = cluster.child(1).asInt(cache.clusterMin);
			
			// chance
			cache.chance = spawnDef.child("chance").first().asInt(100);
			
			cache.waterFactor = spawnDef.child("water").first().asInt(100) / 100f;
			cache.landFactor = spawnDef.child("land").first().asInt(100) / 100f;
			
			// modify chance according to moving ability
			if(movingDef.exists()){
				cache.landAbility = movingDef.child("land").exists();
				cache.waterAbility = movingDef.child("water").exists();
				cache.airAbility = movingDef.child("air").exists();
				
				// reduce spawn chance factor for some missing ability.
				// air ability allow to be spawn in any environment.
				if(!cache.landAbility && !cache.airAbility) cache.landFactor = 0;
				if(!cache.waterAbility && !cache.airAbility) cache.waterFactor = 0;
			}
			
			items.add(cache);
		}
	}
	
	public void generate(Array<OpenWorldElement> result, float x, float y) 
	{
		// update ranking based on context (location and environnement)
		float altitude = generatorSystem.getAltitude(x, y);
		
		// determinate environment : with offset
		boolean aquatic = altitude < envSystem.waterLevel;
		
		boolean deepWater = altitude < envSystem.waterLevel - 4f;
		boolean highLands = altitude > envSystem.waterLevel + 1f;
		
		// make available list and compute ranking
		available.clear();
		int rankMax = 0;
		for(SpawnCache cache : items)
		{
			float chance = cache.chance;
			
			// TODO it works for items because they have no ability so no filters here ...
			if(cache.waterAbility && !cache.airAbility && !deepWater) continue;
			if(cache.landAbility && !cache.airAbility && !highLands) continue;
			
			// TODO apply other constraints
			if(aquatic) chance *= cache.waterFactor;
			else chance *= cache.landFactor;
			
			int percent = (int)chance;
			if(percent > 0){
				
				cache.rank = rankMax;
				rankMax += percent;
				
				available.add(cache);
			}
		}
		
		
		if(available.size == 0) return;
		
		// Find by chance
		int rank = MathUtils.random(rankMax);
		int left = 0;
		int right = available.size;
		SpawnCache item = null;
		int index;
		for(;;){
			index = (left + right)/2;
			if(right - left <= 1){
				item = available.get(index);
				break;
			}
			SpawnCache cache = available.get(index);
			if(rank < cache.rank){
				right = index;
			}else{
				left = index;
			}
		}
		
		// clustering
		int count = MathUtils.random(item.clusterMin, item.clusterMax);
		for(int i=0 ; i<count ; i++){
			OpenWorldElement element = OpenWorldModel.generateNewElement(item.def);
			element.landAbility = item.landAbility;
			element.airAbility = item.airAbility;
			element.waterAbility = item.waterAbility;
			result.add(element);
		}
	}
}
