package net.mgsx.core.tools;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class ToolBase extends InputAdapter // TODO name should be ... tool ...
{
	ToolGroup group;
	
	public Family activator;
	
	final protected void end(){
		group.end(this);
	}

	protected void activate(){}
	protected void desactivate(){}
	
	protected final boolean ctrl(){
		return Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
				Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);

	}
	protected final boolean shift(){
		return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
				Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

	}

}
