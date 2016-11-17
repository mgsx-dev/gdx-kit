package net.mgsx.game.plugins.camera;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.camera.components.CullingComponent;
import net.mgsx.game.plugins.camera.systems.CameraDebugSystem;

@PluginDef(dependencies={CameraPlugin.class})
public class CameraEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.addTool(new Tool("2D Camera", editor) {
			@Override
			protected void activate() {
				
				Entity entity = editor.currentEntity();
				CameraComponent camera = editor.entityEngine.createComponent(CameraComponent.class);
				CullingComponent culling = editor.entityEngine.createComponent(CullingComponent.class);
				
				PerspectiveCamera pc = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				pc.position.set(0, 0, 10);
				pc.up.set(0,1,0);
				pc.lookAt(0,0,0);
				pc.near = 1f;
				pc.far = 3000f;
				pc.update();
				camera.camera = pc;
				
				entity.add(camera);
				entity.add(culling);
				
				end();
			}
		});
		
		editor.entityEngine.addSystem(new CameraDebugSystem(editor));
		
		// disable by default
		editor.entityEngine.getSystem(CameraDebugSystem.class).setProcessing(false);
	}

}
