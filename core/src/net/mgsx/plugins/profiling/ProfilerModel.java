package net.mgsx.plugins.profiling;

public class ProfilerModel {

	/** catch all calls including editor (true), or just entities (false) */
	public boolean all = false;
	
	public int calls;

	public int textureBindings;

	public int drawCalls;

	public int shaderSwitches;

	public int vertexCount;

	public float fps;
}
