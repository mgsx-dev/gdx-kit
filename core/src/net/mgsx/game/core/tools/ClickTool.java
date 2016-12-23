package net.mgsx.game.core.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;

abstract public class ClickTool extends Tool
{

	public ClickTool(String name, EditorScreen editor) {
		super(name, editor);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT){
			create(snap(unproject(screenX, screenY)));
			return true;
		}
		else if(button == Input.Buttons.RIGHT){
			end();
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}

	abstract protected void create(Vector2 position);
}
