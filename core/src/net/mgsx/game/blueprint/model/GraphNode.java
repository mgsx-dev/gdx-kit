package net.mgsx.game.blueprint.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GraphNode {
	public Object object;
	public Vector2 position = new Vector2();
	public Array<Portlet> inlets = new Array<Portlet>();
	public Array<Portlet> outlets = new Array<Portlet>();

	public GraphNode(Object object) {
		this.object = object;
	}
}
