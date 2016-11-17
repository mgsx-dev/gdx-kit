package net.mgsx.game.core.helpers;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;

public class FamilyBuilder
{
	private Array<Class<? extends Component>> all = new Array<Class<? extends Component>>(Class.class);
	private Array<Class<? extends Component>> one = new Array<Class<? extends Component>>(Class.class);
	private Array<Class<? extends Component>> exclude = new Array<Class<? extends Component>>(Class.class);
	
	public FamilyBuilder() {
		super();
	}
	public FamilyBuilder all(Class<? extends Component>... componentTypes){
		for(Class<? extends Component> componentType : componentTypes) all.add(componentType);
		return this;
	}
	public FamilyBuilder one(Class<? extends Component>... componentTypes){
		one.addAll(componentTypes);
		return this;
	}
	public FamilyBuilder exclude(Class<? extends Component>... componentTypes){
		exclude.addAll(componentTypes);
		return this;
	}
	public FamilyBuilder reset(){
		all.clear();
		one.clear();
		exclude.clear();
		return this;
	}
	public Family get(){
		return Family.all(all.toArray()).one(one.toArray()).exclude(exclude.toArray()).get();
	}
}