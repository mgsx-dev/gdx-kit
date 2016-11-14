package net.mgsx.game.core.ui.accessors;

public interface Accessor
{
	public Object get();
	public void set(Object value);
	public String getName();
	public Class getType();
}