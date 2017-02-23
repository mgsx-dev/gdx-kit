package net.mgsx.game.plugins.camera.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;

@Storable(value="camera", auto=true)
@EditableComponent(autoTool=false, autoClone=true)
public class CameraComponent implements Component, Duplicable
{
	public static final ComponentMapper<CameraComponent> components = ComponentMapper.getFor(CameraComponent.class);
	
	@Storable
	@Editable public Camera camera;
	public boolean frustumDirty;
	
	@Override
	public Component duplicate(Engine engine) {
		CameraComponent clone = engine.createComponent(CameraComponent.class);
		clone.frustumDirty = true;
		if(camera instanceof OrthographicCamera){
			OrthographicCamera camClone = new OrthographicCamera();
			camClone.zoom = ((OrthographicCamera) camera).zoom;
			clone.camera = camClone;
		}else if(camera instanceof PerspectiveCamera){
			PerspectiveCamera camClone = new PerspectiveCamera();
			camClone.fieldOfView = ((PerspectiveCamera) camera).fieldOfView;
			clone.camera = camClone;
		}else{
			Gdx.app.error("graphics", "warning : camera not supported for duplication : " + camera.getClass().getName());
		}
		clone.camera.position.set(camera.position);
		clone.camera.direction.set(camera.direction);
		clone.camera.up.set(camera.up);
		clone.camera.near = camera.near;
		clone.camera.far = camera.far;
		clone.camera.viewportWidth = camera.viewportWidth;
		clone.camera.viewportHeight = camera.viewportHeight;
		return clone;
	}
}
