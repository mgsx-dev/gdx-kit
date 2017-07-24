package net.mgsx.game.examples.openworld.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Sort;

public class Compound
{
	private ObjectMap<String, Integer> map = new ObjectMap<String, Integer>();
	private String key = null;
	
	public void add(String component, int quantity){
		key = null;
		if(map.containsKey(component)){
			map.put(component, map.get(component) + quantity);
		}else{
			map.put(component, quantity);
		}
	}
	
	public void add(String component) {
		add(component, 1);
	}
	
	private String getKey(){
		if(key == null)
		{
			Array<String> keys = map.keys().toArray();
			Sort.instance().sort(keys);
			String result = "";
			for(String k : keys){
				if(result != null){
					result += "|";
				}
				result += k + "#" + map.get(k);
			}
			key = result;
		}
		return key;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof Compound && getKey().equals(((Compound) obj).getKey());
	}

	@Override
	public int hashCode() {
		return getKey().hashCode();
	}
	
	
}
