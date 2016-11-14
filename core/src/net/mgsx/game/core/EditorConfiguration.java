package net.mgsx.game.core;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.plugins.Plugin;

public class EditorConfiguration 
{
	public final EditorRegistry registry = new EditorRegistry();

	/**
	 * absolute root path for assets and files.
	 */
	public String root;

	/**
	 * relative path to boot file.
	 */
	public String path;

	/**
	 * Plugin to boot up.
	 */
	final public Array<Plugin> plugins = new Array<Plugin>();
}
