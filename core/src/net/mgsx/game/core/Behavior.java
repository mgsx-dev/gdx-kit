package net.mgsx.game.core;

// TODO replaced by Entity Component : code specific AI in specific plugin : just require
// entity to have several components, for instance :
// PlayerBehavior needs Box2DModel (a body) so plugin could have a method :
// isAssignable(Entity), in this case we return Family.one(Box2DModel.class)
//
public interface Behavior {

	public void act();
}
