package net.mgsx.game.examples.war.model;

import com.badlogic.gdx.utils.Array;

public class History {

	public Array<Integer> history = new Array<Integer>();
	public Integer min, max, size;
	
	
	public History() {
		this(null);
	}
	public History(Integer size) {
		this.size = size;
	}
	
	public void add(Integer value){
		history.add(value);
		if(min == null || min>value) min=value;
		if(max == null || max<value) max=value;
		if(size != null && history.size > size) history.removeIndex(0);
	}
}
