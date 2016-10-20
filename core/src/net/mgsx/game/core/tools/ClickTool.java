package net.mgsx.game.core.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.Editor;

abstract public class ClickTool extends Tool
{

	public ClickTool(String name, Editor editor) {
		super(name, editor);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT){
			create(unproject(screenX, screenY));
			end();
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}

	abstract protected void create(Vector2 position);
}
