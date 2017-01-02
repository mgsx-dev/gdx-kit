package net.mgsx.game.plugins.camera.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.camera.components.CameraComponent;

public class Camera2DTool extends Tool {
	public Camera2DTool(EditorScreen editor) {
		super("2D Camera", editor);
	}

	@Override
	protected void activate() {
		
		Entity entity = editor.currentEntity();
		CameraComponent camera = editor.entityEngine.createComponent(CameraComponent.class);
		
		PerspectiveCamera pc = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		pc.position.set(0, 0, 10);
		pc.up.set(0,1,0);
		pc.lookAt(0,0,0);
		pc.near = 1f;
		pc.far = 3000f;
		pc.update();
		camera.camera = pc;
		
		entity.add(camera);
		
		end();
	}
}