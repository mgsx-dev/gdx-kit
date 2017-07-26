package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.core.components.Locked;

public class LockTool extends Tool
{

	public LockTool(EditorScreen editor) {
		super("Lock/Unlock", editor);
	}
	
	@Override
	public boolean keyDown(int keycode) 
	{
		// TODO conflicts with LearnTool (Ctrl+Shift+L) !
		if(ctrl() && keycode == Input.Keys.L)
		{
			if(shift()){
				for(Entity entity : selection().selection){
					entity.remove(Locked.class);
				}
			}else{
				for(Entity entity : selection().selection){
					entity.add(getEngine().createComponent(Locked.class));
				}
			}
			
			return true;
		}
		return super.keyDown(keycode);
	}

}
