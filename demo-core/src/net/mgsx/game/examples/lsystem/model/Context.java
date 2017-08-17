package net.mgsx.game.examples.lsystem.model;

public interface Context<T extends Context<T>> {

	public void clone(T clone);
}
