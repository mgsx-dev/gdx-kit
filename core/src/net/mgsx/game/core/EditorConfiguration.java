package net.mgsx.game.core;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.plugins.Plugin;

public class EditorConfiguration 
{
	public final EditorRegistry registry = new EditorRegistry();

	/**
	 * relative path to boot file (a null mean no boot file).
	 */
	public String path = null;

	/**
	 * Plugin to boot up.
	 */
	final public Array<Plugin> plugins = new Array<Plugin>();

	/**
	 * auto save path : set null to disable auto save.
	 * Auto save is done on application exit (with or without exception)
	 */
	public String autoSavePath = "kit-autosave.json";
}
