package net.mgsx.game.core;

import net.mgsx.game.core.plugins.EditorPlugin;

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
	public EditorPlugin plugin;
}
