package net.mgsx.plugins.g3d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.core.Editor;
import net.mgsx.core.NativeService;
import net.mgsx.core.NativeService.DialogCallback;
import net.mgsx.core.plugins.Movable;
import net.mgsx.core.tools.Tool;

public class AddModelTool extends Tool
{
	private ModelInstance modelInstance;
	
	public AddModelTool(Editor editor) {
		super("3D Model", editor);
	}
	
	@Override
	protected void activate() {
		
		// TODO open texture region selector if any registered
		
		// else auto open import window
		NativeService.instance.openLoadDialog(new DialogCallback() {
			@Override
			public void selected(FileHandle file) {
				Model model = editor.loadAssetNow(file.path(), Model.class);
				modelInstance = new ModelInstance(model);
				Entity entity = editor.currentEntity();
				G3DModel data = new G3DModel();
				data.modelInstance = modelInstance;
				entity.add(data);
				
				entity.add(new Movable(new ModelMove(modelInstance)));
			}
			@Override
			public void cancel() {
			}
		});
	}
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT){ // TODO use click tool
			create(unproject(screenX, screenY));
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
	protected void create(Vector2 position) {
		
		// nothing ...
		modelInstance = null;
	}
}
