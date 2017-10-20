package net.mgsx.game.blueprint;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.blueprint.annotations.Inlet;
import net.mgsx.game.blueprint.annotations.Outlet;
import net.mgsx.game.core.ui.accessors.Accessor;

public class Portlet {

	public GraphNode node;
	public Inlet inlet;
	public Outlet outlet;
	public Actor actor; // TODO should be a rectangle ... or use a userdata here ?
	public Accessor accessor;
	public Array<Link> inputLinks = new Array<Link>();
	public Array<Link> outLinks = new Array<Link>();

	public Portlet(GraphNode node, Accessor accessor, Inlet inlet) 
	{
		this.node = node;
		this.accessor = accessor;
		this.inlet = inlet;
	}

	public Portlet(GraphNode node, Accessor accessor, Outlet outlet) {
		this.node = node;
		this.accessor = accessor;
		this.outlet = outlet;
	}

	public String getName() {
		if(inlet != null && !inlet.value().isEmpty()) return inlet.value();
		if(outlet != null && !outlet.value().isEmpty()) return outlet.value();
		return accessor.getName();
	}

	public void copyFrom(Portlet src) 
	{
		if(accessor.getType().isAssignableFrom(src.accessor.getType())){
			accessor.set(src.accessor.get());
		}else{
			// TODO other converters ... color to/from float ...
			float value;
			if(src.accessor.getType() == float.class){
				value = src.accessor.get(Float.class);
			}else if(src.accessor.getType() == int.class){
				value = src.accessor.get(Integer.class);
			}else if(src.accessor.getType() == boolean.class){
				value = src.accessor.get(Boolean.class) ? 1 : 0;
			}else{
				return;
			}
			if(accessor.getType() == boolean.class){
				accessor.set(value != 0);
			}else if(accessor.getType() == int.class){
				accessor.set((int)value);
			}else{
				return;
			}
			
			
		}
		
	}

}
