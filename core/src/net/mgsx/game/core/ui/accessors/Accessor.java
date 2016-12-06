package net.mgsx.game.core.ui.accessors;

import net.mgsx.game.core.annotations.Editable;

public interface Accessor
{
	public Object get();
	public void set(Object value);
	public String getName();
	public Class getType();
	public Editable config();
}