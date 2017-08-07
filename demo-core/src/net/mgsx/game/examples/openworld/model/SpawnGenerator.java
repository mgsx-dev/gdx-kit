package net.mgsx.game.examples.openworld.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.examples.openworld.utils.FreeMindReader.FreemindNode;

public class SpawnGenerator {
	
	public static final int LAYER_ITEM = 0;
	public static final int LAYER_ANIMAL = 1;
	
	private static class SpawnCache
	{
		FreemindNode def;
		int clusterMin, clusterMax;
		int rank;
	}
	
	private Array<SpawnCache> items = new Array<SpawnCache>();
	
	private int rankMax;
	
	public SpawnGenerator(int layer) 
	{
		// collect all spawnable items and build a quick access map for chance calculation.
		rankMax = 0;
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
				int percent = spawnDef.child("chance").first().asInt(100);
				cache.rank = rankMax;
				rankMax += percent;
				
				items.add(cache);
			}
		}
		
		
	}

	public void generate(Array<OpenWorldElement> result) 
	{
		if(items.size == 0) return;
		
		// Find by chance
		int rank = MathUtils.random(rankMax);
		int left = 0;
		int right = items.size;
		SpawnCache item = null;
		int index;
		for(;;){
			index = (left + right)/2;
			if(right - left <= 1){
				item = items.get(index);
				break;
			}
			SpawnCache cache = items.get(index);
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
