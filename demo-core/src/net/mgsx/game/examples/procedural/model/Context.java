package net.mgsx.game.examples.procedural.model;

public interface Context<T extends Context<T>> {

	public void clone(T clone);
}
