package net.mgsx.game.core.helpers;

import com.badlogic.ashley.core.Component;

public class FamilyHelper {

	public static FamilyBuilder all(Class<? extends Component> ...types){
		return new FamilyBuilder().all(types);
	}
	public static FamilyBuilder one(Class<? extends Component> ...types){
		return new FamilyBuilder().one(types);
	}
	public static FamilyBuilder exclude(Class<? extends Component> ...types){
		return new FamilyBuilder().exclude(types);
	}
}
