package net.mgsx.game.plugins.camera.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.helpers.systems.TransactionSystem;

// FIXME this system causing bugs on camera sync, when not added (or not processing at start), it
// will prevent camera sync for rendering !
public class CameraCullingDebugSystem extends TransactionSystem
{
	private EditorScreen editor;
	private Camera backup;
	
	public CameraCullingDebugSystem(EditorScreen editor) {
		super(GamePipeline.BEFORE_RENDER + 2, new AfterSystem(GamePipeline.LAST){});
		this.editor = editor;
	}

	@Override
	protected boolean updateBefore(float deltaTime) 
	{
		if(editor.getEditorCamera().isActive()){
			editor.stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			backup = editor.game.camera;
			editor.game.camera = editor.getEditorCamera().camera();
			return true;
		}
		return false;
	}

	@Override
	protected void updateAfter(float deltaTime) {
		// set editor camera
		editor.game.camera = backup;
		backup = null;

	}

	
}
