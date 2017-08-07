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
		for(FreemindNode item : OpenWorldModel.items()){
			FreemindNode spawnDef;
			if(layer == LAYER_ITEM) spawnDef = item.child("spawnable");
			else spawnDef = item.child("moving");
			if(spawnDef.exists()){
				SpawnCache cache = new SpawnCache();
				cache.def = item;
				
				// clustering
				FreemindNode cluster = spawnDef.child("cluster");
				cache.clusterMin = cluster.first().asInt(1);
				cache.clusterMax = cluster.child(1).asInt(cache.clusterMin);
				
				// chance
				cache.chance = spawnDef.child("chance").first().asInt(100);
				
				cache.waterFactor = spawnDef.child("water").first().asInt(100) / 100f;
				
				items.add(cache);
			}
		}
	}
	
	public void generate(Array<OpenWorldElement> result, float x, float y) 
	{
		// update ranking based on context (location and environnement)
		float altitude = generatorSystem.getAltitude(x, y);
		boolean aquatic = altitude < envSystem.waterLevel;
		
		// make available list and compute ranking
		available.clear();
		int rankMax = 0;
		for(SpawnCache cache : items)
		{
			float chance = cache.chance;
			
			// TODO apply other constraints
			if(aquatic) chance *= cache.waterFactor;
			
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
			result.add(element);
		}
	}
}
