package net.mgsx.game.core.binding;

import net.mgsx.game.core.ui.accessors.Accessor;

public class Binding {
	public String command;
	public Object listener;
	
	public String target;
	public transient Accessor accessor;
}
