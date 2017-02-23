package net.mgsx.game.plugins.camera.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.camera.components.ViewportComponent;

public class ViewportTool extends Tool
{
	public ViewportTool(EditorScreen editor) {
		super("Viewport", editor);
	}
	
	@Editable
	public float worldWidth = 1000, worldHeight = 1000;

	@Editable
	public void screenViewport(){
		current().viewport = new ScreenViewport(); // TODO maybe another tool
	}
	@Editable
	public void fitViewport(){
		current().viewport = new FitViewport(worldWidth, worldHeight, null);
	}
	@Editable
	public void fillViewport(){
		current().viewport = new FillViewport(worldWidth, worldHeight, null);
	}
	@Editable
	public void stretchViewport(){
		current().viewport = new StretchViewport(worldWidth, worldHeight, null);
	}
	@Editable
	public void extendedViewport(){
		current().viewport = new ExtendViewport(worldWidth, worldHeight, null); // TODO another tool
	}
	
	private ViewportComponent current(){
		Entity entity = currentEntity();
		ViewportComponent viewport = ViewportComponent.components.get(entity);
		if(viewport == null)
		{
			viewport = getEngine().createComponent(ViewportComponent.class);
			entity.add(viewport);
		}
		return viewport;
	}
}
