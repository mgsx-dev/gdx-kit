package net.mgsx.game.plugins.core.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Storable;

@Storable("core.clearScreen")
@EditableSystem
public class ClearScreenSystem extends EntitySystem
{
	@Editable
	public Color color = new Color(.2f, .2f, .2f, 1);
	
	@Editable public boolean clearColor = true;
	@Editable public boolean clearDepth = true;
	
	
	public ClearScreenSystem() {
		super(GamePipeline.BEFORE_RENDER);
	}
	
	@Override
	public void update(float deltaTime) 
	{
		Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
		int mask = 0;
		if(clearColor) mask |= GL20.GL_COLOR_BUFFER_BIT;
		if(clearDepth) mask |= GL20.GL_DEPTH_BUFFER_BIT;
		
		if(mask != 0) Gdx.gl.glClear(mask);
	}
}
