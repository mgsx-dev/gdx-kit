package net.mgsx.core.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;

abstract public class SelectToolBase extends Tool
{

	public SelectToolBase(String name, Camera camera) {
		super(name, camera);
	}
	
	protected <T> void handleSelection(T itemSelected, Array<T> selection)
	{
		if(ctrl()){
			if(selection.contains(itemSelected, true)){
				selection.removeValue(itemSelected, true);
			}else{
				selection.add(itemSelected);
			}
		}else if(shift()){
			if(!selection.contains(itemSelected, true)){
				selection.add(itemSelected);
			}
		}else{
			selection.clear();
			selection.add(itemSelected);
		}
	}

}
