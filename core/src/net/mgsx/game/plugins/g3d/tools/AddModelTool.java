package net.mgsx.game.plugins.g3d.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.NativeService;
import net.mgsx.game.core.NativeService.DialogCallback;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.components.Transform2DComponent;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.g3d.components.G3DModel;

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

				// TODO if animations not empty ?
				data.animationController = new AnimationController(modelInstance);
				
				
				entity.add(data);
				
				if(!Transform2DComponent.components.has(entity)){
					entity.add(editor.entityEngine.createComponent(Transform2DComponent.class));
				}
				
				// TODO not necessary, done in listener
				entity.add(new Movable(new ModelMove(data)));
			}
			@Override
			public void cancel() {
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
