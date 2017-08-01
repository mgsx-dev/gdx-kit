package net.mgsx.game.examples.openworld.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.collision.Ray;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.openworld.components.TreesComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.examples.openworld.systems.UserObjectSystem;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;

@Editable
public class OpenWorldActionTool extends Tool
{
	public static enum Action{
		NONE, GRAB, AXE // and so on ...
	}
	
	@Editable public Action action;
	
	@Inject BulletWorldSystem bulletWorld;
	@Inject UserObjectSystem userObject;
	
	public OpenWorldActionTool(EditorScreen editor) {
		super("Action Tool", editor);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		if(button != Input.Buttons.LEFT) return false;
		
		Ray ray = camera().getPickRay(screenX, screenY);
		ray.direction.scl(camera().far);
		Ray rayResult = new Ray();
		Object o = bulletWorld.rayCastObject(ray, rayResult);
		if(o != null){
			
			// Game logic : depending on action and item, could produce element or any other things ...
			if(o instanceof TreesComponent){
				if(action == Action.AXE){
					// TODO create some wood
					OpenWorldElement element = new OpenWorldElement();
					element.color = new Color().set(Color.BROWN);
					element.dynamic = true;
					element.geo_x = element.geo_y = 1;
					element.position.set(rayResult.origin);
					element.size = .1f;
					element.type = "box";
					userObject.appendObject(element);
				}else{
					// TODO display info to the player
					Gdx.app.log("OW", "no actions for object " + o.toString());
				}
			}
			
			return true;
		}
		return false;
	}

}
