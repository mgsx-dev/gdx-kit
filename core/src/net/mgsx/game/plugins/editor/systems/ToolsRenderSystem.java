package net.mgsx.game.plugins.editor.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.ToolGroup;

public class ToolsRenderSystem extends EntitySystem
{
	@Inject protected EditorSystem editor;
	@Inject protected DebugRenderSystem render;
	
	public ToolsRenderSystem() {
		super(GamePipeline.RENDER_TOOLS);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		editor = engine.getSystem(EditorSystem.class); // TODO remove when inject ok
		
	}

	@Override
	public void update(float deltaTime) {
		
		
		render.editorBatch.begin();
		for(ToolGroup g : editor.tools){
			g.render(render.editorBatch);
		}
		render.editorBatch.end();
		
		for(ToolGroup g : editor.tools){
			g.update(deltaTime);
			g.render(render.shapeRenderer);
		}
		
	}
}
