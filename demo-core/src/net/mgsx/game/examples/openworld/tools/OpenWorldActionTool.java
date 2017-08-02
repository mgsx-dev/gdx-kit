package net.mgsx.game.examples.openworld.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.collision.Ray;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;
import net.mgsx.game.examples.openworld.components.TreesComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.examples.openworld.model.OpenWorldModel;
import net.mgsx.game.examples.openworld.systems.OpenWorldHUDSystem;
import net.mgsx.game.examples.openworld.systems.UserObjectSystem;
import net.mgsx.game.examples.openworld.systems.UserObjectSystem.UserObject;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;

@Editable
public class OpenWorldActionTool extends Tool
{
	public static enum Action{
		NONE, LOOK, GRAB, BUILD, TOOL
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
			
			// find which object as been picked :
			String elementName = null;
			Entity e = null;
			UserObject uo = null;
			
			if(o instanceof Entity){
				e = (Entity)o;
				ObjectMeshComponent omc = ObjectMeshComponent.components.get(e);
				if(omc != null){
					if(omc.userObject != null){
						uo = omc.userObject;
						elementName = omc.userObject.element.type;
					}
				}
			}
			else if(o instanceof TreesComponent){
				elementName = "tree";
				
			}
			
			if(elementName == null) return false;
			
			// select appropriate action
			if(action == Action.TOOL){
				final String toolName = "machete"; // TODO dynamic
				
				OpenWorldElement element = OpenWorldModel.useTool(toolName, elementName);
				if(element != null){
					element.position.set(rayResult.origin);
					userObject.appendObject(element);
				}else{
					// nothing as been generated ...
				}
			}
			else if(action == Action.GRAB){
				// try to grab element
				if(e != null){
					// remove it TODO but keep its properties somewhere in order to regenerates !
					if(uo != null) userObject.removeElement(uo);
					getEngine().removeEntity(e);
					// and add it to the player backpack ! if meet conditions (size, ...etc).
					// animate model : lerp to player and inc GUI
					getEngine().getSystem(OpenWorldHUDSystem.class).hudMain.addItemToBackpack(uo.element);
					Gdx.app.log("OW", "grabbed !");
					
				}else{
					Gdx.app.log("OW", "cannot grab : " + elementName);
				}
			}
			// default tool is look at
			else{
				String description = OpenWorldModel.description(elementName);
				Gdx.app.log("OW", "description : " + description);
			}
			
			return true;
		}
		return false;
	}

}
