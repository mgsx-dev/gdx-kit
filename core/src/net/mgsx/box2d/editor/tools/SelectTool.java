package net.mgsx.box2d.editor.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.box2d.Body;

import net.mgsx.box2d.editor.model.BodyItem;
import net.mgsx.box2d.editor.model.WorldItem;
import net.mgsx.fwk.editor.Tool;

public class SelectTool extends Tool 
{
	private WorldItem worldItem;
	
	public SelectTool(Camera camera, WorldItem worldItem) {
		super("Select", camera);
		this.worldItem = worldItem;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// Body body = worldItem.queryFirstBody(unproject(screenX, screenY), pixelSize().scl(2));
		Body body = worldItem.queryFirstBody(unproject(screenX, screenY));
		if(body != null)
		{
			BodyItem bodyItem = (BodyItem)body.getUserData();
			if(ctrl()){
				if(worldItem.selection.bodies.contains(bodyItem, true)){
					worldItem.selection.bodies.removeValue(bodyItem, true);
				}else{
					worldItem.selection.bodies.add(bodyItem);
				}
			}else if(shift()){
				if(!worldItem.selection.bodies.contains(bodyItem, true)){
					worldItem.selection.bodies.add(bodyItem);
				}
			}else{
				worldItem.selection.bodies.clear();
				worldItem.selection.bodies.add(bodyItem);
			}
			return true;
		}else{
			worldItem.selection.bodies.clear();
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}

}
