package net.mgsx.game.plugins.g3d.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class AddModelTool extends Tool
{
	private ModelInstance modelInstance;
	
	public AddModelTool(EditorScreen editor) {
		super("3D Model", editor);
	}
	
	@Override
	protected void activate() {
		
		// TODO open texture region selector if any registered
		
		// else auto open import window
		NativeService.instance.openLoadDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				Model model = editor.loadAssetNow(file.path(), Model.class);
				modelInstance = new ModelInstance(model);
				Entity entity = editor.currentEntity();
				G3DModel data = new G3DModel();
				data.modelInstance = modelInstance;

				// TODO if animations not empty ?
				data.animationController = new AnimationController(modelInstance);
				
				
				entity.add(data);
			}
			@Override
			public boolean match(FileHandle file) {
				return file.extension().equals("g3dj") || file.extension().equals("g3db");
			}
			@Override
			public String description() {
				return "Model files (g3dj, g3db)";
			}
		});
	}
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT && modelInstance != null){ // TODO use click tool
			create(unproject(screenX, screenY));
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
	protected void create(Vector2 position) {
		modelInstance.transform.translate(position.x, position.y, 0);
		// nothing ...
		modelInstance = null;
		end();
	}
}
