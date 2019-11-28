package net.mgsx.game.blueprint.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GraphNode
{
	public Object object;
	public final Vector2 position = new Vector2();
	public transient Array<Portlet> inlets = new Array<Portlet>();
	public transient Array<Portlet> outlets = new Array<Portlet>();
	public final Color color = new Color(Color.WHITE);

	public GraphNode() {
		// empty for JSON
	}
	public GraphNode(Object object) {
		this.object = object;
	}
	public Link getInput(String name) {
		for(Portlet in : inlets){
			if(in.accessor.getName().equals(name)){
				if(in.inputLinks.size > 0) return in.inputLinks.first();
			}
		}
		return null;
	}
	public Link getOutput(String name) {
		for(Portlet out : outlets){
			if(out.accessor.getName().equals(name)){
				if(out.outLinks.size > 0) return out.outLinks.first();
			}
		}
		return null;
	}
}
