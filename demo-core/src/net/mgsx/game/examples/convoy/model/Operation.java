package net.mgsx.game.examples.convoy.model;

public interface Operation{
	public boolean allowed();
	public void commit();
}