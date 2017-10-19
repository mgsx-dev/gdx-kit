package net.mgsx.game.blueprint;

import com.badlogic.gdx.scenes.scene2d.Actor;

import net.mgsx.game.core.ui.accessors.Accessor;

public class Portlet {

	public Inlet inlet;
	public Outlet outlet;
	public Actor actor;
	public Accessor accessor;
	public Object object;

	public Portlet(Object object, Accessor accessor, Inlet inlet) 
	{
		this.accessor = accessor;
		this.inlet = inlet;
	}

	public Portlet(Object object, Accessor accessor, Outlet outlet) {
		this.accessor = accessor;
		this.outlet = outlet;
	}

	public String getName() {
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
